import {MockDB} from "../../../../dev/mocks/mocks/MockDB";

import {Contact} from "../../../../js/model/web/Contact";
import {CourseClass} from "../../../../js/model/web/CourseClass";

import {Enrolment} from "../../../../js/model/checkout/Enrolment";
import {PurchaseItems} from "../../../../js/model/checkout/PurchaseItems";
import {IshState} from "../../../../js/services/IshState";

import * as MockFunctions from "../../../../dev/mocks/mocks/MockFunctions";

import {CheckoutModel} from "../../../../js/model/checkout/CheckoutModel";

import {BuildCheckoutModel} from "../../../../js/enrol/services/CheckoutService";
import {Promotion} from "../../../../js/model/web/Promotion";


test('test build CheckoutModel from State', () => {
  const db: MockDB = new MockDB();
  const contact: Contact = db.getContactByIndex(0);
  const courseClass: CourseClass = db.getCourseClassByIndex(0);
  const enrolment: Enrolment = db.createEnrolment(contact.id, courseClass.id);
  const promotion: Promotion = MockFunctions.mockPromotion();

  const items: PurchaseItems = {
    contactId: contact.id,
    enrolments: [enrolment]
  };
  const state: IshState = MockFunctions.mockState(contact, [courseClass], [items], [promotion]);
  const result: CheckoutModel = BuildCheckoutModel.fromState(state);

  expect(result.payerId).toBe(contact.id);
  expect(result.promotionIds[0]).toBe(promotion.id);
  expect(result.purchaseItemsList[0].contactId).toBe(contact.id);
  expect(result.purchaseItemsList[0].enrolments[0].classId).toBe(courseClass.id);

});