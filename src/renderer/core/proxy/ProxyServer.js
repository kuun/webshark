// @flow
import http from 'http';
import https from 'https';
import net from 'net';
import os from 'os';
import fs from 'fs';
import ProxySession from './ProxySession';


export default class ProxyServer {
  laddr: string;
  lport: number;
  httpServer: http.Server;
  httpsServer: https.Server;
  httpsIpc: string;

  constructor (laddr: string, lport: string, keyFile: string, certFile: string) {
    this.laddr = laddr;
    this.lport = lport;
    this.httpServer = http.createServer();
    this.randomNum = Math.floor(Math.random() * Math.floor(100000));
    if (os.platform() === 'win32') {
      this.httpsIpc = '\\\\.\\pipe\\' + 'webshark' +  this.randomNum;
    } else {
      this.httpsIpc = '/tmp/webshark-' + this.randomNum + '.sock';
    }
    let key: string = this.readFile(keyFile);
    let cert: string = this.readFile(certFile);
    this.httpsServer = https.createServer({
      key,
      cert
    })
  }

  onRequest = (req: http.IncomingMessage, res: http.ServerResponse) => {
    // console.log("req: ", req, ", res: ", res);
    let session = new ProxySession(req, res);
    session.forward();
  };

  onConnect = (req: http.IncomingMessage, sock: net.Socket) => {
    let httpsSock: net.Socket = net.connect({
      path: this.httpsIpc
    }, () => {
      sock.write('HTTP/1.1 200 Connection Established\r\n\r\n');
      // pipe client connection to https proxy server.
      sock.pipe(httpsSock);
      httpsSock.pipe(sock);
    });
    httpsSock.on('error', (e) => {
      console.log('can not connect to https inspect server, error: ' + e);
      sock.write('HTTP/1.1 502 Bad Gateway\r\n\r\n');
      sock.close();
      httpsSock.close();
    })
  };

  start () {
    return Promise.all([this.startHttpServer(), this.startHttpsServer()]);
  }

  startHttpServer () {
    return new Promise((resolve, reject) => {
      this.httpServer.on('error', (e) => {
        reject(e);
      });
      this.httpServer.on('listening', () => {
        resolve(undefined);
      });
      this.httpServer.listen({
        host: this.laddr,
        port: this.lport
      });
      this.httpServer.on('request', this.onRequest);
      this.httpServer.on('connect', this.onConnect);
    });
  }

  startHttpsServer () {
    return new Promise((resolve, reject) => {
      this.httpsServer.on('error', (e) => {
        reject(e);
      });
      this.httpsServer.on('listening', () => {
        resolve(undefined);
      });
      this.httpsServer.listen({
        path: this.httpsIpc
      });
      this.httpsServer.on('request', this.onRequest);
    });
  }

  stop () {
    this.httpServer.close();
    this.httpsServer.close();
  }

  readFile(fileName: string) :string {
    return fs.readFileSync(fileName);
  }
}
