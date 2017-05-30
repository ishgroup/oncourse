import {CheckoutModel} from "../../../../js/model/checkout/CheckoutModel";
import {CHANGE_PHASE, SHOW_MESSAGES} from "../../../../js/enrol/actions/Actions";
import {Phase} from "../../../../js/enrol/reducers/State";
import {UPDATE_CONTACT_NODE} from "../../../../js/enrol/containers/summary/actions/Actions";
import {ProcessCheckoutModel} from "../../../../js/enrol/epics/EpicProceedToPayment";

import {ContactNode} from "../../../../js/model/checkout/ContactNode";

import {MockDB} from "../../../../dev/mocks/mocks/MockDB";

import {Contact} from "../../../../js/model/web/Contact";
import {CourseClass} from "../../../../js/model/web/CourseClass";

import {Enrolment} from "../../../../js/model/checkout/Enrolment";

import {mockAmount} from "../../../../dev/mocks/mocks/MockFunctions";


const db: MockDB = new MockDB();
const contact1: Contact = db.getContactByIndex(0);
const contact2: Contact = db.getContactByIndex(1);

const courseClass1: CourseClass = db.getCourseClassByIndex(0);
const courseClass2: CourseClass = db.getCourseClassByIndex(1);

const enrolment11: Enrolment = db.createEnrolment(contact1.id, courseClass1.id);
const enrolment12: Enrolment = db.createEnrolment(contact1.id, courseClass2.id);


const node1: ContactNode = {
  contactId: contact1.id,
  enrolments: [enrolment11, enrolment12]
};


test('test CheckoutModel processing', () => {

  const model: CheckoutModel = new CheckoutModel();
  model.contactNodes = [];
  model.error = {
    code: 0,
    message: "Common Error Message"
  };
  model.amount = mockAmount();

  model.contactNodes = [node1];

  let actions: any[] = ProcessCheckoutModel.process(model);

  expect(actions[0].type).toBe(CHANGE_PHASE);
  expect(actions[0].payload).toBe(Phase.Summary);
  expect(actions[1].type).toBe(SHOW_MESSAGES);
  expect(actions[1].payload.formErrors[0]).toBe("Common Error Message");
  expect(actions[2].type).toBe(UPDATE_CONTACT_NODE);
  expect(actions[2].payload.result.length).toBe(1);
  expect(actions[2].payload.entities.contacts[node1.contactId].enrolments.length).toBe(2);
});