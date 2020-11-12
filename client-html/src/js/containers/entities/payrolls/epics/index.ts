import { combineEpics } from "redux-observable";
import { EpicExecutePayroll } from "./EpicExecutePayroll";
import { EpicPreparePayRoll } from "./EpicPreparePayRoll";

export const EpicPayrolls = combineEpics(EpicPreparePayRoll, EpicExecutePayroll);
