import { combineEpics } from "redux-observable";
import { EpicDeletePaymentType } from "./EpicDeletePaymentType";
import { EpicGetPaymentTypes } from "./EpicGetPaymentTypes";
import { EpicUpdatePaymentTypes } from "./EpicUpdatePaymentTypes";

export const EpicPaymentTypes = combineEpics(EpicGetPaymentTypes, EpicUpdatePaymentTypes, EpicDeletePaymentType);
