import React from 'react';
import {withRouter} from 'react-router';
import './HomePage.css';
import ProxyStartForm from './ProxyStartForm';

class RecordPage extends React.Component {
  render() {
    return (
      <div className="App">
        <ProxyStartForm/>
      </div>
    );
  }
}

export default withRouter(RecordPage);
