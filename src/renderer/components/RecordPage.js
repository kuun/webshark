import React from 'react';
import {withRouter} from 'react-router';
import RecordTable from './RecordTable';
import './RecordPage.css';
import {connect} from "react-redux";
import DetailTabs from "./DetailTabs";

class RecordPage extends React.Component {
  render() {
    const {width, height} = this.props.windowSize;
    const leftWidth = 400;
    const rightWidth = width - leftWidth - 15;

    return (
      <div style={{width: '100%', height: '100%'}}>
        <div className="rightPanel" style={{width: rightWidth, height: '100%'}}>
          <RecordTable width={rightWidth - 10} height={height}/>
        </div>
        <div className="leftPanel" style={{width: leftWidth, height: '100%'}}>
          <DetailTabs/>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  const windowSize = state.windowSize;
  return {
    windowSize
  }
};

export default withRouter(connect(mapStateToProps)(RecordPage));
