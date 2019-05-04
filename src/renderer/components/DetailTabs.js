import React from 'react';
import RecordDetails from "./RecordDetails";
import {Button, ButtonGroup} from "@blueprintjs/core";
import './DetailTabs.css';

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

  isShow = (tabId) => {
    let style = {
      display: 'none'
    };
    if (this.state.activeTab === tabId) {
      style.display = 'block';
    }
    return style;
  };

  onClickTab = (tabId) => {
    return () => this.setState({activeTab: tabId})
  };

  render() {
    const {activeTab} = this.state;
    return (
      <div>
        <div>
          <ButtonGroup fill={true}>
            <Button small={true}
                    active={activeTab === TabId.TabHeaders}
                    onClick={this.onClickTab(TabId.TabHeaders)} >Headers</Button>
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
            <RecordDetails></RecordDetails>
          </div>
          <div style={this.isShow(TabId.TabRequestBody)}></div>
          <div style={this.isShow(TabId.TabResponseBody)}></div>
        </div>
      </div>
    );
  }
}

export default DetailTabs;
