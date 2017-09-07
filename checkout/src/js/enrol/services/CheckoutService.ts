import * as L from "lodash";
import {
  ContactFields, ContactFieldsRequest, SubmitFieldsRequest, CreateContactParams, Field, CheckoutModel, Amount,
  ContactNode, CheckoutModelRequest, ContactNodeRequest, PaymentResponse, PaymentRequest, DataType,
  GetCorporatePassRequest, ContactId, PaymentStatus, Contact, Concession, CodeResponse, FieldSet, PurchaseItem,
} from "../../model";

import {Injector} from "../../injector";
import {CartState, IshState} from "../../services/IshState";
import {ContactApi} from "../../http/ContactApi";
import {CheckoutApi} from "../../http/CheckoutApi";
import {ContactNodeStorage, State} from "../containers/summary/reducers/State";
import {
  PaymentService, CreditCardFormValues,
  CorporatePassFormValues,
} from "../containers/payment/services/PaymentService";
import {CheckoutState, Phase} from "../reducers/State";
import {Values as ContactValues} from "../containers/contact-add/actions/Actions";
import {State as PaymentState} from "../containers/payment/reducers/State";
import {IAction} from "../../actions/IshAction";
import {Observable} from "rxjs/Observable";
import {of} from "rxjs/observable/of";
import {getPaymentStatus, updatePaymentStatus} from "../containers/payment/actions/Actions";
import {changePhase, finishCheckoutProcess} from "../actions/Actions";
import {ContactNodeService} from "./ContactNodeService";
import {PromotionApi} from "../../http/PromotionApi";
import {CorporatePassApi} from "../../http/CorporatePassApi";
import {toFormKey} from "../../components/form/FieldFactory";

const DELAY_NEXT_PAYMENT_STATUS: number = 5000;


export class CheckoutService {
  constructor(private contactApi: ContactApi,
              private checkoutApi: CheckoutApi,
              private promotionApi: PromotionApi,
              private corporatePassApi: CorporatePassApi) {
  }

  public cartIsEmpty = (cart: CartState): boolean => {
    return L.isEmpty(cart.courses.result) && L.isEmpty(cart.products.result);
  }

  public ifCodeExist = (code, state): boolean => {
    const promotions = state.cart.promotions.entities;
    const inPromotions = Object.keys(promotions).filter(key => promotions[key].code === code).length;
    const inRedeemVouchers = state.checkout.redeemVouchers.filter(v => v.key === code).length;
    return !!(inPromotions || inRedeemVouchers);
  }

  public hasPayer = (state: CheckoutState): boolean => {
    return !L.isNil(state.payerId);
  }

  public getAllSingleChildIds = (state: CheckoutState): string[] => (
    state.contacts.result.filter(id => state.contacts.entities.contact[id].parentRequired)
  )

  public hasActiveVoucherPayer = (state: CheckoutState): boolean => {
    return !!state.redeemVouchers.filter(v => v.payer && v.enabled).length;
  }

  public hasCartContact = (cart: CartState): boolean => {
    return Object.keys(cart.contact).length !== 0;
  }

  public loadFields = (contact: Contact, state: IshState): Promise<ContactFields> => {
    return this.contactApi.getContactFields(BuildContactFieldsRequest.fromState(contact, state.cart, state.checkout.newContact));
  }

  public submitContactDetails = (values: ContactFields, fields: { [key: string]: any }): Promise<any> => {
    return this.contactApi.submitContactDetails(BuildSubmitFieldsRequest.fromValues(fields, values));
  }

  public createOrGetContact = (values: ContactValues): Promise<ContactId> => {
    return this.contactApi.createOrGetContact(BuildCreateContactParams.fromValues(values));
  }


  public getContactNode = (contact: Contact, cart: CartState): Promise<ContactNode> => {
    return this.checkoutApi.getContactNode(BuildContactNodeRequest.fromContact(contact, cart));
  }

  public submitCode = (code: string, state: IshState): Promise<CodeResponse> => {
    return this.promotionApi.submitCode(code);
  }

  public getConcessionTypes = () => {
    return this.contactApi.getConcessionTypes();
  }

  public getContactConcessionsAndMemberships = (payload, state: IshState) => {
    return this.contactApi.getContactConcessionsAndMemberships(state.checkout.contacts.result);
  }

  public submitConcession = (payload, props) => {
    return this.contactApi.submitConcession(BuildConcessionRequest.fromValues(payload, props));
  }

