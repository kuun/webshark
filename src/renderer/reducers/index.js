import {combineReducers, createStore} from 'redux';
import proxyServer from './proxyServer';
import recordTableColumns from './recordTableColumns';
import records from './records';

const rootReducer = combineReducers({
  proxyServer,
  recordTableColumns,
  records
});

export default createStore(rootReducer);
