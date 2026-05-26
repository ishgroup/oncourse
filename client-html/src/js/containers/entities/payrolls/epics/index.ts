import { combineEpics } from 'redux-observable';
import { EpicExecutePayroll } from './EpicExecutePayroll';
import { EpicPreparePayRoll } from './EpicPreparePayRoll';
import { EpicSetPreparedPayroll } from './EpicSetPreparedPayroll';

export const EpicPayrolls = combineEpics(
  EpicPreparePayRoll,
  EpicExecutePayroll,
  EpicSetPreparedPayroll
);
