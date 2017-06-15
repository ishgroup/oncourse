import {ContactNode} from "../../model/checkout/ContactNode";
import * as Items from "../../model/checkout";

export class ContactNodeService {
  /**
   * Extracts correspondent item from the node and cast to corrspondent type.
   * We do it to get an possibility to use 'instanceof' expression for purchase items.
   * className can accept the following values:
   */
  static getPurchaseItem = (node: ContactNode, className: string): Items.PurchaseItem => {
    return Object.assign(new Items[className], node[`${className.toLowerCase()}s`][0]);
  }
}