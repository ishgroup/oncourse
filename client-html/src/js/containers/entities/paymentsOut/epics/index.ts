import { combineEpics } from "redux-observable";
import { EpicGetPaymentOut } from "./EpicGetPaymentOut";
import { EpicUpdatePaymentOut } from "./EpicUpdatePaymenOut";
import { EpicGetAddPaymentOutContact } from "./EpicGetAddPaymentOutContact";
import { EpicGetAddPaymentOutValues } from "./EpicGetAddPaymentOutValues";
import { EpicGetPaymentOutMethods } from "./EpicGetPaymentOutMethods";
import { EpicGetRefundablePayents } from "./EpicGetRefundablePayments";
import { EpicPostPaymentOut } from "./EpicPostPaymentOut";

export const EpicPaymentOut = combineEpics(
  EpicGetPaymentOut,
  EpicUpdatePaymentOut,
  EpicPostPaymentOut,
  EpicGetAddPaymentOutContact,
  EpicGetPaymentOutMethods,
  EpicGetAddPaymentOutValues,
  EpicGetRefundablePayents
);
