import {_toRequestType, FULFILLED} from "../../../../common/actions/ActionUtils";
import {NAME as ContactAddForm} from "../ContactAddForm";
import {Contact} from "../../../../model/web/Contact";
import {normalize} from "normalizr";
import {ContactsSchema} from "../../../../NormalizeSchema";


export const OPEN_ADD_CONTACT_FORM: string = "checkout/open/add/contact";


export const ADD_CONTACT: string = "checkout/contact/add";
export const ADD_CONTACT_REQUEST: string = _toRequestType(ADD_CONTACT);

export const addContactRequest = (id: string, values: any): any => {
  const payload: Contact = Object.assign({}, values, {id: id});
  return {
    type: ADD_CONTACT_REQUEST,
    payload: payload,
    meta: {
      from: ContactAddForm
    }
  };
};

export const openAddContactForm = (): { type: string } => {
  return {
    type: OPEN_ADD_CONTACT_FORM
  }
};

export const addContact = function (contact: Contact) {
  return {type: ADD_CONTACT, payload: normalize(contact, ContactsSchema)};
};


