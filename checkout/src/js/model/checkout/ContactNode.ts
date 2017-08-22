import {Application} from "./Application";
import {Article} from "./Article";
import {Enrolment} from "./Enrolment";
import {Membership} from "./Membership";
import {Voucher} from "./Voucher";

export class ContactNode {
  contactId?: string;
  enrolments?: Enrolment[];
  applications?: Application[];
  articles?: Article[];
  memberships?: Membership[];
  vouchers?: Voucher[];
}

