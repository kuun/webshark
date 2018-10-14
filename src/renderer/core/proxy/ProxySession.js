// @flow
import http from 'http';

export default class ProxySession {
  req: http.IncomingMessage;
  res: http.ServerResponse;

  constructor(req: http.IncomingMessage, res: http.ServerResponse) {
    this.req = req;
    this.res = res;
  }

  handle() {
    this.req
  }
}
