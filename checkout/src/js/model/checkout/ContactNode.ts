import { Application } from './Application';
import { Article } from './Article';
import { Enrolment } from './Enrolment';
import { Membership } from './Membership';
import { Voucher } from './Voucher';
import { WaitingList } from './WaitingList';

export class ContactNode {
  contactId: string;

  enrolments: Enrolment[];

  applications: Application[];

  articles: Article[];

  memberships: Membership[];

  vouchers: Voucher[];

  waitingLists: WaitingList[];

  suggestedCourseIds?: string[];

  suggestedProductIds?: string[];
}
