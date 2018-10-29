const defaultColumns = [{
  title: 'Sequence',
  dataIndex: 'id',
  width: 80
}, {
  title: 'Method',
  dataIndex: 'method',
  width: 80
}, {
  title: 'URL',
  dataIndex: 'url',
  width: 300
}, {
  title: 'Status Code',
  dataIndex: 'statusCode',
  width: 80
}, {
  title: 'Content Type',
  dataIndex: 'resHeaders',
  width: 200,
  render: (data) => {
    if (data) {
      return data['content-type'];
    }
    return undefined;
  }
}];

const recordTableColumns = (state = defaultColumns, action) => {
  switch (action.type) {
    default:
      return state;
  }
};

export default recordTableColumns;
