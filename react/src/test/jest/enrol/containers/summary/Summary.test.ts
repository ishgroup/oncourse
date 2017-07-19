import {MockDB} from "../../../../../dev/mocks/mocks/MockDB";

import {Contact, CourseClass, Enrolment, ContactNode} from "../../../../../js/model";

import {IshState} from "../../../../../js/services/IshState";

import {ContactPropsBy} from "../../../../../js/enrol/containers/summary/Summary";

import * as MockFunctions from "../../../../../dev/mocks/mocks/MockFunctions";

import {inspect} from "util";

const db: MockDB = new MockDB();
const contact: Contact = db.getContactByIndex(0);
const courseClass: CourseClass = db.getCourseClassByIndex(0);
const enrolment: Enrolment = db.createEnrolment(contact.id, courseClass.id);

const items: ContactNode = {
  contactId: contact.id,
  enrolments: [enrolment]
};

const state: IshState = MockFunctions.mockState(contact, [courseClass], [items]);
console.log(inspect(state, false, 10, true));

test('test ContactProps By State', () => {
  const props = ContactPropsBy(contact.id, state);
  expect(props.contact.id).toBe(contact.id);
  expect(props.enrolments[0].contact.id).toBe(contact.id);
  expect(props.enrolments[0].courseClass.id).toBe(courseClass.id);
});