import {Application} from "./../checkout/Application";
import {Article} from "./../checkout/Article";
import {Enrolment} from "./../checkout/Enrolment";
import {Membership} from "./../checkout/Membership";
import {Voucher} from "./../checkout/Voucher";

export class ContactNode {
  contactId?: string;
  enrolments?: Enrolment[];
  applications?: Application[];
  articles?: Article[];
  memberships?: Membership[];
  vouchers?: Voucher[];
}

