import { combineEpics } from "redux-observable";
import { EpicGetTransaction } from "./EpicGetTransaction";

export const EpicTransaction = combineEpics(EpicGetTransaction);
