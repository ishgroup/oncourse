import { combineEpics } from "redux-observable";
import { EpicGetTaxTypes } from "./EpicGetTaxTypes";
import { EpicUpdateTaxTypes } from "./EpicUpdateTaxTypes";
import { EpicDeleteTaxType } from "./EpicDeleteTaxType";

export const EpicTaxTypes = combineEpics(EpicGetTaxTypes, EpicUpdateTaxTypes, EpicDeleteTaxType);
