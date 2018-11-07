import * as ActionTypes from '../constants/ActionTypes';
import { combineReducers } from 'redux';


const records = (state = [], action) => {
  switch (action.type) {
    case ActionTypes.RECORD_TABLE_ADD:
      return [
        ...state,
        action.record
      ];
    default:
      return state;
  }
};

const defaultColumns = [{
  title: 'Sequence',
  dataIndex: 'id',
  width: 80
}, {
  title: 'Method',
  dataIndex: 'method',
  width: 80
}, {
  title: 'URL',
  dataIndex: 'url',
  width: 300
}, {
  title: 'Status Code',
  dataIndex: 'statusCode',
  width: 80
}, {
  title: 'Content Type',
  dataIndex: 'resHeaders',
  width: 200,
  render: (data) => {
    if (data) {
      return data['content-type'];
    }
    return undefined;
  }
}];

const columns = (state = defaultColumns, action) => {
  switch (action.type) {
    default:
      return state;
  }
};

const selectedRecord = (state = {selectedRowKeys: [], record: null}, action) => {
  switch (action.type) {
    case ActionTypes.RECORD_TABLE_SELECT_RECORD:
      if (action.record === state.record) {
        return state;
      }
      state = {
        selectedRowKeys: [action.record.id],
        record: action.record
      };
      return state;
    default:
      return state;
  }
};

const recordTable = combineReducers({
  records,
  selectedRecord,
  columns
});

export default recordTable;
