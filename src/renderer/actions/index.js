import * as ActionTypes from '../constants/ActionTypes';

export const startProxy = (addr, port) => ({
  type: ActionTypes.START_PROXY,
  addr: addr,
  port: port
});

