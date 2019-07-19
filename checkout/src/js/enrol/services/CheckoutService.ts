import * as L from "lodash";
import {
  ContactFields, ContactFieldsRequest, SubmitFieldsRequest, CreateContactParams, Field, CheckoutModel, Amount,
  ContactNode, CheckoutModelRequest, ContactNodeRequest, PaymentResponse, PaymentRequest, DataType, Suburb,
  GetCorporatePassRequest, ContactId, PaymentStatus, Contact, Concession, CodeResponse, FieldSet, PurchaseItem,
} from "../../model";

import {Injector} from "../../injector";
import {CartState, IshState} from "../../services/IshState";
import {State as SummaryState} from "../containers/summary/reducers/State";
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
import {ProductContainer} from "../../model/checkout/request/ProductContainer";

const DELAY_NEXT_PAYMENT_STATUS: number = 5000;


export class CheckoutService {
  constructor(private contactApi: ContactApi,
              private checkoutApi: CheckoutApi,
              private promotionApi: PromotionApi,
              private corporatePassApi: CorporatePassApi) {
  }

  public cartIsEmpty = (cart: CartState): boolean => {
    return L.isEmpty(cart.courses.result) && L.isEmpty(cart.products.result) && L.isEmpty(cart.waitingCourses.result);
  }

  public isOnlyWaitingCoursesInCart = (cart: CartState) => {
    return cart.waitingCourses.result.length &&
      !cart.courses.result.length &&
      !cart.products.result.length;
  }

  public isOnlyWaitingCourseSelected = (summary: State) => {
    return summary.entities.waitingLists && Object.values(summary.entities.waitingLists).find(e => e.selected)
      && summary.entities.enrolments && !Object.values(summary.entities.enrolments).find(e => e.selected)
      && summary.entities.vouchers && !Object.values(summary.entities.vouchers).find(e => e.selected)
      && summary.entities.articles && !Object.values(summary.entities.articles).find(e => e.selected)
      && summary.entities.memberships && !Object.values(summary.entities.memberships).find(e => e.selected);
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
    return this.contactApi.getContactFields(BuildContactFieldsRequest.fromState(contact, state.cart, state.checkout.newContact, state.checkout.phase));
  }

  public loadFieldsForSelected = (contact: Contact, state: IshState): Promise<ContactFields> => {
    return this.contactApi.getContactFields(BuildContactFieldsRequest.fromStateSelected(contact, state.checkout.summary, state.checkout.phase));
  }

  public submitContactDetails = (values: ContactFields, fields: { [key: string]: any }): Promise<any> => {
    return this.contactApi.submitContactDetails(BuildSubmitFieldsRequest.fromValues(fields, values));
  }

  public createOrGetContact = (values: ContactValues, fieldset?: FieldSet): Promise<ContactId> => {
    return this.contactApi.createOrGetContact(BuildCreateContactParams.fromValues(values, fieldset));
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

  public getUpdateModel = (state: IshState, payNow: number): Promise<CheckoutModel> => {
    let request:CheckoutModelRequest = BuildCheckoutModelRequest.fromState(state);
    request.payNow = payNow;
    return this.checkoutApi.getCheckoutModel(request);
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
      value.status === PaymentStatus.SUCCESSFUL_WAITING_COURSES ||
      value.status === PaymentStatus.UNDEFINED
    );
  }


  public getPaymentStatus = (state: PaymentState): Promise<PaymentResponse> => {
    return this.checkoutApi.getPaymentStatus(state.value.sessionId);
  }


  public createParentChildrenRelation = (parentId, childIds): Promise<any> => (
    this.contactApi.createParentChildrenRelation({parentId, childrenIds: childIds})
  )

  public getCorporatePass = (code: string, state: IshState): Promise<any> => (
    this.corporatePassApi.getCorporatePass(BuildGetCorporatePassRequest.fromState(state, code))
  )

  public haveContactSelectedItems = (contact: Contact, summary: SummaryState): boolean => {
    const request: ContactFieldsRequest = BuildContactFieldsRequest.fromStateSelected(contact, summary);
    return !!(request.classIds.length || request.waitingCourseIds.length || request.products.length);
  }

