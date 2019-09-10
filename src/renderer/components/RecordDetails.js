import React from 'react';
import {Alignment, Button, Collapse} from '@blueprintjs/core';
import _ from 'lodash';
import'./RecordDetails.css';

class HeaderDetail extends React.Component {
  renderRow(header, index) {
    return (
      <div key={index} className="detailWrap">
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
      <div className="detailBlock">
        {this.renderRows()}
      </div>
    );
  }
}

class CollapseHeadersPanel extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      isOpen: true,
    };
  }

  handleTitleClick = () => {
    this.setState({isOpen: !this.state.isOpen});
  };

  getIcon = () => {
    return this.state.isOpen ? 'chevron-down' : 'chevron-up';
  };

  render() {
    const {title, headers} = this.props;
    return (
      <div>
        <Button onClick={this.handleTitleClick}
                icon={this.getIcon()}
                alignText={Alignment.LEFT}
                small={true}
                fill={true}>{title}</Button>
        <Collapse isOpen={this.state.isOpen}>
          <HeaderDetail headers={headers}/>
        </Collapse>
      </div>
    );
  }
}

class RecordDetails extends React.Component {
  constructor(props) {
    super(props);
  }

  getGeneralHeaders() {
    const {record} = this.props;
    let generalHeaders = [];
    if (record) {
      generalHeaders.push({name: 'Request URL', value: record.url});
      generalHeaders.push({name: 'Request Method', value: record.method});
      generalHeaders.push({name: 'Status Code', value: record.statusCode});
    }
    return generalHeaders;
  }

  getReqHeaders() {
    const {record} = this.props;
    let headers = [];
    if (record) {
      _.each(record.reqHeaders, (value, name) => {
        headers.push({name, value})
      });
    }
    return headers;
  }

  getResHeaders() {
    const {record} = this.props;
    let headers = [];
    if (record) {
      _.each(record.resHeaders, (value, name) => {
        headers.push({name, value})
      });
    }
    return headers;
  }

  render() {
    return (
      <div>
        <CollapseHeadersPanel title="General Headers" headers={this.getGeneralHeaders()}/>
        <CollapseHeadersPanel title="Response Headers" headers={this.getResHeaders()}/>
        <CollapseHeadersPanel title="Request Headers" headers={this.getReqHeaders()}/>
      </div>
    );
  }
}

export default RecordDetails;
