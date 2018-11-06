import * as ActionTypes from '../constants/ActionTypes';

const defautWindowSize = {
  width: 0,
  height: 0
}

const windowSize = (state = defautWindowSize, action) => {
  switch (action.type) {
    case ActionTypes.RESIZE_WINDOW:
      state = action.size;
      return state;
    default:
      return state;
  }
};

export default windowSize;
