import React from 'react';
import {connect} from 'react-redux';
import { Table } from 'antd';

class RecordTable extends React.Component {
  render() {
    return (
      <Table
        columns={this.props.columns}
        dataSource={this.props.records}
        rowKey="id"
        bordered
        size="small"
      />
    );
  }
}

const mapStateToProps = state => {
  return {
    columns: state.recordTableColumns,
    records: state.records
  }
};

export default connect(mapStateToProps)(RecordTable);
