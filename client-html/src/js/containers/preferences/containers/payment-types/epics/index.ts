import { combineEpics } from "redux-observable";
import { EpicGetPaymentTypes } from "./EpicGetPaymentTypes";
import { EpicUpdatePaymentTypes } from "./EpicUpdatePaymentTypes";
import { EpicDeletePaymentType } from "./EpicDeletePaymentType";

export const EpicPaymentTypes = combineEpics(EpicGetPaymentTypes, EpicUpdatePaymentTypes, EpicDeletePaymentType);
