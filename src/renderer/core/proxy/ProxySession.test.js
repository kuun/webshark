import http from 'http';
import buffer from 'buffer';
import ProxySession from "./ProxySession";
import prettyFormat from 'pretty-format';
import HttpRecord from "./HttpRecord";

jest.mock('http');
jest.mock('https');


class MockConnection {
  destroy() {}
}

class MockHttpMessage {
  handlers: {};
  client: {};
  connection: MockConnection;
  statusCode: number;
  statusMessage: string;
  _readableData: buffer.Buffer;
  _res: MockHttpMessage;

  constructor() {
    this.handlers = {};
    this.client = {};
    this.connection = new MockConnection();
  }

  on(event: string, handler) {
    this.handlers[event] = handler;
  }

  read(): buffer.Buffer {
    return this._readableData;
  }

  pause() {}

  resume() {}

  write() {}

  end() {}

  flushHeaders() {}

  setHeader() {}

  _triger(event: string) {
    let handler = this.handlers[event];
    switch (event) {
      case 'response':
        handler(this._res);
        break;
      case 'error':
        handler(arguments[1]);
        break;
      default:
        handler();
        break;
    }
  }
}

let reqBody: buffer.Buffer;
let resBody: buffer.Buffer;
let fromClientReq: MockHttpMessage;
let toClientRes: MockHttpMessage;
let toServerReq: MockHttpMessage;
let fromServerRes: MockHttpMessage;
let session: ProxySession;

const METHOD_GET = 'get';
const METHOD_POST = 'post';

beforeEach(() => {
  // setup request and response.
  reqBody = buffer.Buffer.from('request body');
  resBody = buffer.Buffer.from('response body');

  fromClientReq = new MockHttpMessage();
  fromClientReq.httpVersionMajor = 1;
  fromClientReq.httpVersionMinor = 1;
  fromClientReq.method = METHOD_POST;
  fromClientReq.url = 'http://www.baidu.com/';
  fromClientReq.headers = {
    'host': 'www.baidu.com',
  };
  fromClientReq._readableData = reqBody;

  toClientRes = new MockHttpMessage();

  fromServerRes = new MockHttpMessage();
  fromServerRes._readableData = resBody;
  fromServerRes.statusCode = 200;
  fromServerRes.statusMessage = 'Ok';
  fromServerRes.headers = {
    'server': 'MockServer',
  };

  toServerReq = new MockHttpMessage();
  toServerReq._res = fromServerRes;

  http.request.mockImplementation(() => {
    return toServerReq;
  });

  HttpRecord.resetNextId();
  session = new ProxySession(fromClientReq, toClientRes);
  session.forward();
});

test('record http session without any error', () => {
  toServerReq._triger('response');
  fromClientReq._triger('readable');
  fromServerRes._triger('readable');
  fromServerRes._triger('end');

  let record: HttpRecord = session.record;
  verifyRequestHeader(record);
  verifyResponseHeader(record);
  expect(record.reqBody).toContain(reqBody);
  expect(record.resBody).toContain(resBody);
  expect(record.error).toBeUndefined();
});

test('record http session but receive client error before server responses', () => {
  let err: Error = new Error('client error');
  fromClientReq._triger('error', err);

  verifyHttpSessionWithoutResponse(session, err);
});


test('record http session but receive server error while waiting for response', () => {
  let err: Error = new Error('can not get server response');
  toServerReq._triger('error', err);

  verifyHttpSessionWithoutResponse(session, err);
});

test('record http session but receive server error before reading body from server', () => {
  let err: Error = new Error('can not read response body');
  toServerReq._triger('response');
  toServerReq._triger('error', err);

  let record: HttpRecord = session.record;
  verifyRequestHeader(record);
  verifyResponseHeader(record);
  expect(record.reqBody).not.toContain(reqBody);
  expect(record.resBody).not.toContain(resBody);
  expect(record.error).toBe(err);
});

test('record http session but receive server error while reading body from server', () => {
  let err: Error = new Error('can not read response body');
  toServerReq._triger('response');
  fromServerRes._triger('readable');
  toServerReq._triger('error', err);

  let record: HttpRecord = session.record;
  verifyRequestHeader(record);
  verifyResponseHeader(record);
  expect(record.reqBody).not.toContain(reqBody);
  expect(record.resBody).toContain(resBody);
  expect(record.error).toBe(err);
});

test('record http session but receive server error while writing data to server', () => {
  let err: Error = new Error('can not write request body to server');
  toServerReq._triger('response');
  fromClientReq._triger('readable');
  toServerReq._triger('error', err);

  let record: HttpRecord = session.record;
  verifyRequestHeader(record);
  verifyResponseHeader(record);
  expect(record.reqBody).toContain(reqBody);
  expect(record.resBody).not.toContain(resBody);
  expect(record.error).toBe(err);
});

test('record http session but receive client error while writing data to client', () => {
  let err: Error = new Error('can not write response body to client');
  toServerReq._triger('response');
  fromClientReq._triger('readable');
  fromServerRes._triger('readable');
  toClientRes._triger('error', err);

  let record: HttpRecord = session.record;
  verifyRequestHeader(record);
  verifyResponseHeader(record);
  expect(record.reqBody).toContain(reqBody);
  expect(record.resBody).toContain(resBody);
  expect(record.error).toBe(err);
});

function verifyHttpSessionWithoutResponse(session: ProxySession, err: Error) {
  let record: HttpRecord = session.record;

  verifyRequestHeader(record);
  expect(record.statusCode).toBeUndefined();
  expect(record.statusMessage).toBeUndefined();
  expect(record.resHeaders).toBeUndefined();
  expect(record.reqBody).not.toContain(reqBody);
  expect(record.resBody).not.toContain(resBody);
  expect(record.error).toBe(err);
}

function verifyRequestHeader(record: HttpRecord) {
  expect(record.id).toBe(1);
  expect(record.majorVersion).toBe(1);
  expect(record.minorVersion).toBe(1);
  expect(record.method).toBe(METHOD_POST);
  expect(record.url).toBe('http://www.baidu.com/');
  expect(record.reqHeaders).toHaveProperty('host', 'www.baidu.com');
}

function verifyResponseHeader(record: HttpRecord) {
  expect(record.statusCode).toBe(200);
  expect(record.statusMessage).toBe('Ok');
  expect(record.resHeaders).toHaveProperty('server', 'MockServer');
}
