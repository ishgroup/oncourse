import * as L from "lodash";

import {Enrolment} from "../../../../model/checkout/Enrolment";
import {normalize, schema} from "normalizr";
import {ContactNode} from "../../../../model/checkout/ContactNode";
import {Voucher} from "../../../../model/checkout/Voucher";
const SEnrolments = new schema.Entity('enrolments', {}, {idAttribute: (e) => `${e.contactId}-${e.classId}`});

const SPurchaseItems = new schema.Entity('contacts', {
  enrolments: [SEnrolments]
}, {idAttribute: "contactId"});

const Schema = new schema.Array(SPurchaseItems);

export interface State {
  result: string[]
  entities: {
    enrolments: { [key: string]: Enrolment }
    vouchers: { [key: string]: Voucher }
    contacts: {
      [key: string]: {
        contactId: string,
        enrolments: string[]
      }
    }
  }
}

export const ContactNodeToState = (input: ContactNode[]): State => {
  return normalize(input, Schema);
};

export const convertFromEnrolment = (input: Enrolment): State => {
  return normalize([
    {
      contactId: input.contactId,
      enrolments: [input]
    }
  ], Schema);
};

enum Case {
  addContact,
  addEnrolment,
  updateEnrolment,
  refresh
}

const getCase = (state: State, payload: State): Case => {
  const contactId: string = payload.result[0];
  const enrolmentId: string = payload.entities.contacts[payload.result[0]].enrolments[0];
  if (state.result.length == 0) {
    return Case.refresh;
  } else if (state.result.indexOf(contactId) < 0) {
    return Case.addContact
  } else if (L.isNil(state.entities.enrolments[enrolmentId])) {
    return Case.addEnrolment
  } else {
    return Case.updateEnrolment
  }
};

export const merge = (state: State, payload: State): State => {
  const ns: State = L.cloneDeep(state);
  switch (getCase(state, payload)) {
    case Case.addContact:
      ns.result = [...ns.result, ...payload.result];
      ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
      ns.entities.contacts = {...ns.entities.contacts, ...payload.entities.contacts};
      break;
    case Case.addEnrolment:
      payload.result.forEach((id) => {
        ns.entities.contacts[id].enrolments = [...ns.entities.contacts[id].enrolments, ...payload.entities.contacts[id].enrolments]
      });
      ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
      break;
    case Case.updateEnrolment:
      ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
      break;
    case Case.refresh:
      return payload;
    default:
      throw new Error();
  }
  return ns;
};