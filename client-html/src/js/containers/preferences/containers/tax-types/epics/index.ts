import { combineEpics } from "redux-observable";
import { EpicDeleteTaxType } from "./EpicDeleteTaxType";
import { EpicGetTaxTypes } from "./EpicGetTaxTypes";
import { EpicUpdateTaxTypes } from "./EpicUpdateTaxTypes";

export const EpicTaxTypes = combineEpics(EpicGetTaxTypes, EpicUpdateTaxTypes, EpicDeleteTaxType);
