import * as ActionTypes from '../constants/ActionTypes';
import * as ProxyStatus from  '../constants/ProxyStatus';

const defaultState = {
  status: ProxyStatus.STOPPED,
  addr: '127.0.0.1',
  port: 8000
};

const proxyServer = (state = defaultState, action) => {
  switch (action.type) {
    case ActionTypes.START_PROXY:
      return {
        status: ProxyStatus.STARTING,
        addr: action.addr,
        port: action.port,
        keyFile: action.keyFile,
        certFile: action.certFile
      };
    default:
      return state;
  }
};

export default proxyServer;
