import {MockDB} from "../../../../../js/httpStub/mocks/MockDB";

import {Contact} from "../../../../../js/model/web/Contact";
import {CourseClass} from "../../../../../js/model/web/CourseClass";

import {Enrolment} from "../../../../../js/model/checkout/Enrolment";
import {PurchaseItems} from "../../../../../js/model/checkout/PurchaseItems";

import {convert} from "../../../../../js/enrol/containers/summary/reducers/State";
import {IshState} from "../../../../../js/services/IshState";
import {normalize} from "normalizr";
import {ClassesListSchema} from "../../../../../js/NormalizeSchema";


import {ContactPropsBy} from "../../../../../js/enrol/containers/summary/Summary";
import {inspect} from "util";

const db: MockDB = new MockDB();
const contact: Contact = db.getContactByIndex(0);
const courseClass: CourseClass = db.getCourseClassByIndex(0);
const enrolment: Enrolment = db.createEnrolment(contact.id, courseClass.id);

const items: PurchaseItems = {
  contactId: contact.id,
  enrolments: [enrolment]
};

const nCourses = normalize([courseClass], ClassesListSchema);

const state: IshState = {
  checkout: {
    summary: convert([items]),
    payer: {
      entity: contact
    },
    fields: null,
    error: null,
    amount: null,
    phase: null
  },
  cart: null,
  products: null,
  checkoutPath: null,
  form: null,
  popup: null,
  courses: {
    entities: nCourses.entities.classes,
    result: nCourses.result
  }
};


test('test ContactProps By State', () => {
    const props = ContactPropsBy(contact.id, state);
    expect(props.contact.id).toBe(contact.id);
    expect(props.enrolments[0].contact.id).toBe(contact.id);
    expect(props.enrolments[0].courseClass.id).toBe(courseClass.id);
});