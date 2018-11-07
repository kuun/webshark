import * as ActionTypes from '../constants/ActionTypes';

export const resizeWindow = (width, height) => ({
  type: ActionTypes.RESIZE_WINDOW,
  size: {
    width,
    height
  }
});

export const startProxy = (addr, port) => ({
  type: ActionTypes.START_PROXY,
  addr: addr,
  port: port
});

export const addRecord = (record) => ({
  type: ActionTypes.RECORD_TABLE_ADD,
  record: record
});

export const selectRecrod = (record) => ({
  type: ActionTypes.RECORD_TABLE_SELECT_RECORD,
  record: record
});
