import {Contact} from "../../../../model/web/Contact";
import {normalize} from "normalizr";
import {ContactSchema, ContactsState} from "../../../../NormalizeSchema";
import {ContactId} from "../../../../model/web/ContactId";
import {IAction} from "../../../../actions/IshAction";

export const OPEN_ADD_CONTACT_FORM: string = "checkout/open/add/contact";

export const SUBMIT_ADD_CONTACT: string = "checkout/submit/add/contact";
export const ADD_CONTACT_TO_STATE: string = "checkout/add/contact/to/state";

export const NAME = "ContactAddForm";
export interface Values {
  firstName: string;
  lastName: string;
  email: string;
}


export interface SubmitContact {
  contact: Contact;
  newContact: boolean;
}

export const submitAddContact = (contactId: ContactId, values: Values): IAction<SubmitContact> => {
  const contact: Contact = new Contact();
  contact.id = contactId.id;
  contact.firstName = values.firstName;
  contact.lastName = values.lastName;
  contact.email = values.email;
  const payload: SubmitContact = {
    contact: contact,
    newContact: contactId.newContact,
  };
  return {
    type: SUBMIT_ADD_CONTACT,
    payload: payload,
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

export const addContact = function (contact: Contact): { type: string, payload: ContactsState } {
  return {type: ADD_CONTACT_TO_STATE, payload: normalize(contact, ContactSchema)};
};
