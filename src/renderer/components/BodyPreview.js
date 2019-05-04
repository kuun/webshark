import React from 'react';
import {Buffer} from 'buffer';
import zlib from 'zlib';

class BodyPreview extends React.Component {
  constructor(props) {
    super(props);
  }

  getContent() {
    const {bodyBuffers, headers} = this.props;
    const contentType = headers['content-type'];
    try {
      if (!contentType) {
        throw 'no content type';
      }
      const mediaType = this.getMediaType(contentType);
      const charset = this.getCharset(contentType);
      if (!mediaType) {
        throw 'no media type';
      }
      let buffers = bodyBuffers;
      buffers = this.processTransferEncoding(buffers, headers);
      buffers = this.processContentEncoding(buffers, headers);
      let buff: Buffer = Buffer.concat(buffers);
      return buff.toString(charset);
    } catch (error) {
      console.log('error: ' + error)
    }
  }

  // process transfer-encoding
  processTransferEncoding(buffers: Buffer[], headers) {
    const transferEncoding = headers['transfer-encoding'];
    if (!transferEncoding) {
      return buffers;
    }
    switch(transferEncoding) {
      case 'chuncked':
        throw 'unimplemented transfer encoding';
    }
  }

  processContentEncoding(buffers: Buffer[], headers) {
    const contentEncoding = headers['content-encoding'];
    if (!contentEncoding) {
      return buffers;
    }
    switch(contentEncoding) {
      case 'gzip' || 'x-gzip':
        return this.ungzipContent(buffers);
      case 'defalte':
        return this.inflateContent(buffers);
      default:
        throw 'unsupported content encoding: ' + contentEncoding;
    }
  }

  ungzipContent(buffers: Buffer[]) {
    return zlib.unzipSync(buffers);
  }

  inflateContent(buffers: Buffer[]) {
    return zlib.inflateSync(buffers);
  }

  getMediaType(contentType: string) {
    if (!contentType) {
      return undefined;
    }
    let commaIndex = contentType.indexOf(';');
    if (commaIndex === -1) {
      return contentType;
    } else {
      return contentType.substring(0, commaIndex);
    }
  }

  getCharset(contentType: string) {
    let charset = 'utf-8';
    if (!contentType) {
      return charset;
    }
    let index = contentType.indexOf('charset=');
    if (index !== -1) {
      let commaIndex = contentType.indexOf(';', index);
      if (commaIndex === -1) {
        charset = contentType.substring(index + 8);
      } else {
        charset = contentType.substring(index + 8, commaIndex);
      }
    }
    return charset;
  }


  render() {
    return (
      <div>
        <p>{this.getContent()}</p>
      </div>
    );
  }
}

export default BodyPreview;
