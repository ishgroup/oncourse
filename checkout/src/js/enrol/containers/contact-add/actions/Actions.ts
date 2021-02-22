import {Contact, ContactId} from "../../../../model";
import {normalize} from "normalizr";
import {ContactSchema, ContactsState} from "../../../../NormalizeSchema";
import {IAction} from "../../../../actions/IshAction";
import {ContactState} from "../../../../services/IshState";

export const SUBMIT_ADD_CONTACT: string = "checkout/submit/add/contact";

export const ADD_PAYER_FROM_VOUCHER: string = "checkout/add/payer/from/voucher";

export const ADD_CONTACT_TO_STATE: string = "checkout/add/contact/to/state";

export const NAME = "ContactAddForm";
export interface Values {
  firstName: string;
  lastName: string;
  email: string;
  company?: boolean;
}

const getContactPayload = (contactId, values): SubmitContact => {
  const contact: Contact = new Contact();
  contact.id = contactId.id;
  contact.firstName = values.firstName;
  contact.lastName = values.lastName;
  contact.email = values.email;

  return {
    contact,
    newContact: contactId.newContact,
    parentRequired: contactId.parentRequired,
  };
};


export interface SubmitContact {
  contact: Contact;
  newContact: boolean;
  parentRequired?: boolean;
  parent?: Contact;
}

export const submitAddContact = (contactId: ContactId, values: Values): IAction<SubmitContact> => {
  return {
    type: SUBMIT_ADD_CONTACT,
    payload: getContactPayload(contactId, values),
    meta: {
      from: NAME,
    },
  };
};

export const addPayerFromVoucher = (contact: Contact): IAction<SubmitContact> => {
  return {
    type: ADD_PAYER_FROM_VOUCHER,
    payload: {contact, newContact: false},
    meta: {
      from: NAME,
    },
  };
};

export const addContact = function (contact: ContactState): { type: string, payload: ContactsState } {
  return {type: ADD_CONTACT_TO_STATE, payload: normalize(contact, ContactSchema)};
};
