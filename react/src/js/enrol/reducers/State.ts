import {ValidationError} from "../../model/common/ValidationError";
import {Contact} from "../../model/web/Contact";
import {ContactFields} from "../../model/field/ContactFields";
import {State as SummaryState} from "../containers/summary/reducers/State";
import {State as PaymentState} from "../containers/payment/reducers/State";
import {Amount} from "../../model/checkout/Amount";
import {ContactsState} from "../../NormalizeSchema";

export interface CheckoutState {
  newContact: boolean;
  phase: Phase;
  page: Phase;  // Summary, Payment, Result
  fields: ContactFields;
  error: ValidationError;
  payerId: string;
  contacts: ContactsState;
  amount: Amount;
  summary: SummaryState;
  payment: PaymentState;
}

export interface PayerState {
  entity: Contact;
}

export enum Phase {
  Init,
  AddPayer,
  EditContact,
  Summary,
  Payment,
  Result,
  AddContact,
  AddContactAsPayer,
  AddContactAsCompany,
}
