import * as L from 'lodash';
import faker from 'faker';

import { CheckoutApi } from '../../js/http/CheckoutApi';
import {
  ContactNodeRequest,
  ContactNode,
  Contact,
  CourseClass,
  Enrolment,
  CheckoutModel,
  Product,
  Membership,
  CheckoutModelRequest,
  Voucher,
  PaymentResponse,
  ValidationError,
  PaymentRequest,
  PaymentStatus,
  Article,
  Application,
  FieldHeading,
} from '../../js/model';
import { CreatePromiseReject, MockConfig } from './mocks/MockConfig';
import { mockAmount } from './mocks/MockFunctions';
import { FieldName } from '../../js/enrol/containers/payment/services/PaymentService';
import { WaitingList } from '../../js/model/checkout/WaitingList';
import { Course } from '../../js/model/web/Course';

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

    result.vouchers = this.createVouchersBy([contact], classes);

    const courses = request.waitingCourseIds.map((id) => this.config.db.getWaitingCourseById(id));
    result.waitingLists = this.createWaitingListsBy([contact], courses);

    return this.config.createResponse(result);
  }

  public createEnrolmentsBy(contacts: Contact[], classes: CourseClass[]): Enrolment[] {
    return L.flatten(contacts.map((c: Contact) => classes.map((cc: CourseClass) => this.config.db.createEnrolment(c.id, cc.id))));
  }

  public createApplicationBy(contacts: Contact[], classes: CourseClass[]): Application[] {
    return L.flatten(contacts.map((c: Contact) => classes.map((cc: CourseClass) => this.config.db.createApplication(c.id, cc.id))));
  }

  public createVouchersBy(contacts: Contact[], classes: CourseClass[]): Voucher[] {
    return L.flatten(contacts.map((c: Contact) => classes.map((cc: CourseClass) => this.config.db.createVoucher(c.id, cc.id))));
  }

  public createMembershipsBy(contacts: Contact[], classes: CourseClass[]): Membership[] {
    return L.flatten(contacts.map((c: Contact) => classes.map((cc: CourseClass) => this.config.db.createMembership(c.id, cc.id))));
  }

  public createArticlesBy(contacts: Contact[], classes: Product[]): Article[] {
    return L.flatten(contacts.map((c) => classes.map((cc) => this.config.db.createArticle(c.id, cc.id))));
  }

  public createWaitingListsBy(contacts: Contact[], courses: Course[]): WaitingList[] {
    return L.flatten(contacts.map((c: Contact) => courses.map((cc: Course) => this.config.db.createWaitingList(c.id, cc.id))));
  }

  getCheckoutModel(request: CheckoutModelRequest): Promise<CheckoutModel> {
    const result: CheckoutModel = new CheckoutModel();

    const keys2 = ['street', 'customField.contact.passportType'];
    const fields2 = keys2.map((key) => this.config.db.getFieldByKey(key));

    const mockHeadings = ():FieldHeading[] => ([
      {
        fields: fields2,
        name: 'heading 2',
        description: '',
        ordering: 1
      },
    ]);

    result.contactNodes = L.cloneDeep(request.contactNodes);
    result.contactNodes.map((node) => node.enrolments.map((enrolment) => enrolment.fieldHeadings = mockHeadings()));
    result.contactNodes.map((node) => node.applications.map((application) => application.fieldHeadings = mockHeadings()));

    result.amount = mockAmount();
    result.payerId = request.payerId;

    return this.config.createResponse(result);
  }

  makePayment(paymentRequest: PaymentRequest): Promise<PaymentResponse> {
    if (this.config.props.checkoutApi.makePayment.formError) {
      const error: ValidationError = this.config.createValidationError(1, [FieldName.creditCardNumber, FieldName.creditCardName]);
      return CreatePromiseReject(error);
    }

    if (this.config.props.checkoutApi.makePayment.modelError) {
      return this.getCheckoutModel(paymentRequest.checkoutModelRequest).then((model: CheckoutModel) => {
        model.error = {
          code: 0,
          message: faker.hacker.phrase(),
        };
        return CreatePromiseReject(model);
      });
    }

    const result: PaymentResponse = new PaymentResponse();
    result.sessionId = paymentRequest.sessionId;
    result.status = this.paymentStatusValue();
    return this.config.createResponse(result);
  }

  getPaymentStatus(sessionId: string): Promise<PaymentResponse> {
    const result: PaymentResponse = new PaymentResponse();
    result.sessionId = sessionId;
    result.reference = 'TEST_REFERENCE';
    result.status = this.paymentStatusValue();
    return this.config.createResponse(result);
  }

  private paymentStatusValue(): PaymentStatus {
    if (this.config.props.checkoutApi.makePayment.result.inProgress) {
      return PaymentStatus.IN_PROGRESS;
    } if (this.config.props.checkoutApi.makePayment.result.success) {
      return PaymentStatus.SUCCESSFUL;
    } if (this.config.props.checkoutApi.makePayment.result.failed) {
      return PaymentStatus.FAILED;
    } if (this.config.props.checkoutApi.makePayment.result.undefined) {
      return PaymentStatus.UNDEFINED;
    } if (this.config.props.checkoutApi.makePayment.result.unhandled) {
      return undefined;
    }
    return PaymentStatus.IN_PROGRESS;
  }
}
