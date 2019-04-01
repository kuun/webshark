import React from 'react';
import {RecordTable} from "./RecordTable";


describe('RecordTable', () => {
  let table: RecordTable;

  beforeEach(() => {
    let props = {
      records: [
        {},{}, {}, {}
      ],
      selectedRecord: {
        id: 1
      },
      tableSize: {
        x: 800,
        y: 600,
      },
      rowCount: 4
    };

    table = new RecordTable(props);
  });

  test('row class name should be "selectedRow" if row is selected', () => {
    expect(table.rowClassName({index: 0})).toBe('selectedRow');
  });

  test('row class name should be "headerRow" if row number is less than 0', () => {
    expect(table.rowClassName({index: -1})).toBe('headerRow');
  });

  test('row class name should be "evenRow" if row number is even', () => {
    expect(table.rowClassName({index: 4})).toBe('evenRow');
  })

  test('row class name should be "oddRow" if row number is odd', () => {
    expect(table.rowClassName({index: 3})).toBe('oddRow');
  })

});
