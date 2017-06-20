import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {Contact} from "../../../../model/web/Contact";
import {normalize} from "normalizr";
import {ContactBox, ContactSchema} from "../../../../NormalizeSchema";
import {ContactId} from "../../../../model/web/ContactId";


export const OPEN_ADD_CONTACT_FORM: string = "checkout/open/add/contact";


export const ADD_CONTACT: string = "checkout/contact/add";
export const ADD_ADDITIONAL_CONTACT: string = "checkout/contact/addAdditional";
export const ADD_CONTACT_REQUEST: string = _toRequestType(ADD_CONTACT);

export const NAME = "ContactAddForm";
export interface Values {
  firstName: string;
  lastName: string;
  email: string;
}


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
    contact,
    newContact: contactId.newContact,
  };
  return {
    payload,
    type: ADD_CONTACT_REQUEST,
    meta: {
      from: NAME,
    },
  };
};

export const openAddContactForm = (): { type: string } => {
  return {
    type: OPEN_ADD_CONTACT_FORM,
  };
};

export const addContact = function (contact: Contact): { type: string, payload: ContactBox } {
  return {type: ADD_CONTACT, payload: normalize(contact, ContactSchema)};
};

export const addAdditionalContact = function (contact: Contact): { type: string, payload: ContactBox } {
  return {type: ADD_ADDITIONAL_CONTACT, payload: normalize(contact, ContactSchema)};
};

