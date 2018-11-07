import React from 'react';
import {connect} from 'react-redux';
import { Table } from 'antd';
import { selectRecrod } from "../actions";

class RecordTable extends React.Component {
  constructor(props) {
    super(props);
    this.pagination = {
      defaultPageSize: 50,
      showTatal: true,
      size: 'small',
      showQuickJumper: true,
      showSizeChanger: true,
      pageSizeOptions: ['10', '20', '50', '100', '200', '500']
    };
    this.onRow = (record) => {
      return {
        onClick: () => {
          this.props.onSelect(record);
        }
      }
    }
  }

  render() {
    const rowSelection = {
      type: 'radio',
      selectedRowKeys: this.props.selectedRowKeys,
      onChange: (selectedRowKeys, selectedRows) => {
        if (selectedRows) {
          if (selectedRows.length > 0) {
            this.props.onSelect(selectedRows[0]);
          } else {
            this.props.onSelect(null);
          }
        }
      }
    };

    return (
      <Table
        columns={this.props.columns}
        dataSource={this.props.records}
        rowKey="id"
        bordered
        size="small"
        scroll={this.props.tableSize}
        pagination={this.pagination}
        rowSelection={rowSelection}
        onRow={this.onRow}
      />
    );
  }
}

const mapStateToProps = state => {
  const {columns, records, selectedRecord} = state.recordTable;
  const selectedRowKeys = selectedRecord.selectedRowKeys;
  const windowSize = state.windowSize;
  const tableSize = {
    x: 0,
    y: windowSize.height - 100
  };
  return {
    columns,
    records,
    selectedRowKeys,
    tableSize
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
