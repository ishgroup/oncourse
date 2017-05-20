import {Props as ContactProps} from "../../../js/enrol/containers/summary/components/ContactComp";
import {Props as EnrolmentProps} from "../../../js/enrol/containers/summary/components/EnrolmentComp";
import {CourseClass} from "../../../js/model/web/CourseClass";
import {Enrolment} from "../../../js/model/checkout/Enrolment";
import {Contact} from "../../../js/model/web/Contact";
import {NoCourseClassPlaces} from "../../../js/enrol/containers/summary/Messages";

import faker from "faker";

const mockContact = ():Contact => {
  return {
    id: faker.random.number() as string,
    firstName: faker.name.firstName(),
    lastName: faker.name.lastName(),
    email: faker.internet.email()
  }
};

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
    room: {
      name: faker.commerce.department(),
      site: {
        name: `${faker.address.city()}, ${faker.company.companyName()}`,
        postcode: faker.address.zipCode(),
        suburb: faker.address.city(),
        street: faker.address.streetAddress()
      }
    },
    price: {
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
  }
};

const contacts: Contact[] =
  [
    mockContact(),
    mockContact()
  ];



export const courseClass1: CourseClass = mockCourseClass();
export const courseClass2: CourseClass = mockCourseClass();

export const enrolment1: Enrolment = {
  classId: "1",
  contactId: "1",
  errors: [
    NoCourseClassPlaces
  ],
  price: courseClass1.price
};


export const enrolment1Props: EnrolmentProps = {
  contact: contacts[0],
  enrolment: enrolment1,
  courseClass: courseClass1,
  onSelect: (e, v) => {
  },
  selected: true
};

export const enrolment2Props: EnrolmentProps = {
  contact: contacts[0],
  enrolment: enrolment1,
  courseClass: courseClass2,
  onSelect: (e, v) => {
  },
  selected: true
};


export const contact1Props: ContactProps = {
  contact: contacts[0],
  enrolments: [enrolment1Props, enrolment2Props]
};

export const contact2Props: ContactProps = {
  contact: contacts[1],
  enrolments: []
};

