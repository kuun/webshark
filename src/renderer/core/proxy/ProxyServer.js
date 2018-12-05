// @flow
import http from 'http';
import https from 'https';
import net from 'net';
import os from 'os';
import forge from 'node-forge';
import ProxySession from './ProxySession';

export default class ProxyServer {
  laddr: string;
  lport: number;
  httpServer: http.Server;
  httpsServer: https.Server;
  httpsIpc: string;

  constructor (laddr: string, lport: string) {
    this.laddr = laddr;
    this.lport = lport;
    this.httpServer = http.createServer();
    this.randomNum = Math.floor(Math.random() * Math.floor(100000));
    if (os.platform() === 'win32') {
      this.httpsIpc = '\\\\.\\pipe\\' + 'webshark' +  this.randomNum;
    } else {
      this.httpsIpc = '/tmp/webshark-' + this.randomNum + '.sock';
    }
    let certKey = this.buildCertAndKey();
    this.httpsServer = https.createServer({
      key: certKey.key,
      cert: certKey.cert
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

  buildCertAndKey () {
    let pki = forge.pki;

    // generate a keypair and create an X.509v3 certificate
    var keys = pki.rsa.generateKeyPair(2048);
    var cert = pki.createCertificate();
    cert.publicKey = keys.publicKey;
    // alternatively set public key from a csr
    //cert.publicKey = csr.publicKey;
    // NOTE: serialNumber is the hex encoded value of an ASN.1 INTEGER.
    // Conforming CAs should ensure serialNumber is:
    // - no more than 20 octets
    // - non-negative (prefix a '00' if your value starts with a '1' bit)
    cert.serialNumber = '00' + this.randomNum;
    cert.validity.notBefore = new Date();
    cert.validity.notAfter = new Date();
    cert.validity.notAfter.setFullYear(cert.validity.notBefore.getFullYear() + 1);
    var attrs = [{
      name: 'commonName',
      value: 'example.org'
    }, {
      name: 'countryName',
      value: 'US'
    }, {
      shortName: 'ST',
      value: 'Virginia'
    }, {
      name: 'localityName',
      value: 'Blacksburg'
    }, {
      name: 'organizationName',
      value: 'Test'
    }, {
      shortName: 'OU',
      value: 'Test'
    }];
    cert.setSubject(attrs);
    cert.setIssuer(attrs);
    // self-sign certificate
    cert.sign(keys.privateKey);
    // convert a Forge certificate to PEM
    var pemCert = pki.certificateToPem(cert);
    var pemKey = pki.privateKeyToPem(keys.privateKey);

    return {
      key: pemKey,
      cert: pemCert
    }
  }
}