  public updateItem = (item: PurchaseItem, state: IshState): Promise<PurchaseItem> => {
    if (item.selected) {
      const request: ContactNodeRequest = BuildContactNodeRequest.fromPurchaseItem(item, state);
      return this.checkoutApi.getContactNode(request)
        .then(data => {
          return Promise.resolve(ContactNodeService.getPurchaseItem(data, item));
        });
    } else {
      return Promise.resolve(item);
    }
  }

  public getAmount = (state: IshState): Promise<Amount> => {
    return this.checkoutApi.getCheckoutModel(BuildCheckoutModelRequest.fromState(state))
      .then((model: CheckoutModel): Promise<Amount> => {
        return Promise.resolve(model.amount);
      });
  }

  public getCheckoutModel = (state: IshState): Promise<CheckoutModel> => {
    return this.checkoutApi.getCheckoutModel(BuildCheckoutModelRequest.fromState(state));
  }

  public makePayment = (values: CreditCardFormValues, state: IshState): Promise<PaymentResponse> => {
    const request: PaymentRequest = PaymentService.creditFormValuesToRequest(values, state);
    return this.checkoutApi.makePayment(request);
  }

  public submitPaymentCorporatePass = (values: CorporatePassFormValues, state): Promise<any> => {
    const request: any = PaymentService.corporatePassValuesToRequest(values, state);
    return this.corporatePassApi.makeCorporatePass(request);
  }

  public isFinalStatus = (value: PaymentResponse): boolean => {
    return (
      value.status === PaymentStatus.SUCCESSFUL ||
      value.status === PaymentStatus.SUCCESSFUL_BY_PASS ||
      value.status === PaymentStatus.UNDEFINED
    );
  }


  public getPaymentStatus = (state: PaymentState): Promise<PaymentResponse> => {
    return this.checkoutApi.getPaymentStatus(state.value.sessionId);
  }

  public validatePayNow = (amount: Amount) => {
    const errors = [];

    if (Number(amount.payNow) < Number(amount.minPayNow) || Number(amount.payNow) > Number(amount.subTotal)) {
      errors.push(`You must pay at least $${amount.minPayNow} and not more than $${amount.subTotal}.`);
    }

    return errors;
  }

  public createParentChildrenRelation = (parentId, childIds): Promise<any> => (
    this.contactApi.createParentChildrenRelation({parentId, childrenIds: childIds})
  )

  public changeParent = (parentId, childId): Promise<any> => (
    this.contactApi.changeParent({parentId, childId})
  )

  public getCorporatePass = (code: string, state: IshState): Promise<any> => (
    this.corporatePassApi.getCorporatePass(BuildGetCorporatePassRequest.fromState(state, code))
  )

  public checkIfCorporatePassEnabled = (): Promise<any> => (
    this.corporatePassApi.isCorporatePassEnabled()
  )

  public processPaymentResponse = (response: PaymentResponse): IAction<any>[] | Observable<any> => {
    switch (response.status) {
      case PaymentStatus.IN_PROGRESS:
        return of(getPaymentStatus()).delay(DELAY_NEXT_PAYMENT_STATUS);
      case PaymentStatus.SUCCESSFUL:
      case PaymentStatus.SUCCESSFUL_BY_PASS:
      case PaymentStatus.UNDEFINED:
        return [
          changePhase(Phase.Result),
          updatePaymentStatus(response),
          finishCheckoutProcess(response),
        ];
      case PaymentStatus.FAILED:
        return [
          changePhase(Phase.Result),
          updatePaymentStatus(response),
        ];
      default:
        throw new Error(`Unknown status ${response.status}`);
    }
  }

}

const {
  contactApi,
  promotionApi,
  checkoutApi,
  corporatePassApi,
} = Injector.of();

export class BuildContactNodeRequest {
  static fromPurchaseItem = (item: any, state: IshState): ContactNodeRequest => {
    const result: ContactNodeRequest = new ContactNodeRequest();
    result.contactId = item.contactId;
    result.classIds = item.classId ? [item.classId] : [];
    result.productIds = item.productId ? [item.productId] : [];
    result.promotionIds = state.cart.promotions.result;
    return result;
  }

  static fromState = (cart: CartState): ContactNodeRequest => {
    const result: ContactNodeRequest = new ContactNodeRequest();
    result.contactId = cart.contact.id;
    result.classIds = cart.courses.result;
    result.productIds = cart.products.result;
    result.promotionIds = cart.promotions.result;
    return result;
  }

  static fromContact = (contact: Contact, cart: CartState): ContactNodeRequest => {
    const result: ContactNodeRequest = new ContactNodeRequest();
    result.contactId = contact.id;
    result.classIds = cart.courses.result;
    result.productIds = cart.products.result;
    result.promotionIds = cart.promotions.result;
    return result;
  }
}

