import {
  CourseClass, Contact, Room, Amount, Field, Item, ContactNode, Product, Discount, Promotion, DataType,
  CourseClassPrice,
} from "../../../js/model";

import faker from "faker";
import {IshState} from "../../../js/services/IshState";
import {normalize} from "normalizr";
import {ClassesListSchema, ContactsSchema} from "../../../js/NormalizeSchema";
import {ContactNodeToState} from "../../../js/enrol/containers/summary/reducers/State";
import {Phase} from "../../../js/enrol/reducers/State";


export const mockDiscount = (): Discount => {
  const result: Discount = new Discount();
  result.id = faker.random.number() as string;
  result.discountedFee = faker.finance.amount();
  result.discountValue = faker.finance.amount();
  result.title = faker.commerce.productName();
  result.expiryDate = faker.date.future();
  return result;
};

export const mockPromotion = (): Promotion => {
  const result: Promotion = {
    id: faker.random.number() as string,
    code: faker.random.alphaNumeric(5).toUpperCase(),
    name: faker.commerce.productName(),
  };
  return result;
};

export const mockCourseClass = (): CourseClass => {
  return {
    id: faker.random.number() as string,
    code: faker.random.number() as string,
    course: {
      code: faker.random.alphaNumeric(5).toUpperCase(),
      name: faker.commerce.productName(),
    },
    start: faker.date.future(),
    end: faker.date.future(),
    distantLearning: false,
    room: mockRoom(),
    price: mockCourseClassPrice(),
    hasAvailablePlaces: true,
    isAllowByApplication: false,
    isCancelled: false,
    isFinished: false,
    isPaymentGatewayEnabled: true,
    availableEnrolmentPlaces: faker.random.number(),
  };
};

export const mockProduct = (): Product => {
  return {
    id: faker.random.number() as string,
    code: faker.random.number() as string,
    name: faker.commerce.productName(),
    isPaymentGatewayEnabled: true,
    canBuy: true,
  };
};

export const mockContact = (): Contact => {
  return {
    id: faker.random.number() as string,
    firstName: faker.name.firstName(),
    lastName: faker.name.lastName(),
    email: faker.internet.email(),
    uniqueIdentifier: faker.random.alphaNumeric(10),
    company: false,
  };
};

export const mockCourseClassPrice = (): CourseClassPrice => {
  return {
    fee: faker.commerce.price(),
    feeOverriden: faker.commerce.price(),
    hasTax: true,
    appliedDiscount: mockDiscount(),
    possibleDiscounts: [mockDiscount(), mockDiscount()],
  };
};

export const mockCourseClassWithFeesRange = (): CourseClassPrice => {
  return {
    fee: faker.commerce.price(),
    feeOverriden: faker.commerce.price(),
    hasTax: true,
    appliedDiscount: mockDiscount(),
    possibleDiscounts: [mockDiscount(), mockDiscount(), mockDiscount(), mockDiscount(), mockDiscount()],
  };
};


export const mockRoom = (): Room => {
  return {
    name: faker.commerce.department(),
    site: {
      name: `${faker.address.city()}, ${faker.company.companyName()}`,
      postcode: faker.address.zipCode(),
      suburb: faker.address.city(),
      street: faker.address.streetAddress(),
    },
  };
};

export const mockAmount = (): Amount => {
  return {
    total: faker.finance.amount(),
    subTotal: faker.finance.amount(),
    owing: faker.finance.amount(),
    discount: faker.finance.amount(),
    payNow: faker.finance.amount(),
    minPayNow: 0,
    voucherPayments: [{redeemVoucherId: '1-100', amount: 555}, {redeemVoucherId: '2-100', amount: 666}],
    isEditable: true,
  };
};

export const mockEnumField = (name: string, key: string, enumType: string, items: Item[]): Field => {
  const r: Field = mockField(name, key, DataType.ENUM);
  r.enumType = enumType;
  r.enumItems = items;
  return r;
};


export const mockField = (name: string, key: string, dateType: DataType, required: boolean = false): Field => {
  return {
    key,
    name,
    id: faker.random.number() as string,
    description: faker.hacker.phrase(),
    mandatory: required,
    dataType: dateType,
    enumType: null,
    value: null,
    defaultValue: null,
    enumItems: [],
    ordering: 0,
  };
};

export const mockState = (contact: Contact,
                          classes: CourseClass[] = [],
                          items: ContactNode[] = [],
                          promotions: Promotion[] = []): IshState => {

  const nCourses = normalize(classes, ClassesListSchema);

  const nPromotions = {
    result: [],
    entities: {},
  };

  promotions.forEach((p: Promotion) => {
    nPromotions.result.push(p.id);
    nPromotions.entities[p.id] = p;
  });

  const state: IshState =
    <IshState>{
      checkout: {
        newContact: false,
        summary: ContactNodeToState(items),
        payerId: contact.id,
        fields: null,
        error: null,
        amount: null,
        phase: null,
        page: Phase.Summary,
        payment: {},
        contacts: normalize([contact], ContactsSchema),
        redeemVouchers: [],
      },
      cart: {
        contact,
        promotions: {
          result: nPromotions.result,
          entities: nPromotions.entities,
        },
        courses: null,
        products: null,
      },
      products: null,
      checkoutPath: null,
      form: null,
      popup: null,
      courses: {
        entities: nCourses.entities.classes,
        result: nCourses.result,
      },
    };
  return state;
};

