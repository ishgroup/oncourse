import {CourseClass} from "../../../js/model/web/CourseClass";
import {Contact} from "../../../js/model/web/Contact";

import faker from "faker";
import {CourseClassPrice} from "../../../js/model/web/CourseClassPrice";
import {Room} from "../../../js/model/web/Room";
import {Amount} from "../../../js/model/checkout/Amount";


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

export const mockContact = (): Contact => {
  return {
    id: faker.random.number() as string,
    firstName: faker.name.firstName(),
    lastName: faker.name.lastName(),
    email: faker.internet.email(),
    uniqueIdentifier: faker.random.alphaNumeric()
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
