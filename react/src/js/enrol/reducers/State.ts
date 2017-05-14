//--- Enrol State
import {ValidationError} from "../../model/common/ValidationError";
import {Contact} from "../../model/web/Contact";

export interface EnrolState {
  phase: Phase,
  error: ValidationError;
  payer: PayerState;
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
  Summary
}