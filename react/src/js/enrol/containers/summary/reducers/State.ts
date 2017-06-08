
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
