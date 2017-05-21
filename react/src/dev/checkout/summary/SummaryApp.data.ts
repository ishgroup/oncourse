import {Props as ContactProps} from "../../../js/enrol/containers/summary/components/ContactComp";
import {Props as EnrolmentProps} from "../../../js/enrol/containers/summary/components/EnrolmentComp";
import {CourseClass} from "../../../js/model/web/CourseClass";
import {Enrolment} from "../../../js/model/checkout/Enrolment";
import {Contact} from "../../../js/model/web/Contact";

import faker from "faker";
import {CourseClassPrice} from "../../../js/model/web/CourseClassPrice";
import {Room} from "../../../js/model/web/Room";
import {NoCourseClassPlaces} from "../../../js/enrol/containers/summary/Messages";
import {Amount} from "../../../js/model/checkout/Amount";

const mockCourseClass = (): CourseClass => {
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


const mockContact = (): Contact => {
  return {
    id: faker.random.number() as string,
    firstName: faker.name.firstName(),
    lastName: faker.name.lastName(),
    email: faker.internet.email()
  }
};

const mockCourseClassPrice = (): CourseClassPrice => {
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

const mockRoom = (): Room => {
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


const contacts: Contact[] =
  [
    mockContact(),
    mockContact()
  ];

const classes: CourseClass[] = [
  mockCourseClass(),
  mockCourseClass(),
  mockCourseClass(),
  mockCourseClass()
];

export const createEnrolment = (ci: number, cci: number): Enrolment => {
  return {
    contactId: contacts[ci].id,
    classId: classes[cci].id,
    errors: [],
    warnings: [],
    price: classes[cci].price,
  }
};

const enrolments: Enrolment[] = [
  createEnrolment(0,0),
  createEnrolment(0,1),
  createEnrolment(1,2),
  createEnrolment(1,3),
];

enrolments[0].errors = [NoCourseClassPlaces];


const createEnrolmentProps = (e: Enrolment):EnrolmentProps => {
  return {
      contact: contacts.find((c) => c.id == e.contactId),
      courseClass: classes.find((c) => c.id == e.classId),
      onSelect: (e,i) => {},
      selected: e.errors.length == 0,
      enrolment: e
  };
};

const createContactProps = (contact: Contact):ContactProps => {
  return {
    contact: contact,
    enrolments: enrolments.filter((e) => e.contactId == contact.id).map(createEnrolmentProps),
  }
};

export const contactPropses: ContactProps[] = contacts.map(createContactProps);


contactPropses[1].enrolments[0].selected = false;
contactPropses[1].enrolments[1].courseClass.start = faker.date.past();

export const amount: Amount = {
  total: faker.finance.amount(),
  owing: faker.finance.amount(),
  discount: faker.finance.amount(),
  payNow: faker.finance.amount()
};
