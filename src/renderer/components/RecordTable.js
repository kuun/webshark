import React from 'react';
import {connect} from 'react-redux';
import { Table } from 'antd';

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
  }


  render() {
    return (
      <Table
        columns={this.props.columns}
        dataSource={this.props.records}
        rowKey="id"
        bordered
        size="small"
        scroll={this.props.tableSize}
        pagination={this.pagination}
      />
    );
  }
}

const mapStateToProps = state => {
  const windowSize = state.windowSize;
  const tableSize = {
    x: 0,
    y: windowSize.height - 100
  };
  return {
    columns: state.recordTable.columns,
    records: state.recordTable.records,
    tableSize
  }
};

export default connect(mapStateToProps)(RecordTable);
