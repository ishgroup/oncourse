import * as L from "lodash";
import {ContactFields} from "../../model/field/ContactFields";
import {Injector} from "../../injector";
import {ContactFieldsRequest} from "../../model/field/ContactFieldsRequest";
import {FieldSet} from "../../model/field/FieldSet";
import {SubmitFieldsRequest} from "../../model/field/SubmitFieldsRequest";
import {CreateContactParams} from "../../model/web/CreateContactParams";
import {IshState} from "../../services/IshState";
import {PurchaseItems} from "../../model/checkout/PurchaseItems";
import {ContactApi} from "../../http/ContactApi";
import {CheckoutApi} from "../../http/CheckoutApi";
import {PurchaseItemsRequest} from "../../model/checkout/request/PurchaseItemsRequest";
import {Enrolment} from "../../model/checkout/Enrolment";
import {Field} from "../../model/field/Field";

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
    return this.contactApi.createOrGetContact(BuilderCreateContactParams.fromValues(values));
  };


  public getPurchaseItems = (state: IshState): Promise<PurchaseItems> => {
    return this.checkoutApi.getPurchaseItems(BuildPurchaseItemsRequest.fromState(state));
  };

  public updateEnrolment = (enrolment: Enrolment, state: IshState): Promise<Enrolment> => {
    if (enrolment.selected) {
      const request: PurchaseItemsRequest = BuildPurchaseItemsRequest.fromEnrolment(enrolment, state);
      return this.checkoutApi.getPurchaseItems(request)
        .then((data) => {
          return Promise.resolve(data.enrolments[0])
        })
    } else {
      return Promise.resolve(enrolment);
    }
  }
}


const {
  contactApi,
  checkoutApi
} = Injector.of();

export class BuildPurchaseItemsRequest {
  static fromEnrolment = (enrolment: Enrolment, state: IshState): PurchaseItemsRequest => {
    const result: PurchaseItemsRequest = new PurchaseItemsRequest();
    result.contactId = enrolment.contactId;
    result.classIds = [enrolment.classId];
    result.promotionIds = state.cart.promotions.result;
    return result;
  };

  static fromState = (state: IshState): PurchaseItemsRequest => {
    const result: PurchaseItemsRequest = new PurchaseItemsRequest();
    result.contactId = state.checkout.payer.entity.id;
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

export class BuilderCreateContactParams {
  static fromValues = (values: { [key: string]: string }) => {
    const result: CreateContactParams = new CreateContactParams();
    result.firstName = values['firstName'];
    result.lastName = values['lastName'];
    result.email = values['email'];
    result.fieldSet = FieldSet.enrolment;
    return result;
  }
}

export default new CheckoutService(contactApi, checkoutApi);
