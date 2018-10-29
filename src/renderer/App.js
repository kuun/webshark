import React from 'react';
import { BrowserRouter, Route } from 'react-router-dom';
import 'antd/dist/antd.css'
import './App.css';
import './components/ProxyStartForm'
import HomePage from './components/HomePage';
import RecordPage from './components/RecordPage';

class App extends React.Component {
  render() {
    return (
        <BrowserRouter>
        <div>
          <Route exact path="/" component={HomePage} />
          <Route path="/recordPage" component={RecordPage}/>
        </div>
        </BrowserRouter>
    );
  }
}

export default App;
