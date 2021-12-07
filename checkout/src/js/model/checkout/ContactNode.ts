import {Application} from "./../checkout/Application";
import {Article} from "./../checkout/Article";
import {Enrolment} from "./../checkout/Enrolment";
import {Membership} from "./../checkout/Membership";
import {Voucher} from "./../checkout/Voucher";
import {WaitingList} from "./../checkout/WaitingList";

export class ContactNode {
  contactId: string;
  contactFirstName: string;
  contactLastName: string;
  contactEmail: string;
  enrolments: Enrolment[];
  applications: Application[];
  articles: Article[];
  memberships: Membership[];
  vouchers: Voucher[];
  waitingLists: WaitingList[];
  suggestedCourseIds?: string[];
  suggestedProductIds?: string[];
}

