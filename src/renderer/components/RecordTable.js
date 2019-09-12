import React from 'react';
import { AgGridReact } from 'ag-grid-react';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-balham.css';
import _ from 'lodash';
import RecordStore from '../stores/RecordStore';
import {autorun, observer} from 'mobx-react';


export class RecordTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      defaultColDef: {
        resizable: true,
        filter: 'agTextColumnFilter',
      },
      columnDefs: [
        {
          headerName: 'ID',
          field: 'id',
          width: 60,
        },
        {
          headerName: 'Method',
          field: 'method',
          width: 90,
        },
        {
          headerName: 'URL',
          field: 'url',
          width: 400,
        }, {
          headerName: 'Code',
          field: 'statusCode',
          width: 80,
        }, {
          headerName: 'Content Type',
          field: 'resHeaders',
          width: 200,
          valueGetter: this.getContentType,
        },
      ],
    }
  }

  onRowSelected = (event) => {
    if (event.node.selected) {
      RecordStore.setSelectedRecord(event.data);
    }
  };

  getContentType = ({ data }) => {
    if (!data) return undefined;
    let columnData = data.resHeaders;
    let contentType = columnData ? columnData['content-type'] : undefined;
    if (!contentType) return undefined;
    let commaIndex = contentType.indexOf(';');
    if (commaIndex === -1) {
      return contentType;
    } else {
      return contentType.substring(0, commaIndex);
    }
  };

  getRowNodeId = (data) => {
    return data.id;
  }

  onGridReady = (params) => {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;

    autorun(() => {
      let currentRecords = this.gridApi.getModel();
      if (RecordStore.records.length > currentRecords.length) {
        this.gridApi.updateRowData({add: _.slice(RecordStore.records, currentRecords.length)});
      }
    }, { delay: 300 });
  };

  render() {
    return (
      <div
        className="ag-theme-balham"
        style={{
          height: this.props.height,
          width: this.props.width
        }}
        >
        <AgGridReact
          columnDefs={this.state.columnDefs}
          defaultColDef={this.state.defaultColDef}
          rowData={[]}
          getRowNodeId={this.getRowNodeId}
          onGridReady={this.onGridReady}
          rowSelection="single"
          onRowSelected={this.onRowSelected}
          >
        </AgGridReact>
      </div>
    );
  }

}

export default observer(RecordTable);
