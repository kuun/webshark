// Initial welcome page. Delete the following line to remove it.
'use strict';
import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import store from './reducers';
import {Provider} from 'react-redux';

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('app')
);
