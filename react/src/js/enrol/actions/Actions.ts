import {_toRejectType, _toRequestType} from "../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {contactsSchema} from "../../schema";
import {NAME} from "../containers/contact/ContactAddForm";
import {ValidationError} from "../../model/common/ValidationError";
import {Product} from "../../model/web/Product";
import {CourseClass} from "../../model/web/CourseClass";
import {Contact} from "../../model/web/Contact";

//initialize checkout application
export const Init: string = "checkout/init";
export const InitRequest: string = _toRequestType(Init);
export const InitReject: string = _toRejectType(Init);

//change current phase action
export const PhaseChange: string = "checkout/phase/change";

export const ContactAdd: string = "checkout/contact/add";
export const ContactAddRequest: string = _toRequestType(ContactAdd);
export const ContactAddReject: string = _toRejectType(ContactAdd);


export const PayerSet: string = "checkout/payer/set";
export const PayerSetRequest: string = _toRequestType(PayerSet);
export const PayerSetReject: string = _toRejectType(PayerSet);


export const FieldsLoad: string = "checkout/fields/load";
export const FieldsLoadRequest: string = _toRequestType(FieldsLoad);
export const FieldsLoadReject: string = _toRejectType(FieldsLoad);


export const ContactAddAction = (id: string, values: any): any => {
  const payload: any = normalize(Object.assign({}, values, {id: id}), contactsSchema);
  return {
    type: ContactAddRequest,
    payload: payload,
    meta: {
      from: NAME
    }
  };
};

export const ContactAddRejectAction = (error: ValidationError): any => {
  return {
    type: ContactAddReject,
    payload: error,
    meta: {
      from: NAME
    }
  }
};