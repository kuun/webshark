import React from 'react';
import {withRouter} from 'react-router';
import './ProxyStartForm.css';
import ProxyServer from '../core/proxy/ProxyServer';
import {Button, FileInput, FormGroup, InputGroup, NumericInput} from "@blueprintjs/core";
import {AppToaster} from "./AppToaster";
import {Intent} from "@blueprintjs/core/lib/cjs/common/intent";

class ProxyStartForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      addr: '127.0.0.1',
      port: 8000,
      keyFile: '',
      certFile: '',
    };
  }

  handleInputChange = (event) => {
    const target = event.target;
    const name = target.name;
    const value = target.value;
    this.setState({[name]: value});
  };

  handleFileInputChange = (event) => {
    const target = event.target;
    const files = target.files;
    const name = target.name;
    if (!files || files.length === 0) {
      return;
    }
    this.setState({[name]: files[0].path});
  };

  handleClick = () => {
    const {addr, port, keyFile, certFile} = this.state;
    let proxyServer = new ProxyServer(addr, port, keyFile, certFile);
    proxyServer.start().then(() => {
      AppToaster.show({intent: Intent.SUCCESS, timeout: 2000, message: `Proxy server is started on: ${addr}:${port}`});
      this.props.history.push('/recordPage');
    }).catch((err) => {
      let msg;
      console.error("error:", err);
      if (err.code === 'EADDRINUSE') {
        msg = 'Can not start proxy, port is used';
      } else {
        msg = 'Can not start proxy, error: ' + err;
      }
      AppToaster.show({intent: Intent.DANGER, message: msg});
      proxyServer.stop();
    });

  };


  render() {
    const {addr, port, keyFile, certFile} = this.state;
    return (
      <div id="proxyStartForm">
        <FormGroup label="Proxy Address" labelInfo="*" inline>
          <InputGroup placeholder="eg: localhost" name="addr" value={addr} onChange={this.handleInputChange}/>
        </FormGroup>
        <FormGroup label="Proxy Port" labelInfo="*" inline>
          <NumericInput max={65534} min={1} name="port" value={port} buttonPosition="none" onChange={this.handleInputChange}/>
        </FormGroup>
        <FormGroup label="HTTPS key" labelFor="keyFile" labelInfo="*" inline>
          <FileInput text={keyFile ? keyFile : 'Choose key file...'}
                     inputProps={{name: "keyFile"}}
                     onInputChange={this.handleFileInputChange}/>
        </FormGroup>
        <FormGroup label="HTTPS certificate" labelFor="certFile" labelInfo="*" inline>
          <FileInput text={certFile ? certFile : 'Choose certificate file...'}
                     inputProps={{name: "certFile"}}
                     onInputChange={this.handleFileInputChange}/>
        </FormGroup>
        <Button rightIcon="arrow-right" text="Start Proxy Server" onClick={this.handleClick} />
      </div>
    );
  }
}

export default withRouter(ProxyStartForm);
