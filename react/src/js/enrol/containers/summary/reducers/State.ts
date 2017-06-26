import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Application} from "../../../../model/checkout/Application";
import {normalize, schema} from "normalizr";
import {ContactNode} from "../../../../model/checkout/ContactNode";
import {Voucher} from "../../../../model/checkout/Voucher";
import {Article} from "../../../../model/checkout/Article";
import {Membership} from "../../../../model/checkout/Membership";
import {PurchaseItem} from "../../../../model/checkout/Index";
const SEnrolments = new schema.Entity('enrolments', {}, {idAttribute: (e: Enrolment) => `${e.contactId}-${e.classId}`});
const SApplications = new schema.Entity('applications', {}, {idAttribute: (a: Application) => `${a.contactId}-${a.classId}`});
const SMemberships = new schema.Entity('memberships', {}, {idAttribute: (m: Membership) => `${m.contactId}-${m.productId}`});
const SArticles = new schema.Entity('articles', {}, {idAttribute: (a: Article) => `${a.contactId}-${a.productId}`});
const SVouchers = new schema.Entity('vouchers', {}, {idAttribute: (v: Voucher) => `${v.contactId}-${v.productId}`});

const SContactNodes = new schema.Entity('contactNodes', {
    enrolments: [SEnrolments],
    applications: [SApplications],
    memberships: [SMemberships],
    articles: [SArticles],
    vouchers: [SVouchers],
  }, {idAttribute: "contactId"});

const Schema = new schema.Array(SContactNodes);

export interface ContactNodeStorage {
  contactId: string;
  enrolments: string[];
  applications: string[];
  vouchers: string[];
  articles: string[];
  memberships: string[];
}
export interface ContactNodesStorage {[key: string]: ContactNodeStorage
}

export interface State {
  result: string[];
  entities: {
    enrolments: { [key: string]: Enrolment }
    applications: { [key: string]: Application }
    vouchers: { [key: string]: Voucher }
    articles: { [key: string]: Article }
    memberships: { [key: string]: Membership }
    contactNodes: ContactNodesStorage;
  };
}

export const ContactNodeToState = (input: ContactNode[]): State => {
  return normalize(input, Schema);
};

export const ItemToState = (input: PurchaseItem): State => {

  const node: ContactNode = new ContactNode();
  node.contactId = input.contactId;
  node.enrolments = input instanceof Enrolment ? [input] : [];
  node.applications = input instanceof Application ? [input] : [];
  node.memberships = input instanceof Membership ? [input] : [];
  node.articles = input instanceof Article ? [input] : [];
  node.vouchers = input instanceof Voucher ? [input] : [];

  return ContactNodeToState([node]);
};
