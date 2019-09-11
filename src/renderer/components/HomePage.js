import React from 'react';
import { withRouter } from 'react-router';
import './HomePage.css';
import ProxyStartForm from './ProxyStartForm';
import * as Space from 'react-spaces';
import { Card, Elevation } from '@blueprintjs/core';

const RecordPage = () => {
  return (
    <Space.Fixed height="100%">
      <Space.Top size="20%"/>
      <Space.Fill>
        <Space.Left size="20%"/>
        <Space.Fill>
          <Card className="proxyFormCard" elevation={Elevation.TWO}>
            <ProxyStartForm/>
          </Card>
        </Space.Fill>
        <Space.Right size="20%"/>
      </Space.Fill>
    </Space.Fixed>
  );
}

export default withRouter(RecordPage);
