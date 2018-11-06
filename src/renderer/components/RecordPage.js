import React from 'react';
import {withRouter} from 'react-router';
import RecordTable from './RecordTable';
import './RecordPage.css';

class RecordPage extends React.Component {
  render() {
    return (
      <RecordTable className="RecordTable"/>
    );
  }
}

export default withRouter(RecordPage);
