import {combineReducers, createStore} from 'redux';
import proxyServer from './proxyServer';
import recordTable from './recordTable';
import windowSize from './windowSize';

const rootReducer = combineReducers({
  proxyServer,
  recordTable,
  windowSize
});

export default createStore(rootReducer);
