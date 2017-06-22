import {ContactFields} from "../../../../model/field/ContactFields";
import {IAction} from "../../../../actions/IshAction";
import {Contact} from "../../../../model/web/Contact";

export const OPEN_EDIT_CONTACT: string = "checkout/open/edit/contact";
export const SUBMIT_EDIT_CONTACT: string = "checkout/submit/edit/contact";

export const SET_FIELDS_TO_STATE: string = "checkout/set/fields/to/state";
export const RESET_FIELDS_STATE: string = "checkout/reset/fields";


export const submitEditContact = (contact: Contact): IAction<Contact> => {
  return {
    type: SUBMIT_EDIT_CONTACT,
    payload: contact,
  };
};

export const openEditContact = (contact: Contact): IAction<Contact> => {
  return {
    type: OPEN_EDIT_CONTACT,
    payload: contact,
  };
};

export const setFieldsToState = (fields: ContactFields): IAction<ContactFields> => {
  return {
    type: SET_FIELDS_TO_STATE,
    payload: fields,
  };
};

export const resetFieldsState = (): IAction<any> => {
  return {
    type: RESET_FIELDS_STATE,
  };
};

