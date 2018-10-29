import React from 'react';
import {withRouter} from 'react-router';
import RecordTable from './RecordTable';

class RecordPage extends React.Component {
  render() {
    return (
      <RecordTable/>
    );
  }
}

export default withRouter(RecordPage);
