import {CheckoutModel, ContactNode, Contact, CourseClass, Enrolment} from "../../../../js/model";
import {CHANGE_PHASE, SHOW_MESSAGES} from "../../../../js/enrol/actions/Actions";
import {Phase} from "../../../../js/enrol/reducers/State";
import {ADD_CONTACT_NODE_TO_STATE} from "../../../../js/enrol/containers/summary/actions/Actions";
import {ProcessCheckoutModel} from "../../../../js/enrol/epics/EpicProceedToPayment";

import {MockDB} from "../../../../dev/mocks/mocks/MockDB";

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
  enrolments: [enrolment11, enrolment12],
  applications: [],
  articles: [],
  vouchers: [],
  memberships: [],
  waitingLists: [],
};


test('test CheckoutModel processing', () => {

  const model: CheckoutModel = new CheckoutModel();
  model.contactNodes = [];
  model.error = {
    code: 0,
    message: "Common Error Message",
  };
  model.amount = mockAmount();

  model.contactNodes = [node1];

  const actions: any[] = ProcessCheckoutModel.process(model);

  expect(actions[0].type).toBe(CHANGE_PHASE);
  expect(actions[0].payload).toBe(Phase.Summary);
  expect(actions[1].type).toBe(ADD_CONTACT_NODE_TO_STATE);
  expect(actions[1].payload.result.length).toBe(1);
  expect(actions[1].payload.entities.contactNodes[node1.contactId].enrolments.length).toBe(2);
});
