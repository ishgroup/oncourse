import * as L from "lodash";
import {ContactFields} from "../../model/field/ContactFields";
import {Injector} from "../../injector";
import {ContactFieldsRequest} from "../../model/field/ContactFieldsRequest";
import {FieldSet} from "../../model/field/FieldSet";
import {SubmitFieldsRequest} from "../../model/field/SubmitFieldsRequest";
import {CreateContactParams} from "../../model/web/CreateContactParams";
import {IshState} from "../../services/IshState";
import {ContactApi} from "../../http/ContactApi";
import {CheckoutApi} from "../../http/CheckoutApi";
import {Enrolment} from "../../model/checkout/Enrolment";
import {Field} from "../../model/field/Field";
import {CheckoutModel} from "../../model/checkout/CheckoutModel";
import {State} from "../containers/summary/reducers/State";
import {Amount} from "../../model/checkout/Amount";
import {ContactNode} from "../../model/checkout/ContactNode";
import {CheckoutModelRequest} from "../../model/checkout/CheckoutModelRequest";
import {ContactNodeRequest} from "../../model/checkout/request/ContactNodeRequest";
import {PaymentService, Values} from "../containers/payment/services/PaymentService";
import {PaymentResponse} from "../../model/checkout/payment/PaymentResponse";
import {PaymentRequest} from "../../model/checkout/payment/PaymentRequest";

export class CheckoutService {
  constructor(private contactApi: ContactApi, private checkoutApi: CheckoutApi) {
  }

  public loadFields = (state: IshState): Promise<ContactFields> => {
    return this.contactApi.getContactFields(BuildContactFieldsRequest.fromState(state));
  };

  public submitContactDetails = (fields: ContactFields, values: { [key: string]: any }): Promise<any> => {
    return this.contactApi.submitContactDetails(BuildSubmitFieldsRequest.fromValues(fields, values));
  };

  public createOrGetContact = (values: { [key: string]: string }): Promise<string> => {
    return this.contactApi.createOrGetContact(BuildCreateContactParams.fromValues(values));
  };


  public getContactNode = (state: IshState): Promise<ContactNode> => {
    return this.checkoutApi.getContactNode(BuildContactNodeRequest.fromState(state));
  };

  public updateEnrolment = (enrolment: Enrolment, state: IshState): Promise<Enrolment> => {
    if (enrolment.selected) {
      const request: ContactNodeRequest = BuildContactNodeRequest.fromEnrolment(enrolment, state);
      return this.checkoutApi.getContactNode(request)
        .then((data) => {
          return Promise.resolve(data.enrolments[0])
        })
    } else {
      return Promise.resolve(enrolment);
    }
  };

  public getAmount(state: IshState): Promise<Amount> {
    return this.checkoutApi.getCheckoutModel(BuildCheckoutModelRequest.fromState(state))
      .then((model: CheckoutModel): Promise<Amount> => {
        return Promise.resolve(model.amount)
      });
  }

  public getCheckoutModel(state: IshState): Promise<CheckoutModel> {
    return this.checkoutApi.getCheckoutModel(BuildCheckoutModelRequest.fromState(state));
  }

  public makePayment(values: Values, state: IshState): Promise<PaymentResponse> {
    const request: PaymentRequest = PaymentService.valuesToRequest(values, state);
    return this.checkoutApi.makePayment(request);
  }
}


const {
  contactApi,
  checkoutApi
} = Injector.of();

export class BuildContactNodeRequest {
  static fromEnrolment = (enrolment: Enrolment, state: IshState): ContactNodeRequest => {
    const result: ContactNodeRequest = new ContactNodeRequest();
    result.contactId = enrolment.contactId;
    result.classIds = [enrolment.classId];
    result.promotionIds = state.cart.promotions.result;
    return result;
  };

  static fromState = (state: IshState): ContactNodeRequest => {
    const result: ContactNodeRequest = new ContactNodeRequest();
    result.contactId = state.cart.contact.id;
    result.classIds = state.cart.courses.result;
    result.productIds = state.cart.products.result;
    result.promotionIds = state.cart.promotions.result;
    return result;
  };
}

export class BuildContactFieldsRequest {
  static fromState = (state: IshState): ContactFieldsRequest => {
    const result: ContactFieldsRequest = new ContactFieldsRequest();
    result.contactId = state.checkout.payer.entity.id;
    result.classIds = state.cart.courses.result;
    result.productIds = state.cart.products.result;
    result.fieldSet = FieldSet.enrolment;
    return result;
  }
}

export class BuildSubmitFieldsRequest {
  static fromValues = (fields: ContactFields, values: { [key: string]: any }): SubmitFieldsRequest => {
    const result: SubmitFieldsRequest = new SubmitFieldsRequest();
    result.contactId = fields.contactId;
    result.fields = L.flatMap(fields.headings, (h) => {
      return h.fields
    });
    result.fields.forEach((f: Field) => {
      f.value = values[f.key]
    });
    return result;
  }
}

export class BuildCreateContactParams {
  static fromValues = (values: { [key: string]: string }) => {
    const result: CreateContactParams = new CreateContactParams();
    result.firstName = values['firstName'];
    result.lastName = values['lastName'];
    result.email = values['email'];
    result.fieldSet = FieldSet.enrolment;
    return result;
  }
}

export class BuildContactNodes {
  static fromState = (state: State): ContactNode[] => {
    return state.result.map((contactId) => {
      const result: ContactNode = new ContactNode();
      result.contactId = contactId;
      result.enrolments = state.entities.contacts[contactId].enrolments.map((enrolmentId) => {
        return state.entities.enrolments[enrolmentId];
      });
      return result;
    })
  };
}

export class BuildCheckoutModelRequest {
  static fromState = (state: IshState): CheckoutModelRequest => {
    const result: CheckoutModelRequest = new CheckoutModelRequest();
    result.payerId = state.checkout.payer.entity.id;
    result.promotionIds = state.cart.promotions.result;
    result.contactNodes = BuildContactNodes.fromState(state.checkout.summary);
    return result;
  }
}

export default new CheckoutService(contactApi, checkoutApi);