  public processPaymentResponse = (response: PaymentResponse, actions: any[] = []): IAction<any>[] | Observable<any> => {
    switch (response.status) {
      case PaymentStatus.IN_PROGRESS:
        return of(getPaymentStatus()).delay(DELAY_NEXT_PAYMENT_STATUS);
      case PaymentStatus.SUCCESSFUL:
      case PaymentStatus.SUCCESSFUL_BY_PASS:
      case PaymentStatus.SUCCESSFUL_WAITING_COURSES:
      case PaymentStatus.UNDEFINED:
        return [
          ...actions,
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
    result.products = item.productId ? [{productId: item.productId, quantity:1} as ProductContainer] : [];
    result.promotionIds = state.cart.promotions.result;
    result.waitingCourseIds = state.cart.waitingCourses.result;
    return result;
  }

  static fromState = (cart: CartState): ContactNodeRequest => {
    const result: ContactNodeRequest = new ContactNodeRequest();
    result.contactId = cart.contact.id;
    result.classIds = cart.courses.result;
    result.products = cart.products.result.map(productId => {
      const container = new ProductContainer();
      container.productId = productId;
      container.quantity = 1;
      return container;
    });
    result.promotionIds = cart.promotions.result;
    result.waitingCourseIds = cart.waitingCourses.result;
    return result;
  }

  static fromContact = (contact: Contact, cart: CartState): ContactNodeRequest => {
    const result: ContactNodeRequest = new ContactNodeRequest();
    result.contactId = contact.id;
    result.classIds = cart.courses.result;
    result.products = cart.products.result.map(productId => {
      const container = new ProductContainer();
      container.productId = productId;
      container.quantity = 1;
      return container;
    });
    result.promotionIds = cart.promotions.result;
    result.waitingCourseIds = cart.waitingCourses.result;
    return result;
  }
}

export class BuildContactFieldsRequest {
  static fromState = (contact: Contact, cart: CartState, newContact: boolean, phase?: Phase ): ContactFieldsRequest => {
    const result: ContactFieldsRequest = new ContactFieldsRequest();
    result.contactId = contact.id;
    result.classIds = cart.courses.result;
    result.products = cart.products.result.map(productId => {
      const container = new ProductContainer();
      container.productId = productId;
      container.quantity = 1;
      return container;
    });
    result.waitingCourseIds = cart.waitingCourses.result;
    result.fieldSet = FieldSet.ENROLMENT;
    result.mandatoryOnly = !newContact;
    result.isPayer = phase && Phase[phase] === "AddContactAsPayer";
    result.isParent = phase && Phase[phase] === "AddParent";
    return result;
  }

  static fromStateSelected = (contact: Contact, summary: SummaryState, phase?: Phase): ContactFieldsRequest => {
    const result: ContactFieldsRequest = new ContactFieldsRequest();
    result.contactId = contact.id;
    result.classIds = [];
    result.products = [];
    result.waitingCourseIds = [];
    result.isPayer = phase && Phase[phase] === "AddContactAsPayer";
    result.isParent = phase && Phase[phase] === "AddParent";

    const enrolments = ['enrolments', 'applications'];
    const products = ['vouchers', 'memberships', 'articles'];
    const waitingLists = ['waitingLists'];

    enrolments.forEach(item => Object.values(summary.entities[item])
      .filter(e => e.contactId === contact.id && e.selected)
      .map(e => result.classIds.push(e.classId)));

    products.forEach(item => Object.values(summary.entities[item])
      .filter(e => e.contactId === contact.id && e.selected)
      .map(e => {
        const productContainer: ProductContainer = new ProductContainer();
        productContainer.productId = e.productId;
        productContainer.quantity = e.quantity;
        result.products = [productContainer];
      }));

    waitingLists.forEach(item => Object.values(summary.entities[item])
      .filter(e => e.contactId === contact.id && e.selected)
      .map(e => result.waitingCourseIds.push(e.courseId)));

    result.fieldSet = FieldSet.ENROLMENT;
    result.mandatoryOnly = true;
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
      const value = values[formKey];

      f.value = value && value.key || value || null;
      f.itemValue = null;

      if (f.value == null && f.dataType === DataType.BOOLEAN) {
        f.value = f.key === 'isMale' ? null : 'false';
      }

      if (value && value.suburb && f.dataType === DataType.SUBURB) {
        const suburb: Suburb = {
          postcode: value.postcode,
          state: value.state,
          suburb: value.suburb,
        };

        f.value = null;
        f.itemValue = {key: value.key, value: suburb};
      }
    });

    return result;
  }
}

export class BuildCreateContactParams {
  static fromValues = (values: ContactValues, fieldset: FieldSet) => {
    const result: CreateContactParams = new CreateContactParams();
    result.firstName = values.firstName;
    result.lastName = values.lastName;
    result.email = values.email;
    result.fieldSet = fieldset || FieldSet.ENROLMENT;
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
    result.waitingLists = storage.waitingLists ? storage.waitingLists.map(id => state.entities.waitingLists[id]) : [];
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
    result.payNow = (state.checkout.amount && state.checkout.amount.isEditable && state.checkout.amount.payNow !== state.checkout.amount.minPayNow) ? state.checkout.amount.payNow : null;
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

export class BuildWaitingCoursesResult {
  static fromState = (state: IshState): any => {
    const result = [];
    const nodes = Object.values(state.checkout.summary.entities.contactNodes)
      .map(item => ({
        contactId: item.contactId,
        coursesIds: item.waitingLists,
      }));

    const filteredNodes = nodes
      .map(node => ({
        name: `${state.checkout.contacts.entities.contact[node.contactId].firstName} ${state.checkout.contacts.entities.contact[node.contactId].lastName}`,
        courses: node.coursesIds.map(id => state.checkout.summary.entities.waitingLists[id]).filter(c => c.selected),
      }))

    filteredNodes.map(n => result.push({
      name: n.name,
      courses: n.courses.map(c => ({
        name: state.cart.waitingCourses.entities[c.courseId].name,
        count: c.studentsCount,
      })),
    }));

    return result;
  }
}

export default new CheckoutService(contactApi, checkoutApi, promotionApi, corporatePassApi);
