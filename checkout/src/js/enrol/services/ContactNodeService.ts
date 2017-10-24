import * as models from "../../model";
import {inspect} from "util";

export class ContactNodeService {
  /**
   * Extracts correspondent item from the node and cast to corespondent type.
   * We do it to get an possibility to use 'instanceof' expression for purchase items.
   * className can accept the following values:
   */
  static getPurchaseItem = (node: models.ContactNode, original: any): models.PurchaseItem => {

    if (original instanceof models.Application) {
      return Object.assign(new models.Application(), node.applications[0]);
    }

    if (original instanceof models.Article) {
      return Object.assign(new models.Article(), node.articles[0]);
    }

    if (original instanceof models.Enrolment) {
      return Object.assign(new models.Enrolment(), node.enrolments[0]);
    }

    if (original instanceof models.Membership) {
      return Object.assign(new models.Membership(), node.memberships[0]);
    }

    if (original instanceof models.Voucher) {
      return Object.assign(new models.Voucher(), node.vouchers[0]);
    }

    throw new Error(`Unsupported purchase item: ${inspect(original, true, 10, true)}`);
  }
}
