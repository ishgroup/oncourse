import {ValidationError} from "../../model/common/ValidationError";
import {Contact} from "../../model/web/Contact";
import {ContactFields} from "../../model/field/ContactFields";
import {State as SummaryState} from "../containers/summary/reducers/State";
import {State as PaymentState} from "../containers/payment/reducers/State";
import {Amount} from "../../model/checkout/Amount";

export interface CheckoutState {
  newContact: boolean;
  phase: Phase;
  fields: ContactFields;
  error: ValidationError;
  payer: PayerState;
  amount: Amount;
  summary: SummaryState;
  payment: PaymentState;
}

export interface PayerState {
  entity: Contact;
}

export enum Phase {
  Init,
  AddContact,
  EditContact,
  Summary,
  Payment,
  PaymentInProgress
}