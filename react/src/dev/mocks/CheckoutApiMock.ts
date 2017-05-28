import * as L from "lodash";

import {CheckoutApi} from "../../js/http/CheckoutApi";
import {PurchaseItemsRequest} from "../../js/model/checkout/request/PurchaseItemsRequest";
import {PurchaseItems} from "../../js/model/checkout/PurchaseItems";
import {Contact} from "../../js/model/web/Contact";
import {CourseClass} from "../../js/model/web/CourseClass";
import {Enrolment} from "../../js/model/checkout/Enrolment";
import {MockConfig} from "./mocks/MockConfig";
import {CheckoutModel} from "../../js/model/checkout/CheckoutModel";
import {mockAmount} from "./mocks/MockFunctions";

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


  calculateAmount(checkoutModel: CheckoutModel): Promise<CheckoutModel> {
    checkoutModel.amount = mockAmount();
    return this.config.createResponse(checkoutModel);
  }
}
