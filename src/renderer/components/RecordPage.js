import React from 'react';
import {withRouter} from 'react-router';
import RecordTable from './RecordTable';
import './RecordPage.css';
import RecordDetails from "./RecordDetails";
import { Layout } from 'antd';

const Content = Layout.Content;
const Sider = Layout.Sider;

class RecordPage extends React.Component {
  render() {
    return (
      <Layout>
        <Content>
          <RecordTable/>
        </Content>
        <Sider width={300} theme="light">
          <RecordDetails/>
        </Sider>
      </Layout>
    );
  }
}

export default withRouter(RecordPage);
