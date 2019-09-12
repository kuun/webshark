import {observable, action} from 'mobx';

const RecordStore = observable({
  records: [],
  selectedRecord: null,
});

RecordStore.addRecord = action((record) => {
  RecordStore.records.push(record);
})

RecordStore.setSelectedRecord = action((record) => {
  RecordStore.selectedRecord = record;
});

export default RecordStore;
