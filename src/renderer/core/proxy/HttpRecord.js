// @flow
import buffer from 'buffer';

let nextId: number = 1;

export default class HttpRecord {
  id: number;
  method: string;
  url: string;
  statusCode: number;
  statusMessage: string;
  majorVersion: number;
  minorVersion: number;
  reqHeaders: {};
  resHeaders: {};
  reqBody: buffer.Buffer[];
  resBody: buffer.Buffer[];
  error: Error;

  constructor() {
    this.reqBody = [];
    this.resBody = [];
  }

  completeRecord() {
    this.id = HttpRecord.getNextId();
  }

  static getNextId(): number {
    return nextId++;
  }

  static resetNextId() {
    nextId = 1;
  }
}
