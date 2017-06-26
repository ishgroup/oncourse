import {ContactNode} from "../../model/checkout/ContactNode";
import * as Items from "../../model/checkout/Index";
import {PurchaseItem} from "../../model/checkout/Index";
import {inspect} from "util";

export class ContactNodeService {
  /**
   * Extracts correspondent item from the node and cast to corespondent type.
   * We do it to get an possibility to use 'instanceof' expression for purchase items.
   * className can accept the following values:
   */
  static getPurchaseItem = (node: ContactNode, original: PurchaseItem): Items.PurchaseItem => {

    if (original instanceof Items.Application) {
      return Object.assign(new Items.Application(), node.applications[0]);
    }

    if (original instanceof Items.Article) {
      return Object.assign(new Items.Article(), node.articles[0]);
    }

    if (original instanceof Items.Enrolment) {
      return Object.assign(new Items.Enrolment(), node.enrolments[0]);
    }

    if (original instanceof Items.Membership) {
      return Object.assign(new Items.Membership(), node.memberships[0]);
    }

    if (original instanceof Items.Voucher) {
      return Object.assign(new Items.Voucher(), node.vouchers[0]);
    }

    throw new Error(`Unsupported purchase item: ${inspect(original, true, 10, true)}`);
  }
}
