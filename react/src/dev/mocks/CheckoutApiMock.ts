import * as L from "lodash";

import {CheckoutApi} from "../../js/http/CheckoutApi";
import {ContactNodeRequest} from "../../js/model/checkout/request/ContactNodeRequest";
import {ContactNode} from "../../js/model/checkout/ContactNode";
import {Contact} from "../../js/model/web/Contact";
import {CourseClass} from "../../js/model/web/CourseClass";
import {Enrolment} from "../../js/model/checkout/Enrolment";
import {MockConfig} from "./mocks/MockConfig";
import {CheckoutModel} from "../../js/model/checkout/CheckoutModel";
import {mockAmount} from "./mocks/MockFunctions";
import {CheckoutModelRequest} from "../../js/model/checkout/CheckoutModelRequest";
import {PaymentResponse} from "../../js/model/checkout/payment/PaymentResponse";

export class CheckoutApiMock extends CheckoutApi {
  public config: MockConfig;

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

  getContactNode(request: ContactNodeRequest): Promise<ContactNode> {
    const result: ContactNode = new ContactNode();
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


  getCheckoutModel(request: CheckoutModelRequest): Promise<CheckoutModel> {
    const result: CheckoutModel = new CheckoutModel();
    result.contactNodes = L.cloneDeep(request.contactNodes);
    result.amount = mockAmount();
    result.payerId = request.payerId;
    return this.config.createResponse(result);
  }


  makePayment(paymentRequest: PaymentRequest): Promise<PaymentResponse> {
    const result: PaymentResponse = new PaymentResponse();
    return this.config.createResponse(result);
  }
}
