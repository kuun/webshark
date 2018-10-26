import React from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Button, InputNumber, notification } from 'antd';
import './ProxyStartForm.css';
import ProxyServer from '../core/proxy/ProxyServer';

const FormItem = Form.Item;

class ProxyStartForm extends React.Component {
  constructor(props) {
    super(props);
  }

  handleClick(e) {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      // TODO: try to start proxy server.
      let addr = values.addr;
      let port = values.port;
      let proxyServer = new ProxyServer(addr, port);
      proxyServer.start().then((err) => {
        if (err !== undefined) {
          let msg;
          console.error("error:", err);
          if (err.code === 'EADDRINUSE') {
            msg = 'Can not start proxy, port is used';
          } else {
            msg = 'Can not start proxy, error: ' + err;
          }
          notification['error']({message: msg});
        } else {
          notification['info']({message: `Proxy server is started on: ${addr}:${port}`});
          this.props.onStart(addr, port);
        }
      });
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;

    const formItemLayout = {
      labelCol: {
        sm: { span: 8 },
      },
      wrapperCol: {
        sm: { span: 16 },
      },
    };

    const tailFormItemLayout = {
      wrapperCol: {
        sm: {
          span: 1,
          offset: 8,
        },
      },
    };

    return (
      <Form className="proxy-start-form">
        <FormItem
          {...formItemLayout}
          label="Proxy Address"
        >
          {getFieldDecorator('addr', {
            initialValue: this.props.addr,
            rules: [{ required: true, message: 'Please input proxy address!' }],
          })(
            <Input placeholder="eg: localhost" />
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label="Proxy Port">
          {getFieldDecorator('port', {
            initialValue: this.props.port,
            rules: [{ required: true, message: 'Please input proxy port!' }],
          })(
            <InputNumber min={1} max={65534} className="proxy-port-input"/>
          )}
        </FormItem>
        <FormItem  {...tailFormItemLayout}>
          <Button type="primary" onClick={(e) => this.handleClick(e)}>
            Start Proxy
          </Button>
        </FormItem>
      </Form>
    );
  }
}

ProxyStartForm.propTypes = {
  onStart: PropTypes.func.isRequired,
  addr: PropTypes.string.isRequired,
  port: PropTypes.number.isRequired
}

export default Form.create()(ProxyStartForm);
