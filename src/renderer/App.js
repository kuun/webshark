import React from 'react';
import { BrowserRouter, Route } from 'react-router-dom';
import 'antd/dist/antd.css'
import './App.css';
import './components/ProxyStartForm'
import ProxyStartFormContainer from './containers/ProxyStartFormContainer';

class App extends React.Component {
  render() {
    return (
        <BrowserRouter>
        <div className="App">
          <header className="App-header">
            <Route exact path="/" component={ProxyStartFormContainer} />
          </header>
        </div>
        </BrowserRouter>
    );
  }
}

export default App;
