import http from 'http';
import https from 'https';
import net from 'net';
import ProxyServer from "./ProxyServer";
import ProxySession from './ProxySession';

jest.mock('http');
jest.mock('https');
jest.mock('net');
jest.mock('./ProxySession');

export const mockForward = jest.fn();

const Server = jest.fn();

const Socket = jest.fn();

let proxyServer: ProxyServer;

// mock module http and https
beforeAll(() => {
  let createServer = () => {
    let server = new Server();
    let handlers = {};

    server.on = jest.fn().mockImplementation((event: string, handler) => {
      handlers[event] = handler;
    });
    server.listen = jest.fn();

    server.trigger = jest.fn().mockImplementation((event: string, ...args) => {
      let handler = handlers[event];

      if (event) {
        handler(...args)
      }
    });
    return server;
  };
  http.createServer.mockImplementation(createServer);
  https.createServer.mockImplementation(createServer);
});

// mock module ProxySession
beforeAll(() => {
  return ProxySession.mockImplementation(() => {
    return {forward: mockForward};
  })

});

// mock module net
beforeAll(() => {
  net.createSocket = (): Socket => {
    let socket = new Socket();
    let handlers = {};

    socket.on = jest.fn().mockImplementation((event: string, handler) => {
      handlers[event] = handler;
    });
    socket.trigger = jest.fn().mockImplementation((event, ...args) => {
      let handler = handlers[event];
      if (event) {
        handler(...args)
      }
    });
    socket.close = jest.fn();
    socket.pipe = jest.fn();
    socket.write = jest.fn();
    socket.close = jest.fn();
    return socket;
  };
  net.connect.mockImplementation((opts, handler) => {
    net.nextSocket.on('connect', handler);
    return net.nextSocket;
  });
});

// reset mock state
beforeEach(() => {
  proxyServer = new ProxyServer('127.0.0.1', '8000');
  ProxySession.mockClear();
  mockForward.mockClear();
});

test('start http server failed with error', () => {
  let err = 'address is used';
  let promise = proxyServer.startHttpServer();
  proxyServer.httpServer.trigger('error', err);
  return promise.catch((e) => {
    expect(e).toBe(err);
  });
});

test('start http server successfully', () => {
  let promise = proxyServer.startHttpServer();
  proxyServer.httpServer.trigger('listening');
  return promise.then((data) => {
    expect(data).toBeUndefined();
  });
});

test('start https server failed with error', () => {
  let err = 'address is used';
  let promise = proxyServer.startHttpsServer();
  proxyServer.httpsServer.trigger('error', err);
  return promise.catch((e) => {
    expect(e).toBe(err);
  });
});

test('start https server successfully', () => {
  let promise = proxyServer.startHttpsServer();
  proxyServer.httpsServer.trigger('listening');
  return promise.then((data) => {
    expect(data).toBeUndefined();
  });
});

test('handle normal http request', () => {
  proxyServer.start();
  proxyServer.httpServer.trigger('request');
  expect(mockForward).toBeCalled();
});

test('handle http CONNECT method and cannnect to dest server successfully', () => {
  let sockToClient = net.createSocket();
  let sockToServer = net.createSocket();
  net.nextSocket = sockToServer;
  proxyServer.start();
  proxyServer.httpServer.trigger('connect', null, sockToClient);
  sockToServer.trigger('connect');
  expect(sockToClient.write).toBeCalled();
  expect(sockToClient.pipe).toBeCalled();
  expect(sockToServer.pipe).toBeCalled();
});

test('handle http CONNECT method but cannnect to dest server failed', () => {
  let sockToClient = net.createSocket();
  let sockToServer = net.createSocket();
  net.nextSocket = sockToServer;
  proxyServer.start();
  proxyServer.httpServer.trigger('connect', null, sockToClient);
  sockToServer.trigger('error');
  expect(sockToClient.write).toBeCalled();
  expect(sockToClient.close).toBeCalled();
  expect(sockToServer.close).toBeCalled();
});

