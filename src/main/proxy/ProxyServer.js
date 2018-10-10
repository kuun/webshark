import http from 'http'

export default class ProxyServer {
  laddr: string;
  lport: int;
  server: http.Server;

  constructor (laddr: string, lport: string) {
    this.laddr = laddr;
    this.lport = lport;
    this.server = http.createServer();
  }

  start (): boolean {
    this.server.on('error', (e) => {
      console.log("error: ", e);
    })
    this.server.listen({
      host: this.laddr,
      port: this.lport
    }, this.onRequest);

  }

  onRequest(req: http.IncomingMessage, res: http.ServerResponse) {

  }
}