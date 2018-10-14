// @flow

export default class HttpRecord {
  method: string;
  url: string;
  majorVersion: number;
  minorVersion: number;
  reqRawHeaders: string[];
  resRawHeaders: string[];
  reqBody: {};
  resBody: {};
}
