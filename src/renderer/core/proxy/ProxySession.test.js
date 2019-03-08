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

  _triger(event: string) {
    let handler = this.handlers[event];
    switch (event) {
      case 'response':
        handler(this._res);
        break;
      default:
        handler();
        break;
    }
  }
}

test('record http session without any error', () => {
  let reqBody: buffer.Buffer = buffer.Buffer.from('request body');
  let resBody: buffer.Buffer = buffer.Buffer.from('response body');

  let fromClientReq: MockHttpMessage = new MockHttpMessage();
  fromClientReq.httpVersionMajor = 1;
  fromClientReq.httpVersionMinor = 1;
  fromClientReq.method = 'get';
  fromClientReq.url = 'http://www.baidu.com/';
  fromClientReq.headers = {
    'host': 'www.baidu.com',
  };
  fromClientReq._readableData = reqBody;

  let toClientRes: MockHttpMessage = new MockHttpMessage();

  let fromServerRes: MockHttpMessage = new MockHttpMessage();
  fromServerRes._readableData = resBody;
  fromServerRes.statusCode = 200;
  fromServerRes.statusMessage = 'Ok';

  let toServerReq: MockHttpMessage = new MockHttpMessage();
  toServerReq._res = fromServerRes;

  http.request.mockImplementation(() => {
    return toServerReq;
  });


  let session: ProxySession = new ProxySession(fromClientReq, toClientRes);
  session.forward();

  toServerReq._triger('response');
  fromClientReq._triger('readable');
  fromServerRes._triger('readable');
  fromServerRes._triger('end');

  let record: HttpRecord = session.record;
  expect(record.id).toBe(1);
  expect(record.majorVersion).toBe(1);
  expect(record.minorVersion).toBe(1);
  expect(record.method).toBe('get');
  expect(record.url).toBe('http://www.baidu.com/');
  expect(record.statusCode).toBe(200);
  expect(record.statusMessage).toBe('Ok');
  expect(record.reqHeaders).toHaveProperty('host', 'www.baidu.com');
  expect(record.reqBody).toContain(reqBody);
  expect(record.resBody).toContain(resBody);
});
