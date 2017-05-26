//--- CheckoutState
import {ValidationError} from "../../model/common/ValidationError";
import {Contact} from "../../model/web/Contact";
import {ContactFields} from "../../model/field/ContactFields";
import {PurchaseItems} from "../../model/checkout/PurchaseItems";

export interface CheckoutState {
  phase: Phase,
  fields: ContactFields,
  error: ValidationError;
  payer: PayerState;
  purchaseItems: PurchaseItems[]
}

export interface PayerEntity extends Contact {

}

export interface PayerState {
  entity: PayerEntity;
}

export enum Phase {
  Init,
  AddContact,
  EditContactDetails,
  Summary,
  ProceedToPayment
}