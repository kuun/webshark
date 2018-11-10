import React from 'react';
import {connect} from 'react-redux';
import { Collapse } from 'antd';
import HttpRecord from "../core/proxy/HttpRecord";
import _ from 'lodash';
import'./RecordPage.css';

const Panel = Collapse.Panel;

class HeaderDetail extends React.Component {
  renderRow(header, index) {
    return (
      <div key={index} className="detail-wrap">
        <span style={{fontWeight: "bold"}}>{header.name}:</span>&nbsp;<span>{header.value}</span>
      </div>
    )
  }

  renderRows() {
    const rows = [];
    _.each(this.props.headers, (header, index) => {
      const row = this.renderRow(header, index);
      rows.push(row);
    });
    return rows;
  }

  render() {
    return (
      <div>
        {this.renderRows()}
      </div>
    );
  }
}

class RecordDetails extends React.Component {
  render() {
    return (
      <Collapse bordered={false} defaultActiveKey={['1', '2', '3']}>
        <Panel header="General" key="1">
          <HeaderDetail headers={this.props.generalHeaders}/>
        </Panel>
        <Panel header="Response Headers" key="2">
          <HeaderDetail headers={this.props.responseHeaders}/>
        </Panel>
        <Panel header="Request Headers" key="3">
          <HeaderDetail headers={this.props.requestHeaders}/>
        </Panel>
      </Collapse>
    );
  }
}

const mapStateToProps = (state) => {
  const record: HttpRecord = state.recordTable.selectedRecord.record;
  let generalHeaders = [];
  let requestHeaders = [];
  let responseHeaders = [];

  if (record) {
    generalHeaders.push({name: 'Request URL', value: record.url});
    generalHeaders.push({name: 'Request Method', value: record.method});
    generalHeaders.push({name: 'Status Code', value: record.statusCode});

    _.each(record.reqHeaders, (value, name) => {
      requestHeaders.push({name, value})
    });

    _.each(record.resHeaders, (value, name) => {
      responseHeaders.push({name, value});
    });
  }

  return {
    generalHeaders,
    requestHeaders,
    responseHeaders
  }
};

export default connect(mapStateToProps)(RecordDetails);
