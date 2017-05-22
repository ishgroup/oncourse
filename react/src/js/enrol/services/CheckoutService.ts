import * as L from "lodash";

import {Contact} from "../../model/web/Contact";
import {CourseClass} from "../../model/web/CourseClass";
import {Product} from "../../model/web/Product";
import {ContactFields} from "../../model/field/ContactFields";
import {Injector} from "../../injector";
import {ContactFieldsRequest} from "../../model/field/ContactFieldsRequest";
import {FieldSet} from "../../model/field/FieldSet";
import {SubmitFieldsRequest} from "../../model/field/SubmitFieldsRequest";
import {CreateContactParams} from "../../model/web/CreateContactParams";
import {CartState, CourseClassCart} from "../../services/IshState";
import {PurchaseItems} from "../../model/checkout/PurchaseItems";
import {Promotion} from "../../model/web/Promotion";
import {ContactApi} from "../../http/ContactApi";
import {CheckoutApi} from "../../http/CheckoutApi";
import {PurchaseItemsRequest} from "../../model/checkout/request/PurchaseItemsRequest";

export class CheckoutSerivce {
  constructor(private contactApi: ContactApi, private checkoutApi: CheckoutApi) {
  }

  public loadFields = (contact: Contact, classes: CourseClass[] = [], products: Product[] = []): Promise<ContactFields> => {
    const request: ContactFieldsRequest = new ContactFieldsRequest();
    request.contactId = contact.id;
    request.classesIds = classes.map((c) => c.id);
    request.fieldSet = FieldSet.enrolment;
    return this.contactApi.getContactFields(new ContactFieldsRequest());
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


  public getPurchaseItems = (cart: CartState): Promise<PurchaseItems> => {
    const request:PurchaseItemsRequest = new PurchaseItemsRequest();
    request.contactId = cart.contact.id;
    request.classIds = cart.courses.result.map((c: CourseClassCart) => c.id);
    request.productIds = cart.products.result.map((p: Product) => p.id);
    request.promotionIds = cart.promotions.result.map((p: Promotion) => p.id);
    return this.checkoutApi.getPurchaseItems(request);
  };
}


const {
  contactApi,
  checkoutApi
} = Injector.of();


export default new CheckoutSerivce(contactApi, checkoutApi);
