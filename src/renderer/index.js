// Initial welcome page. Delete the following line to remove it.
'use strict';
import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import store from './reducers';
import {Provider} from 'react-redux';
import * as actions from './actions';


ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('app')
);

window.addEventListener('resize', () => {
  let width = window.innerWidth;
  let height = window.innerHeight;
  store.dispatch(actions.resizeWindow(width, height));
});

// set initialized window size
store.dispatch(actions.resizeWindow(window.innerWidth, window.innerHeight));

if (module.hot) {
  module.hot.accept();
}
