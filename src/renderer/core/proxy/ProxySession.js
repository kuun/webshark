// @flow
import http from 'http';
import _ from 'lodash';
import HttpRecord from './HttpRecord';

export default class ProxySession {
  // the request is created by proxy server, represents the request from a client.
  proxyReq: http.IncomingMessage;
  // the response is created by proxy server, represents the response to a client.
  proxyRes: http.ServerResponse;
  // the request is created by http client, represents the request to a target server.
  targetReq: http.ClientRequest;
  // the response is created by http client, represents the response from a target server.
  targetRes: http.IncomingMessage;
  record: HttpRecord;

  constructor(proxyReq: http.IncomingMessage, proxyRes: http.ServerResponse) {
    this.proxyReq = proxyReq;
    this.proxyRes = proxyRes;
    this.record = new HttpRecord();
    this.recordRequestHeader();
  }

  forward() {
     this.targetReq = http.request(this.proxyReq.url);
    // copy request headers
    _.each(this.proxyReq.headers, (value, key) => {
      if (key !== 'host') {
        this.targetReq.setHeader(key, value);
      }
    });
    this.targetReq.flushHeaders();
    // pause to read client request body.
    this.proxyReq.pause();
    this.proxyReq.on('readable', () => this.forwardRequest());
    this.proxyReq.on('end', () => {
      console.log('proxy request end');
      this.targetReq.end();
    });

    this.targetReq.on('response', (res: http.IncomingMessage) => {
      this.targetRes = res;
      this.recordResponseHeader();
      this.proxyReq.resume();
      this.targetRes.on('readable', () => this.forwardResponse());
      this.targetRes.on('end', () => {
        console.log('target response end');
        this.proxyRes.end();
      });
    });
  }

  // forward http request body from client to server.
  forwardRequest() {
    let data = this.proxyReq.read();
    if (data) {
      this.record.reqBody.push(data);
      this.targetReq.write(data);
    }
  }

  // forward http response body from server to client.
  forwardResponse() {
    let data = this.targetRes.read();
    if (data) {
      this.record.reqBody.push(data);
      this.proxyRes.write(data);
    }
  }

  recordRequestHeader() {
    this.record.majorVersion = this.proxyReq.httpVersionMajor;
    this.record.minorVersion = this.proxyReq.httpVersionMinor;
    this.record.method = this.proxyReq.method;
    this.record.reqHeaders = this.proxyReq.headers;
  }

  recordResponseHeader() {
    this.record.statusCode = this.targetRes.statusCode;
    this.record.statusMessage = this.targetRes.statusMessage;
    this.record.resHeaders = this.targetRes.headers;
  }
}
