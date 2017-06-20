import {Application} from "../model/../checkout/Application";
import {Article} from "../model/../checkout/Article";
import {Enrolment} from "../model/../checkout/Enrolment";
import {Membership} from "../model/../checkout/Membership";
import {Voucher} from "../model/../checkout/Voucher";

export class ContactNode {
  contactId?: string;
  enrolments?: Enrolment[];
  applications?: Application[];
  articles?: Article[];
  memberships?: Membership[];
  vouchers?: Voucher[];
}

