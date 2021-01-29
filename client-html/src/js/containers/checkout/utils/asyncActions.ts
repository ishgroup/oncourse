/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import uniqid from "uniqid";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import EntityService from "../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../common/utils/common";
import { CheckoutContact, CheckoutCourse, CheckoutEnrolmentCustom } from "../../../model/checkout";
import { addContact, checkoutAddEnrolments } from "../actions";
import { checkoutUpdateSummaryPrices } from "../actions/checkoutSummary";
import { CheckoutPage } from "../components/CheckoutSelection";
import { CHECKOUT_CONTACT_COLUMNS, CHECKOUT_COURSE_CLASS_COLUMNS, CheckoutCurrentStep } from "../constants";
import CheckoutService from "../services/CheckoutService";
import { checkoutCourseClassMap, checkoutCourseMap } from "./index";

export const processCheckoutWaitingListIds = async (ids: string[], onChangeStep, setActiveField, setCustomLoading, dispatch) => {
  setCustomLoading(true);

  const enrolments: CheckoutEnrolmentCustom[] = [];

  await ids.map(id => () =>
    EntityService.getPlainRecords("WaitingList", "student.contact.id,course.id", `id is ${id}`)
      .then(res => {
        const enrolment: CheckoutEnrolmentCustom = {};
        const courseId = res.rows[0].values[1];
        let plainCourse;

        return EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${res.rows[0].values[0]}`)
          .then(res => {
            const contact = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS))[0];
            enrolment.contactId = contact.id;
            dispatch(addContact(contact));
            return EntityService.getPlainRecords("Course", "code,name,isTraineeship", `id is ${courseId}`);
          })
          .then(res => {
            plainCourse = checkoutCourseMap(res.rows.map(getCustomColumnsMap("code,name,isTraineeship"))[0], true);
            return EntityService.getPlainRecords(
              "CourseClass",
              CHECKOUT_COURSE_CLASS_COLUMNS,
              `course.id is ${courseId} and isCancelled is false and isActive is true and ( (startDateTime < tomorrow and endDateTime >= today and isCancelled is false) or (startDateTime >= tomorrow and endDateTime >= tomorrow and isCancelled is false) )`,
              null,
              0,
              "startDateTime",
              true
            );
          })
          .then(res => {
            if (res.rows.length) {
              const plainClass = [res.rows[0]].map(checkoutCourseClassMap)[0];
              enrolment.courseClass = {
                ...plainCourse,
                courseId: plainCourse.id,
                id: uniqid(),
                price: plainClass.price,
                discount: null,
                discounts: [],
                discountExTax: 0,
                studyReason: "Not stated",
                class: { ...plainClass }
              };
            }
            enrolments.push(enrolment);
          });
      })).reduce(async (a, b) => {
    await a;
    await b();
  }, Promise.resolve());

  const enrolmentsCount = enrolments.filter(en => en.courseClass).length;

  const courseIds = enrolments.map(en => en.courseClass.courseId).toString();

  await enrolments.map(en => () => (en.courseClass
    ? CheckoutService.getContactDiscounts(
        en.contactId,
        en.courseClass.id,
        courseIds,
        "",
        "",
        "",
        enrolmentsCount,
        enrolmentsCount
      )
        .then(res => {
          if (res.length) {
            const discounts = res.map(r => r.discount);
            en.courseClass.discounts = discounts;
            en.courseClass.discount = discounts[0];
          }
        })
    : Promise.resolve()
    )).reduce(async (a, b) => {
    await a;
    await b();
  }, Promise.resolve());

  dispatch(checkoutAddEnrolments(enrolments));
  onChangeStep(CheckoutCurrentStep.summary);
  setActiveField(CheckoutPage.summary);
  dispatch(checkoutUpdateSummaryPrices());
  setCustomLoading(false);
};

export const processCheckoutContactId = (contactId, onSelectHandler, dispatch) => {
  EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${contactId}`)
    .then(res => {
      const contact = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS))[0];
      onSelectHandler(contact, "contact");
    })
    .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to get related contact"));
};

export const mapPlainCourseFromUrlId = (plainCourse, plainClass): CheckoutCourse => ({
  ...plainCourse,
  checked: true,
  paymentPlans: [],
  priceExTax: 0,
  tax: 0,
  taxAmount: 0,
  type: "course",
  courseId: plainCourse.id,
  id: uniqid(),
  price: plainClass.price,
  priceOverriden: null,
  discount: null,
  discounts: [],
  discountExTax: 0,
  studyReason: "Not stated",
  class: { ...plainClass }
});

export const processCheckoutCourseClassId = (
  courseClassId,
  onSelectHandler,
  setSelectedCourse,
  addSelectedItem,
  updateSelectedClass,
  getClassPaymentPlans,
  openItem,
  dispatch
) => {
  let plainClass;
  return EntityService.getPlainRecords(
    "CourseClass",
    CHECKOUT_COURSE_CLASS_COLUMNS + ",course.id",
    `id is ${courseClassId}`,
    null,
    0,
    "startDateTime",
    true
  )
    .then(res => {
      plainClass = res.rows.map(checkoutCourseClassMap)[0];
      return EntityService.getPlainRecords("Course", "code,name,isTraineeship", `id is ${res.rows[0].values[21]}`);
    })
    .then(res => {
      const plainCourse = res.rows.map(getCustomColumnsMap("code,name,isTraineeship"))[0];

      onSelectHandler(plainCourse, "course", true);

      const updatedCourse = mapPlainCourseFromUrlId(plainCourse, plainClass);

      addSelectedItem(updatedCourse);
      updateSelectedClass(updatedCourse);
      getClassPaymentPlans(updatedCourse);
      openItem(updatedCourse, "course");
    })
    .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to get related class"));
};

export const processCheckoutInvoiceId = (invoiceId, onSelectHandler, onChangeStep, dispatch) => {
  EntityService.getPlainRecords("Invoice", "contact.id", `id is ${invoiceId}`)
    .then(res => EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${res.rows[0].values[0]}`))
    .then(res => {
      const contact = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS))[0] as CheckoutContact;
      contact.defaultSelectedOwing = Number(invoiceId);
      onSelectHandler(contact, "contact", true);
      onChangeStep(CheckoutCurrentStep.payment);
    })
    .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to get invoice related contact"));
};

export const processCheckoutEnrolmentId = (
  enrolmentId,
  onSelectHandler,
  dispatch
) => {
  let courseId;
  let courseClassId;
  let payerId;
  let plainCourse;

  EntityService.getPlainRecords("Enrolment", "student.contact.id,courseClass.course.id,courseClass.id,invoiceLines.invoice.contact.id", `id is ${enrolmentId}`)
    .then(res => {
      courseId = res.rows[0].values[1];
      courseClassId = res.rows[0].values[2];
      payerId = JSON.parse(res.rows[0].values[3])[0];
      return EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${res.rows[0].values[0]}`);
    })
    .then(async res => {
      const contact = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS))[0];
      if (payerId !== contact.id) {
        await EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${payerId}`)
          .then(res => {
            const payer = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS))[0];
            onSelectHandler(payer, "contact", true);
          });
      }
      onSelectHandler(contact, "contact", true);
      return EntityService.getPlainRecords("Course", "code,name,isTraineeship", `id is ${courseId}`);
    })
    .then(res => {
      plainCourse = res.rows.map(getCustomColumnsMap("code,name,isTraineeship"))[0];
      plainCourse.transferedClassId = Number(courseClassId);
      onSelectHandler(plainCourse, "course");
    })
    .catch(res => instantFetchErrorHandler(dispatch, res, "Failed to get enrolment related items"));
};
