import React from 'react';
import {RecordTable} from "./RecordTable";


describe('RecordTable', () => {
  let table: RecordTable;

  beforeEach(() => {
    let props = {
      records: [
        {},{}, {}, {}
      ],
      tableSize: {
        x: 800,
        y: 600,
      },
      rowCount: 4
    };

    table = new RecordTable(props);
    table.state = {
      selectedIndex: 0
    }
  });

  describe('rowClassName', () => {
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
    });
  });

  describe('getContentType', () => {

    test('handle null or undefined', () => {
      expect(table.getContentType({})).toBeUndefined();
    });

    test('should be undefined if no response headers', () => {
      expect(table.getContentType({
        rowData: {}
      })).toBeUndefined();
    });

    test('should be undefined if no "content-type" in response headers', () => {
      expect(table.getContentType({
        rowData: {
          resHeaders: {}
        }
      })).toBeUndefined();
    });

    test('should get correct content type if there is only media type', () => {
      expect(table.getContentType({
        rowData: {
          resHeaders: {
            'content-type': 'text/html'
          }
        }
      })).toBe('text/html');
    });

    test('should get correct content type if there is charset', () => {
      expect(table.getContentType({
        rowData: {
          resHeaders: {
            'content-type': 'text/html; charset=utf-8'
          }
        }
      })).toBe('text/html');
    });

    test('should get correct content type if there is boundary', () => {
      expect(table.getContentType({
        rowData: {
          resHeaders: {
            'content-type': 'multipart/form-data; boundary=---------------------------974767299852498929531610575'
          }
        }
      })).toBe('multipart/form-data');
    });
  });

});
