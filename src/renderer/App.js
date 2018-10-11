import React from 'react';
import 'antd/dist/antd.css'
import './App.css';
import './components/ProxyStartForm'
import ProxyStartForm from "./components/ProxyStartForm"

class App extends React.Component {
  render() {
    return (
        <div className="App">
          <header className="App-header">
            <ProxyStartForm/>
          </header>
        </div>
    );
  }
}

export default App;
