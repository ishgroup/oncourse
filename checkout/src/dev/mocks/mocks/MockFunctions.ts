import {
  CourseClass,
  Contact,
  Room,
  Amount,
  Field,
  Item,
  ContactNode,
  Product,
  Discount,
  Promotion,
  DataType,
  CourseClassPrice,
  RedeemVoucherProduct,
  Enrolment,
  Voucher,
  FieldHeading,
  Writable,
} from "../../../js/model";
import faker from "faker";
import {IshState} from "../../../js/services/IshState";
import { normalize } from "normalizr";
import {ClassesListSchema, ContactsSchema} from "../../../js/NormalizeSchema";
import {ContactNodeToState} from "../../../js/enrol/containers/summary/reducers/State";
import {Phase} from "../../../js/enrol/reducers/State";
import {Course} from "../../../js/model/web/Course";
import { Type } from '../../../js/model/web/product/Type';

export const mockArray = (mockFunction: Function, max: number = 5): any[] => {
  const result = [];
  for (let i = 0; i < faker.datatype.number({ min: 1, max }); i++) {
    result.push(mockFunction());
  }
  return result;
}

export const mockDiscount = (): Discount => {
  const result: Discount = new Discount();
  result.id = faker.datatype.number().toString();
  result.discountedFee = faker.datatype.float();
  result.discountValue = faker.datatype.float();
  result.title = faker.commerce.productName();
  result.expiryDate = faker.date.future().toString().toString();
  return result;
};

export const mockPromotion = (): Promotion => ({
  id: faker.datatype.number().toString(),
  code: faker.random.alphaNumeric(5).toUpperCase(),
  name: faker.commerce.productName(),
});

export const mockCourseClass = (): CourseClass => {
  return {
    id: faker.datatype.number().toString(),
    code: faker.datatype.number().toString(),
    course: {
      id: faker.datatype.number().toString(),
      code: faker.random.alphaNumeric(5).toUpperCase(),
      name: faker.commerce.productName(),
    },
    start: faker.date.future().toString(),
    end: faker.date.future().toString(),
    distantLearning: false,
    room: mockRoom(),
    price: mockCourseClassPrice(),
    hasAvailablePlaces: true,
    isAllowByApplication: false,
    isCancelled: false,
    isFinished: false,
    isPaymentGatewayEnabled: true,
    availableEnrolmentPlaces: faker.datatype.number(),
    timezone: 'Australia/Sydney',
  };
};

export const mockWaitingCourse = (): Course => {
  return {
    id: faker.datatype.number().toString(),
    code: faker.datatype.number().toString(),
    name: faker.commerce.productName(),
  };
};

export const mockProduct = (type?: Type): Product => {
  return {
    id: faker.datatype.number().toString(),
    code: faker.datatype.number().toString(),
    name: faker.commerce.productName(),
    isPaymentGatewayEnabled: true,
    canBuy: true,
    type: type || faker.random.arrayElement(Object.keys(Type)) as Type,
    allowRemove: faker.datatype.boolean(),
    relatedClassId: faker.datatype.number().toString()
  };
};

export const mockVoucher = (): Voucher => ({
  contactId: faker.datatype.number().toString(),
  productId: faker.datatype.number().toString(),
  warnings: [],
  errors: [],
  price: faker.datatype.float(),
  total: faker.datatype.float(),
  value: faker.datatype.float(),
  classes: [],
  selected: faker.datatype.boolean(),
  isEditablePrice: faker.datatype.boolean(),
  quantity: 1,
  allowRemove: true,
  relatedClassId: null,
  relatedProductId: null,
  fieldHeadings: []
})

export const mockRedeemVoucherProduct = (): RedeemVoucherProduct => ({
  id: faker.datatype.number().toString(),
  name: faker.commerce.productName(),
  enabled: faker.datatype.boolean(),
  amount: faker.datatype.float()
})

export const mockContact = (): Contact => {
  return {
    id: faker.datatype.number().toString(),
    firstName: faker.name.firstName(),
    lastName: faker.name.lastName(),
    email: faker.internet.email(),
    uniqueIdentifier: faker.random.alphaNumeric(10),
    company: false,
  };
};

