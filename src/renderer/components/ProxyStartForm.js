import React from 'react';
import { Form, Input, Button, InputNumber } from 'antd';
import './ProxyStartForm.css'

const FormItem = Form.Item;

class ProxyStartForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      proxyAddr: "127.0.0.1",
      proxyPort: 8000,
    };
  }

  handleClick(e) {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) {
        return;
      }
      console.warn('Received values of form: ', values);
      // TODO: try to start proxy server.
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
      <Form onSubmit={this.handleSubmit} className="proxy-start-form">
        <FormItem
          {...formItemLayout}
          label="Proxy Address"
        >
          {getFieldDecorator('proxyAddr', {
            initialValue: this.state.proxyAddr,
            rules: [{ required: true, message: 'Please input proxy address!' }],
          })(
            <Input placeholder="eg: localhost" />
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          label="Proxy Port">
          {getFieldDecorator('proxyPort', {
            initialValue: this.state.proxyPort,
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

export default Form.create()(ProxyStartForm);
