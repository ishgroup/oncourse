import * as L from "lodash";

import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Application} from "../../../../model/checkout/Application";
import {normalize, schema} from "normalizr";
import {ContactNode} from "../../../../model/checkout/ContactNode";
import {Voucher} from "../../../../model/checkout/Voucher";
const SEnrolments = new schema.Entity('enrolments', {}, {idAttribute: (e: Enrolment) => `${e.contactId}-${e.classId}`});
const SApplications = new schema.Entity('applications', {}, {idAttribute: (a: Application) => `${a.contactId}-${a.classId}`});
const SVouchers = new schema.Entity('vouchers', {}, {idAttribute: (v: Voucher) => `${v.contactId}-${v.productId}`});

const SContactNodes = new schema.Entity('contactNodes', {
  enrolments: [SEnrolments],
  applications: [SApplications],

  vouchers: [SVouchers]
}, {idAttribute: "contactId"});

const Schema = new schema.Array(SContactNodes);

export interface ContactNodesStorage {
  [key: string]: {
    contactId: string,
    enrolments: string[]
    applications: string[]
    vouchers: string[]
  }
}

export interface State {
  result: string[]
  entities: {
    enrolments: { [key: string]: Enrolment }
    applications: { [key: string]: Application }
    vouchers: { [key: string]: Voucher }
    contactNodes: ContactNodesStorage
  }
}

export const ContactNodeToState = (input: ContactNode[]): State => {
  return normalize(input, Schema);
};

export const ItemToState = (input: Enrolment | Application): State => {

  const data  = input instanceof Enrolment ? { enrolments: [input] } : { applications: [input] }
  
  return normalize([
    {
      contactId: input.contactId,
      
      ...data
    }
  ], Schema);
};

enum Case {
  addContact,
  addEnrolment,
  addApplication,
  updateEnrolment,
  updateApplication,
  refresh
}

const getCase = (state: State, payload: State): Case => {
  const contactId: string = payload.result[0];

  if (state.result.length == 0) {
    return Case.refresh;
  } else if (state.result.indexOf(contactId) < 0) {
    return Case.addContact
  }

  if (payload.entities.contactNodes[payload.result[0]].enrolments) {
    const enrolmentId: string = payload.entities.contactNodes[payload.result[0]].enrolments[0];

    if (L.isNil(state.entities.enrolments[enrolmentId])) {
      return Case.addEnrolment
    } else {
      return Case.updateEnrolment
    }
  } else if (payload.entities.contactNodes[payload.result[0]].applications)  {
    const applicationId: string = payload.entities.contactNodes[payload.result[0]].applications[0];

    if (L.isNil(state.entities.applications[applicationId])) {
      return Case.addApplication
    } else {
      return Case.updateApplication
    }
  }
  return Case.refresh
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
    case Case.addApplication:
      payload.result.forEach((id) => {
        ns.entities.contactNodes[id].applications = [...ns.entities.contactNodes[id].applications, ...payload.entities.contactNodes[id].applications]
      });
      ns.entities.applications = {...ns.entities.applications, ...payload.entities.applications};
      break;
    case Case.updateEnrolment:
      ns.entities.enrolments = {...ns.entities.enrolments, ...payload.entities.enrolments};
      break;
    case Case.updateApplication:
      ns.entities.applications = {...ns.entities.applications, ...payload.entities.applications};
      break;
    case Case.refresh:
      return payload;
    default:
      throw new Error();
  }
  return ns;
};