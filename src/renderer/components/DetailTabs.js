import React from 'react';
import RecordDetails from './RecordDetails';
import BodyPreview from './BodyPreview';
import {Button, ButtonGroup} from "@blueprintjs/core";
import './DetailTabs.css';
import {connect} from "react-redux";

const TabId = {
  TabHeaders: Symbol('TabHeaders'),
  TabRequestBody: Symbol('TabRequestBody'),
  TabResponseBody: Symbol('TabResponseBody')
};

class DetailTabs extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      activeTab: TabId.TabHeaders
    };
  }

  isShow(tabId) {
    let style = {
      display: 'none'
    };
    if (this.state.activeTab === tabId) {
      style.display = 'block';
    }
    return style;
  }

  onClickTab(tabId) {
    return () => this.setState({activeTab: tabId});
  }

  getBody(fieldName) {
    const {record} = this.props;
    if (!record) {
      return [];
    }
    return record[fieldName];
  }

  getHeaders(fieldName) {
    const {record} = this.props;
    if (!record) {
      return '';
    }
    const headers = record[fieldName];
    if (!headers) {
      return {};
    }
    return headers;
  }

  render() {
    const {activeTab} = this.state;
    const {record} = this.props;
    return (
      <div>
        <div className="tabButton">
          <ButtonGroup>
            <Button small={true}
                    active={activeTab === TabId.TabHeaders}
                    onClick={this.onClickTab(TabId.TabHeaders)}>Headers</Button>
            <Button small={true}
                    active={activeTab === TabId.TabRequestBody}
                    onClick={this.onClickTab(TabId.TabRequestBody)}>Request body</Button>
            <Button small={true}
                    active={activeTab === TabId.TabResponseBody}
                    onClick={this.onClickTab(TabId.TabResponseBody)}>Response body</Button>
          </ButtonGroup>
        </div>
        <div className="tabPanel">
          <div style={this.isShow(TabId.TabHeaders)}>
            <RecordDetails record={record}/>
          </div>
          <div style={this.isShow(TabId.TabResponseBody)} >
            <BodyPreview bodyBuffers={this.getBody('resBody')}
                         headers={this.getHeaders('resHeaders')}/>
          </div>
          <div style={this.isShow(TabId.TabRequestBody)}>
            <BodyPreview bodyBuffers={this.getBody('reqBody')}
                         headers={this.getHeaders('reqHeaders')}/>
          </div>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  const record = state.recordTable.selectedRecord;
  return {
    record
  }
};

export default connect(mapStateToProps)(DetailTabs);
