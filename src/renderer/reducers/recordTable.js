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

const selectedRecord = (state = null, action) => {
  switch (action.type) {
    case ActionTypes.RECORD_TABLE_SELECT_RECORD:
      if (action.record === state) {
        return state;
      }
      state = action.record;
      return state;
    default:
      return state;
  }
};

const recordTable = combineReducers({
  records,
  selectedRecord
});

export default recordTable;
