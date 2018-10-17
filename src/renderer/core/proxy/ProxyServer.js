// @flow
import http from 'http';
import ProxySession from './ProxySession';

export default class ProxyServer {
  laddr: string;
  lport: number;
  server: http.Server;

  constructor (laddr: string, lport: string) {
    this.laddr = laddr;
    this.lport = lport;
    this.server = http.createServer();
  }

  async start () {
    let promise = new Promise((resolve) => {
      this.server.on('error', (e) => {
        resolve(e);
      });
      this.server.on('listening', () => {
        resolve(undefined);
      });
      this.server.listen({
        host: this.laddr,
        port: this.lport
      });
      this.server.on('request', this.onRequest);
    });
    return await promise;
  }

  stop () {
    this.server.close();
  }

  onRequest(req: http.IncomingMessage, res: http.ServerResponse) {
    console.log("req: ", req, ", res: ", res);
    let session = new ProxySession(req, res);
    session.forward();
  }
}
