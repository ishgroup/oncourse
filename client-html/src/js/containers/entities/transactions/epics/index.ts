import { combineEpics } from "redux-observable";
import { EpicCreateTransaction } from "./EpicCreateTransaction";
import { EpicGetTransaction } from "./EpicGetTransaction";

export const EpicTransaction = combineEpics(EpicGetTransaction, EpicCreateTransaction);