export const mockEnrolment = (contactId?: string): Enrolment => ({
  contactId: contactId || faker.datatype.number().toString(),
  classId: faker.datatype.number().toString(),
  courseId: faker.datatype.number().toString(),
  price: {
    fee: faker.datatype.float(),
    feeOverriden: null,
    appliedDiscount: null,
    possibleDiscounts: [],
    hasTax: faker.datatype.boolean()
  },
  warnings: [],
  errors: [],
  selected: faker.datatype.boolean(),
  fieldHeadings: mockArray(mockFieldHeading),
  allowRemove: faker.datatype.boolean(),
  relatedClassId: null,
  relatedProductId: null,
})

export const mockContactNode = (): ContactNode => {
  const contactId = faker.datatype.number().toString();
  return {
    contactId,
    enrolments: [],
    applications: [],
    articles: [],
    memberships: [],
    vouchers: [],
    waitingLists: [],
    suggestedCourseIds: [],
    suggestedProductIds: [],
  }
}

export const mockCourseClassPrice = (): CourseClassPrice => {
  return {
    fee: faker.datatype.float(),
    feeOverriden: faker.datatype.float(),
    hasTax: true,
    appliedDiscount: mockDiscount(),
    possibleDiscounts: [mockDiscount(), mockDiscount()],
  };
};

export const mockCourseClassWithFeesRange = (): CourseClassPrice => {
  return {
    fee: faker.datatype.float(),
    feeOverriden: faker.datatype.float(),
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
    total: faker.datatype.float(),
    subTotal: faker.datatype.float(),
    owing: faker.datatype.float(),
    discount: faker.datatype.float(),
    payNow: faker.datatype.float(),
    minPayNow: 0,
    voucherPayments: [{redeemVoucherId: '1-100', amount: 555}, {redeemVoucherId: '2-100', amount: 666}],
    voucherProductPayments: [{redeemVoucherProductId: '2-100', amount: 555}, {redeemVoucherProductId: '3-100', amount: 666}],
    isEditable: true,
  };
};

export const mockEnumField = (name: string, key: string, enumType: string, items: Item[]): Field => {
  const r: Field = mockField(name, key, DataType.ENUM, false);
  r.enumType = enumType;
  r.enumItems = items;
  return r;
};

export const mockChoiceField = (name: string, key: string, enumType: string, items: Item[], required: boolean = false, defaultVal = null): Field => {
  const r: Field = mockField(name, key, DataType.CHOICE, true, defaultVal);
  r.enumType = enumType;
  r.enumItems = items;
  return r;
};


export const mockFieldHeading = (): FieldHeading => ({
  name: faker.datatype.string(),
  description: faker.commerce.productDescription(),
  fields: [],
  ordering: faker.datatype.number()
})

export const mockField = (name: string, key: string, dateType: DataType, required: boolean = false, defaultVal = null): Field => {
  return {
    key,
    name,
    id: faker.datatype.number().toString(),
    description: faker.hacker.phrase(),
    mandatory: required,
    dataType: dateType,
    enumType: null,
    value: null,
    defaultValue: defaultVal,
    enumItems: [],
    ordering: 0,
  };
};

export const mockState = (contact: Contact = mockContact(),
                          classes: CourseClass[] = [],
                          items: ContactNode[] = [],
                          promotions: Promotion[] = []):  Writable<IshState> => {

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
        amount: mockAmount(),
        phase: null,
        page: Phase.Summary,
        payment: {},
        contacts: normalize([contact], ContactsSchema),
        redeemVouchers: [],
        redeemedVoucherProducts: []
      },
      cart: {
        contact,
        promotions: {
          result: nPromotions.result,
          entities: nPromotions.entities,
        },
        courses: {
          entities: nCourses.entities.classes,
          result: nCourses.result,
        },
        products: {
          entities: {},
          result: [],
        },
      },
      products: {
        entities: {},
        result: [],
      },
      form: null,
      popup: null,
      courses: {
        entities: nCourses.entities.classes,
        result: nCourses.result,
      },
    };
  return state;
};

export const stubFunction = () => null;

