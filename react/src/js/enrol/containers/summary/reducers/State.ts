import * as L from "lodash";

import {Enrolment} from "../../../../model/checkout/Enrolment";
import {normalize, schema} from "normalizr";
import {ContactNode} from "../../../../model/checkout/ContactNode";
import {Voucher} from "../../../../model/checkout/Voucher";
const SEnrolments = new schema.Entity('enrolments', {}, {idAttribute: (e: Enrolment) => `${e.contactId}-${e.classId}`});
const SVouchers = new schema.Entity('vouchers', {}, {idAttribute: (v: Voucher) => `${v.contactId}-${v.productId}`});

const SContactNodes = new schema.Entity('contactNodes', {
  enrolments: [SEnrolments],
  vouchers: [SVouchers]
}, {idAttribute: "contactId"});

const Schema = new schema.Array(SContactNodes);

export interface ContactNodesStorage {
  [key: string]: {
    contactId: string,
    enrolments: string[]
    vouchers: string[]
  }
}

export interface State {
  result: string[]
  entities: {
    enrolments: { [key: string]: Enrolment }
    vouchers: { [key: string]: Voucher }
    contactNodes: ContactNodesStorage
  }
}

export const ContactNodeToState = (input: ContactNode[]): State => {
  return normalize(input, Schema);
};

export const EnrolmentToState = (input: Enrolment): State => {
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
  const enrolmentId: string = payload.entities.contactNodes[payload.result[0]].enrolments[0];
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
      ns.entities.contactNodes = {...ns.entities.contactNodes, ...payload.entities.contactNodes};
      break;
    case Case.addEnrolment:
      payload.result.forEach((id) => {
        ns.entities.contactNodes[id].enrolments = [...ns.entities.contactNodes[id].enrolments, ...payload.entities.contactNodes[id].enrolments]
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