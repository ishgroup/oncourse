import { combineEpics } from "redux-observable";
import { EpicGetPlainTaxes } from "./EpicGetPlainTaxes";

export const EpicTaxes = combineEpics(EpicGetPlainTaxes);
