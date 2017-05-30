import * as L from "lodash";
import {CheckoutModel} from "../../../../js/model/checkout/CheckoutModel";
import {CHANGE_PHASE, changePhase, updateAmount, SHOW_MESSAGES,SHOW_MESSAGES_REQUEST} from "../../../../js/enrol/actions/Actions";
import {Phase} from "../../../../js/enrol/reducers/State";

import {CommonError} from "../../../../js/model/common/CommonError";
import {updateContactNode, UPDATE_CONTACT_NODE} from "../../../../js/enrol/containers/summary/actions/Actions";

import {showCommonError} from "../../../../js/enrol/epics/EpicUtils";

import {commonErrorToValidationError} from "../../../../js/common/utils/ErrorUtils";

import {ContactNode} from "../../../../js/model/checkout/ContactNode";
import {inspect} from "util";

import {MockDB} from "../../../../dev/mocks/mocks/MockDB";

import {Contact} from "../../../../js/model/web/Contact";
import {CourseClass} from "../../../../js/model/web/CourseClass";

import {Enrolment} from "../../../../js/model/checkout/Enrolment";

import {mockAmount} from "../../../../dev/mocks/mocks/MockFunctions";

class ProcessCheckoutModel {
  static process = (model: CheckoutModel): any[] => {
    let result = ProcessCheckoutModel.processError(model.error);
    result = [...result, ... ProcessCheckoutModel.processNodes(model.contactNodes)];
    result.push(updateAmount(model.amount));
    return result;
  };

  static processNodes = (nodes: ContactNode[]): any[] => {
    const result = [];
    if (!L.isEmpty(nodes)) {
      nodes.forEach((node: ContactNode) => {
        if (node.enrolments.find((e) => !L.isEmpty(e.errors))) {
          result.push(changePhase(Phase.Summary));
        }
        result.push(updateContactNode(node));
      });
    }
    return result;
  };

  static processError = (error: CommonError): any[] => {
    const result = [];
    if (!L.isNil(error)) {
      result.push(changePhase(Phase.Summary));
      result.push(showCommonError(commonErrorToValidationError(error)))
    }
    return result;
  }
}

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