export class BuildContactFieldsRequest {
  static fromState = (contact: Contact, cart: CartState, newContact: boolean): ContactFieldsRequest => {
    const result: ContactFieldsRequest = new ContactFieldsRequest();
    result.contactId = contact.id;
    result.classIds = cart.courses.result;
    result.productIds = cart.products.result;
    result.fieldSet = FieldSet.ENROLMENT;
    result.mandatoryOnly = !newContact;
    return result;
  }
}

export class BuildConcessionRequest {
  static fromValues = (values, state) => {
    const result: Concession = new Concession();
    result.contactId = state.contact.id;
    result.concessionTypeId = values.concessionType.key;
    result.expiryDate = values.expiryDate || null;
    result.number = values.number || null;
    return result;
  }
}

export class BuildSubmitFieldsRequest {
  static fromValues = (fields: ContactFields, values: { [key: string]: any }): SubmitFieldsRequest => {
    const result: SubmitFieldsRequest = new SubmitFieldsRequest();
    result.contactId = fields.contactId;
    result.fields = L.flatMap(fields.headings, h => {
      return h.fields;
    });
    result.fields.forEach((f: Field) => {
      const formKey = toFormKey(f.key);

      f.value = values[formKey] && values[formKey].key || values[formKey] || null;
      if (f.value == null && f.dataType === DataType.BOOLEAN) {
        f.value = 'false';
      }
    });

    if (values.concessionType && values.concessionType.key !== "-1") {
      result.concession = {
        concessionTypeId: values.concessionType.key,
        contactId: fields.contactId,
        expiryDate: values.expiryDate || null,
        number: values.number || null,
      };
    }

    return result;
  }
}

export class BuildCreateContactParams {
  static fromValues = (values: ContactValues) => {
    const result: CreateContactParams = new CreateContactParams();
    result.firstName = values.firstName;
    result.lastName = values.lastName;
    result.email = values.email;
    result.fieldSet = FieldSet.ENROLMENT;
    result.company = values.company || false;
    return result;
  }
}

export class BuildContactNodes {
  static fromState = (state: State): ContactNode[] => {
    return state.result.map(contactId => {
      return BuildContactNodes.contactNodeBy(state.entities.contactNodes[contactId], state);
    });
  }

  private static contactNodeBy = (storage: ContactNodeStorage, state: State): ContactNode => {
    const result: ContactNode = new ContactNode();
    result.contactId = storage.contactId;
    result.enrolments = storage.enrolments ? storage.enrolments.map(id => state.entities.enrolments[id]) : [];
    result.applications = storage.applications ? storage.applications.map(id => state.entities.applications[id]) : [];
    result.memberships = storage.memberships ? storage.memberships.map(id => state.entities.memberships[id]) : [];
    result.articles = storage.articles ? storage.articles.map(id => state.entities.articles[id]) : [];
    result.vouchers = storage.vouchers ? storage.vouchers.map(id => state.entities.vouchers[id]) : [];
    return result;
  }
}

export class BuildCheckoutModelRequest {
  static fromState = (state: IshState): CheckoutModelRequest => {
    const result: CheckoutModelRequest = new CheckoutModelRequest();
    result.payerId = state.checkout.payerId;
    result.promotionIds = state.cart.promotions.result;
    result.redeemedVoucherIds = state.checkout.redeemVouchers.filter(v => v.enabled).map(v => v.id);
    result.contactNodes = BuildContactNodes.fromState(state.checkout.summary);
    result.corporatePassId = (state.checkout.payment.corporatePass && state.checkout.payment.corporatePass.id) || null;
    return result;
  }
}

export class BuildGetCorporatePassRequest {
  static fromState = (state: IshState, code: string): GetCorporatePassRequest => {
    const result: GetCorporatePassRequest = new GetCorporatePassRequest();
    const entities = state.checkout.summary.entities;
    const products = ['memberships', 'vouchers', 'articles'];
    const productIds = [];

    const classIds = Array.from(new Set(Object.keys(entities.enrolments)
      .filter(key => entities.enrolments[key].selected)
      .map(key => entities.enrolments[key].classId)));

    products.map(p => Object.keys(entities[p])
      .filter(key => entities[p][key].selected)
      .map(key => productIds.push(entities[p][key].productId)));

    result.classIds = classIds;
    result.productIds = productIds;
    result.code = code;

    return result;
  }
}

export default new CheckoutService(contactApi, checkoutApi, promotionApi, corporatePassApi);
