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
import {Voucher} from "../../js/model/checkout/Voucher";
import {ProductClass} from "../../js/model/web/ProductClass";
import {PaymentResponse} from "../../js/model/checkout/payment/PaymentResponse";

import {CreatePromiseReject} from "./mocks/MockConfig";
import {ValidationError} from "../../js/model/common/ValidationError";
import {FieldName} from "../../js/enrol/containers/payment/services/PaymentService";
import {PaymentRequest} from "../../js/model/checkout/payment/PaymentRequest";
import {PaymentStatus} from "../../js/model/checkout/payment/PaymentStatus";

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

    const products: ProductClass[] = request.productIds.map((id) => this.config.db.getProductClassById(id));
    result.vouchers = this.createVouchersBy([contact], products);

    return this.config.createResponse(result);
  }

  public createEnrolmentsBy(contacts: Contact[], classes: CourseClass[]): Enrolment[] {
    return L.flatten(contacts.map((c: Contact) => {
      return classes.map((cc: CourseClass) => {
        return this.config.db.createEnrolment(c.id, cc.id)
      })
    }));
  }

  public createVouchersBy(contacts: Contact[], classes: CourseClass[]): Voucher[] {
    return L.flatten(contacts.map((c: Contact) => {
      return classes.map((cc: CourseClass) => {
        return this.config.db.createVoucher(c.id, cc.id)
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
    if (this.config.props.checkoutApi.makePayment.formError) {
      const error: ValidationError = this.config.createValidationError(1, [FieldName.creditCardNumber, FieldName.creditCardName]);
      return CreatePromiseReject(error);
    }
    const result: PaymentResponse = new PaymentResponse();
    result.sessionId = paymentRequest.sessionId;
    return this.config.createResponse(result);
  }
}
