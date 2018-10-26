import React from 'react';
import 'antd/dist/antd.css'
import './App.css';
import './components/ProxyStartForm'
import ProxyStartFormContainer from './containers/ProxyStartFormContainer';

class App extends React.Component {
  render() {
    return (
        <div className="App">
          <header className="App-header">
            <ProxyStartFormContainer/>
          </header>
        </div>
    );
  }
}

export default App;
