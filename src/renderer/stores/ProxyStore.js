import {observable, action} from 'mobx';
import * as ProxyStatus from  '../constants/ProxyStatus';

const PorxyStore = observable({
  status: ProxyStatus.STOPPED,
  addr: '127.0.0.1',
  port: 8000,
  keyFile: '',
  certFile: '',
});

PorxyStore.startProxy = action((action) => {
  PorxyStore.status = ProxyStatus.STARTING,
  PorxyStore.addr = action.addr,
  PorxyStore.port = action.port,
  PorxyStore.keyFile = action.keyFile,
  PorxyStore.certFile = action.certFile
});

export default PorxyStore;
