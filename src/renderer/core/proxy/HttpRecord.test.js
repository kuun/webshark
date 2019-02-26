import HttpRecord from './HttpRecord'

test('complete a http record, record id should be 1', () => {
  let record = new HttpRecord();
  expect(record.id).toBe(undefined);
  record.completeRecord();
  expect(record.id).toBe(1);
});
