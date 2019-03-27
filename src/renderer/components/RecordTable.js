import React from 'react';
import {connect} from 'react-redux';
import { selectRecrod } from "../actions";
import { Column, Table, SortDirection } from 'react-virtualized';
import './RecordTable.css';
import 'react-virtualized/styles.css';


class RecordTable extends React.Component {
  constructor(props) {
    super(props);

    this.onRow = ({rowData}) => {
      this.props.onSelect(rowData);
    };

    this.rowClassName = ({index}) => {
      if (this.props.selectedRecord) {
        if (index === this.props.selectedRecord.id - 1) {
          return 'selectedRow'
        }
      }
      if (index < 0) {
        return 'headerRow';
      } else {
        return index % 2 === 0 ? 'evenRow' : 'oddRow';
      }
    };

    this.getContentType = ({rowData}) => {
      if (!rowData) return undefined;
      let columnData = rowData.resHeaders;
      let contentType = columnData ? columnData['content-type'] : undefined;
      if (!contentType) return undefined;
      let commaIndex = contentType.indexOf(';');
      if (commaIndex === -1) {
        return contentType;
      } else {
        return contentType.substring(0, commaIndex);
      }
    };

  }

  render() {
    const rowGetter = ({index}) => this.props.records[index];

    return (
      <Table
        headerHeight={30}
        width={this.props.tableSize.x}
        height={this.props.tableSize.y}
        className="Table"
        headerClassName="headerColumn"
        rowClassName={this.rowClassName}
        rowHeight={30}
        rowGetter={rowGetter}
        rowCount={this.props.rowCount}
        sortDirection={SortDirection.ASC}
        onRowClick={this.onRow}
        >
        <Column
          label="ID"
          dataKey="id"
          width={60}
        />
        <Column
          label="Method"
          dataKey="method"
          width={90}
        />
        <Column
          width={400}
          label="URL"
          dataKey="url"
          flexGrow={1}
        />
        <Column
          width={210}
          label="Status Code"
          dataKey="statusCode"
        />
        <Column
          width={210}
          label="Content Type"
          dataKey="resHeaders"
          cellDataGetter={this.getContentType}
        />
      </Table>
    );
  }

}

const mapStateToProps = state => {
  const {records, selectedRecord} = state.recordTable;
  const windowSize = state.windowSize;
  const tableSize = {
    x: windowSize.width - 300,
    y: windowSize.height
  };
  const rowCount = records.length;
  return {
    records,
    selectedRecord,
    tableSize,
    rowCount
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSelect: (record) => {
      dispatch(selectRecrod(record));
    }
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(RecordTable);
