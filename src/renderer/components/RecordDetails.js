import React from 'react';
import {connect} from 'react-redux';
import HttpRecord from "../core/proxy/HttpRecord";
import {Alignment, Button, Collapse} from '@blueprintjs/core';
import _ from 'lodash';
import'./RecordPage.css';

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
        <Button className="collapsePanelTitle"
                onClick={this.handleTitleClick}
                icon={this.getIcon()}
                alignText={Alignment.LEFT}>{title}</Button>
        <Collapse isOpen={this.state.isOpen}>
          <HeaderDetail headers={headers}/>
        </Collapse>
      </div>
    );
  }
}

class RecordDetails extends React.Component {
  render() {
    return (
      <div>
        <CollapseHeadersPanel title="General Headers" headers={this.props.generalHeaders}/>
        <CollapseHeadersPanel title="Response Headers" headers={this.props.responseHeaders}/>
        <CollapseHeadersPanel title="Request Headers" headers={this.props.requestHeaders}/>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  const record: HttpRecord = state.recordTable.selectedRecord;
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
