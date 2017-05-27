import * as L from "lodash";

import {CheckoutApi} from "../http/CheckoutApi";
import {PurchaseItemsRequest} from "../model/checkout/request/PurchaseItemsRequest";
import {PurchaseItems} from "../model/checkout/PurchaseItems";
import {Contact} from "../model/web/Contact";
import {CourseClass} from "../model/web/CourseClass";
import {Enrolment} from "../model/checkout/Enrolment";
import {MockConfig} from "./mocks/MockConfig";

export class CheckoutApiMock extends CheckoutApi {
  public config: MockConfig = MockConfig.CONFIG;

  getPurchaseItems(request: PurchaseItemsRequest): Promise<PurchaseItems> {
    const result: PurchaseItems = new PurchaseItems();
    result.contactId = request.contactId;

    const contact: Contact = this.config.db.getContactById(request.contactId);
    const classes: CourseClass[] = request.classIds.map((id) => this.config.db.getCourseClassById(id));

    result.enrolments = this.createEnrolmentsBy([contact], classes);
    return this.config.createResponse(result);
  }

  public createEnrolmentsBy(contacts: Contact[], classes: CourseClass[]): Enrolment[] {
    return L.flatten(contacts.map((c: Contact) => {
      return classes.map((cc: CourseClass) => {
        return this.config.db.createEnrolment(c.id, cc.id)
      })
    }));
  }
}
