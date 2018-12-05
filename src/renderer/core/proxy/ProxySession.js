// @flow
import http from 'http';
import https from 'https';
import _ from 'lodash';
import HttpRecord from './HttpRecord';
import store from '../../reducers';
import {addRecord} from "../../actions";

export default class ProxySession {
  // the request is created by proxy server, represents the request from a client.
  proxyReq: http.IncomingMessage;
  // the response is created by proxy server, represents the response to a client.
  proxyRes: http.ServerResponse;
  // the request is created by http client, represents the request to a target server.
  targetReq: http.ClientRequest;
  // the response is created by http client, represents the response from a target server.
  targetRes: http.IncomingMessage;
  isHttps: boolean;
  record: HttpRecord;

  constructor(proxyReq: http.IncomingMessage, proxyRes: http.ServerResponse) {
    this.proxyReq = proxyReq;
    this.proxyRes = proxyRes;
    if (proxyReq.client.ssl) {
      this.isHttps = true;
    }
    this.record = new HttpRecord();
    this.recordRequestHeader();
  }

  forward() {
    this.targetReq = this.createTargetReq();
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
      // console.log('proxy request end');
      this.targetReq.end();
    });
    this.proxyReq.on('error', (err) => {
      console.log('get error while handling proxy request: ' + err);
      this.close();
    });
    this.proxyRes.on('error', (err) => {
      console.log('get error while handling proxy response: ' + err);
      this.close();
    });

    this.targetReq.on('response', (res: http.IncomingMessage) => {
      this.targetRes = res;
      this.recordResponseHeader();
      this.copyResponseHeader();
      this.proxyReq.resume();
      this.targetRes.on('readable', () => this.forwardResponse());
      this.targetRes.on('end', () => {
        // console.log('target response end');
        this.proxyRes.end();
        this.record.completeRecord();
        store.dispatch(addRecord(this.record));
      });
      this.targetRes.on('error', (err) => {
        console.log('get error while handling target response: ' + err);
        this.close();
      })
    });
    this.targetReq.on('error', (err) => {
      // TODO: write 502 to client.
      console.log('get error while handling target request: ' + err + ', target request: ', this.targetReq);
      this.close();
    });
  }

  // create http/https request to target server.
  createTargetReq () {
    if (this.isHttps) {
      return https.request({
        host: this.proxyReq.headers['host'],
        path: this.proxyReq.url,
        method: this.proxyReq.method
      });
    } else {
      return http.request(this.proxyReq.url);
    }
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
    if (this.isHttps) {
      this.record.url = 'https://' + this.proxyReq.headers['host'] + this.proxyReq.url;
    } else {
      this.record.url = this.proxyReq.url;
    }
  }

  recordResponseHeader() {
    this.record.statusCode = this.targetRes.statusCode;
    this.record.statusMessage = this.targetRes.statusMessage;
    this.record.resHeaders = this.targetRes.headers;
  }

  copyResponseHeader() {
    this.proxyRes.statusCode = this.targetRes.statusCode;
    this.proxyRes.statusMessage  = this.targetRes.statusMessage;
    _.each(this.targetRes.headers, (value, key) => {
      this.proxyRes.setHeader(key, value);
    });
  }

  close() {
    if (this.proxyReq) {
      this.proxyReq.connection.destroy();
      this.proxyReq = undefined;
    }
    if (this.proxyRes) {
      this.proxyRes.connection.destroy();
      this.proxyRes = undefined;
    }
    if (this.targetReq) {
      this.targetReq.connection.destroy();
      this.targetReq = undefined;
    }
    if (this.targetRes) {
      this.targetRes.connection.destroy();
      this.targetRes = undefined;
    }
  }
}
