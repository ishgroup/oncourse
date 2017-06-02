import {CourseClass} from "../../../js/model/web/CourseClass";
import {Contact} from "../../../js/model/web/Contact";

import faker from "faker";
import {CourseClassPrice} from "../../../js/model/web/CourseClassPrice";
import {Room} from "../../../js/model/web/Room";
import {Amount} from "../../../js/model/checkout/Amount";
import {Field} from "../../../js/model/field/Field";
import {DataType} from "../../../js/model/field/DataType";
import {Item} from "../../../js/model/common/Item";
import {IshState} from "../../../js/services/IshState";
import {normalize} from "normalizr";
import {ClassesListSchema} from "../../../js/NormalizeSchema";
import {ContactNode} from "../../../js/model/checkout/ContactNode";
import {ContactNodeToState} from "../../../js/enrol/containers/summary/reducers/State";
import {Promotion} from "../../../js/model/web/Promotion";
import {Product} from "../../../js/model/web/Product";
import {ProductClass} from "../../../js/model/web/ProductClass";
import {State as PaymentState} from "../../../js/enrol/containers/payment/reducers/State";

export const mockPromotion = (): Promotion => {
  const result: Promotion = {
    id: faker.random.number() as string,
    code: faker.random.alphaNumeric(5).toUpperCase(),
    name: faker.commerce.productName()
  };
  return result;
};

export const mockCourseClass = (): CourseClass => {
  return {
    id: faker.random.number() as string,
    code: faker.random.number() as string,
    course: {
      code: faker.random.alphaNumeric(5).toUpperCase(),
      name: faker.commerce.productName()
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
    availableEnrolmentPlaces: faker.random.number()
  }
};

export const mockProductClass = (): ProductClass => {
  return {
    id: faker.random.number() as string,
    code: faker.random.number() as string,
    product: {
      code: faker.random.alphaNumeric(5).toUpperCase(),
      name: faker.commerce.productName()
    },
    price: faker.commerce.price(),
    isCancelled: false,
    isPaymentGatewayEnabled: true,
    canBuy: true
  }
};

export const mockContact = (): Contact => {
  return {
    id: faker.random.number() as string,
    firstName: faker.name.firstName(),
    lastName: faker.name.lastName(),
    email: faker.internet.email(),
    uniqueIdentifier: faker.random.alphaNumeric(10)
  }
};

export const mockCourseClassPrice = (): CourseClassPrice => {
  return {
    fee: faker.commerce.price(),
    feeOverriden: faker.commerce.price(),
    hasTax: true,
    appliedDiscount: {
      id: faker.random.number() as string,
      discountedFee: faker.finance.amount(),
      discountValue: faker.finance.amount(),
      title: faker.commerce.productName(),
      expiryDate: faker.date.future()
    }
  }
};

export const mockRoom = (): Room => {
  return {
    name: faker.commerce.department(),
    site: {
      name: `${faker.address.city()}, ${faker.company.companyName()}`,
      postcode: faker.address.zipCode(),
      suburb: faker.address.city(),
      street: faker.address.streetAddress()
    }
  }
};

export const mockAmount = (): Amount => {
  return {
    total: faker.finance.amount(),
    owing: faker.finance.amount(),
    discount: faker.finance.amount(),
    payNow: faker.finance.amount()
  }
};

export const mockEnumField = (name: string, key: string, enumType: string, items: Item[]): Field => {
  const r: Field = mockField(name, key, DataType.ENUM);
  r.enumType = enumType;
  r.enumItems = items;
  return r;
};


export const mockField = (name: string, key: string, dateType: DataType): Field => {
  return {
    id: faker.random.number() as string,
    key: key,
    name: name,
    description: faker.hacker.phrase(),
    mandatory: true,
    dataType: dateType,
    enumType: null,
    value: null,
    defaultValue: null,
    enumItems: [],
    ordering: 0
  }
};

export const mockState = (contact: Contact,
                          classes: CourseClass[] = [],
                          items: ContactNode[] = [],
                          promotions: Promotion[] = []): IshState => {

  const nCourses = normalize(classes, ClassesListSchema);

  const nPromotions = {
    result: [],
    entities: {}
  };

  promotions.forEach((p: Promotion) => {
    nPromotions.result.push(p.id);
    nPromotions.entities[p.id] = p
  });

  const state: IshState =
    {
      checkout: {
        newContact: false,
        summary: ContactNodeToState(items),
        payer: {
          entity: contact
        },
        fields: null,
        error: null,
        amount: null,
        phase: null,
        payment: {}
      },
      cart: {
        contact: contact,
        promotions: {
          result: nPromotions.result,
          entities: nPromotions.entities
        },
        courses: null,
        products: null
      },
      products: null,
      checkoutPath: null,
      form: null,
      popup: null,
      courses: {
        entities: nCourses.entities.classes,
        result: nCourses.result
      }
    };
  return state;
};




