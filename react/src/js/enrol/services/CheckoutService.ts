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
import {State} from "../containers/summary/reducers/State";

export class CheckoutSerivce {
  constructor(private contactApi: ContactApi, private checkoutApi: CheckoutApi) {
  }

  public loadFields = (state: IshState): Promise<ContactFields> => {
    const request: ContactFieldsRequest = new ContactFieldsRequest();
    request.contactId = state.checkout.payer.entity.id;
    request.classIds = state.cart.courses.result;
    request.productIds = state.cart.products.result;
    request.fieldSet = FieldSet.enrolment;
    return this.contactApi.getContactFields(request);
  };

  public submitContactDetails = (fields: ContactFields, values: any): Promise<any> => {
    const request: SubmitFieldsRequest = new SubmitFieldsRequest();
    request.contactId = fields.contactId;
    request.fields = L.flatMap(fields.headings, (h) => {
      return h.fields
    });
    return this.contactApi.submitContactDetails(request);
  };

  public createOrGetContact = (values: any): Promise<string> => {
    const request: CreateContactParams = Object.assign({}, values, {fieldSet: FieldSet.enrolment});
    return this.contactApi.createOrGetContact(request);
  };


  public getPurchaseItems = (state: IshState): Promise<PurchaseItems> => {
    const request: PurchaseItemsRequest = new PurchaseItemsRequest();
    request.contactId = state.checkout.payer.entity.id;
    request.classIds = state.cart.courses.result;
    request.productIds = state.cart.products.result;
    request.promotionIds = state.cart.promotions.result;
    return this.checkoutApi.getPurchaseItems(request);
  };

  public updateEnrolment = (enrolment: Enrolment, state: IshState): Promise<Enrolment> => {
    if (enrolment.selected) {
      const request: PurchaseItemsRequest = new PurchaseItemsRequest();
      request.contactId = enrolment.contactId;
      request.classIds = [enrolment.classId];
      request.promotionIds = state.cart.promotions.result;
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


export default new CheckoutSerivce(contactApi, checkoutApi);
