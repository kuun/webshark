import {observable, action} from 'mobx';

const WindowSizeStore = observable({
  width: 0,
  height: 0,
});

WindowSizeStore.resize = action((width, height) => {
  WindowSizeStore.width = width;
  WindowSizeStore.height = height;
});

export default WindowSizeStore;
