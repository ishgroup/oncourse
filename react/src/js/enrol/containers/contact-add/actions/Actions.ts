import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {NAME as ContactAddForm, Values} from "../ContactAddForm";
import {Contact} from "../../../../model/web/Contact";
import {normalize} from "normalizr";
import {ContactBox, ContactSchema} from "../../../../NormalizeSchema";
import {ContactId} from "../../../../model/web/ContactId";


export const OPEN_ADD_CONTACT_FORM: string = "checkout/open/add/contact";


export const ADD_CONTACT: string = "checkout/contact/add";
export const ADD_CONTACT_REQUEST: string = _toRequestType(ADD_CONTACT);


export interface AddContactPayload {
  contact: Contact;
  newContact: boolean;
}
export const addContactRequest = (contactId: ContactId, values: Values): any => {
  const contact: Contact = new Contact();
  contact.id = contactId.id;
  contact.firstName = values.firstName;
  contact.lastName = values.lastName;
  contact.email = values.email;
  const payload: AddContactPayload = {
    contact: contact,
    newContact: contactId.newContact
  };
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

export const addContact = function (contact: Contact): { type: string, payload: ContactBox } {
  return {type: ADD_CONTACT, payload: normalize(contact, ContactSchema)};
};


