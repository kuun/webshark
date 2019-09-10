import React from 'react';
import {withRouter} from 'react-router';
import RecordTable from './RecordTable';
import './RecordPage.css';
import {connect} from 'react-redux';
import DetailTabs from './DetailTabs';
import SplitPane from 'react-split-pane';

class RecordPage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      rightWidth: 400,
    };
  }

  render() {
    const {width, height} = this.props.windowSize;
    // const leftWidth = 400;
    const leftWidth = width - this.state.rightWidth;

    return (
      <SplitPane split="vertical" defaultSize={400} minSize={340} primary="second"
      onChange={this.onChange}>
        <div>
          <RecordTable width={leftWidth} height={height}/>
        </div>
        <div className="detailBlock detailWrap">
          <DetailTabs/>
        </div>
      </SplitPane>
    );
  }

  onChange = (size) => {
    this.setState({
      rightWidth: size,
    });
  }
}

const mapStateToProps = state => {
  const windowSize = state.windowSize;
  return {
    windowSize
  }
};

export default withRouter(connect(mapStateToProps)(RecordPage));
