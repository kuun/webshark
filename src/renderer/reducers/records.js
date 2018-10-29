import * as ActionTypes from '../constants/ActionTypes';

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

export default records;

