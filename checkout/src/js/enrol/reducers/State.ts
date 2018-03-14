import {ValidationError, Contact, ContactFields, Amount, Preferences} from "../../model";
import {State as SummaryState} from "../containers/summary/reducers/State";
import {State as PaymentState} from "../containers/payment/reducers/State";
import {ContactsState} from "../../NormalizeSchema";

export interface CheckoutState {
  newContact: boolean;
  phase: Phase;
  page: Phase;  // Summary, Payment, Result
  fields: ContactFieldsState;
  error: ValidationError;
  payerId: string;
  contacts: ContactsState;
  amount: AmountState;
  summary: SummaryState;
  payment: PaymentState;
  concession: any;
  redeemVouchers: any;
  contactAddProcess: any;
  preferences: Preferences;
  fetching?: boolean;
  isCartModified: boolean;
}

export interface PayerState {
  entity: Contact;
}

export class AmountState extends Amount {
  payNowVisibility?: boolean = true;
}

export class ContactFieldsState {
  current: ContactFields = null;
  unfilled: ContactFields[] = [];
}

export enum Phase {
  Init,
  AddPayer,
  EditContact,
  ComplementEditContact,
  Summary,
  Payment,
  Result,
  AddContact,
  AddContactAsPayer,
  AddContactAsCompany,
  AddConcession,
  AddParent,
  ChangeParent,
}

