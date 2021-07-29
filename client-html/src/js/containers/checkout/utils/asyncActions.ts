/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ProductType } from "@api/model";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import EntityService from "../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../common/utils/common";
import {
  CheckoutContact,
  CheckoutCourse,
  CheckoutEnrolmentCustom,
  CheckoutProductPurchase
} from "../../../model/checkout";
import { addContact, checkoutAddItems } from "../actions";
import { checkoutUpdateSummaryPrices } from "../actions/checkoutSummary";
import {
  CHECKOUT_CONTACT_COLUMNS,
  CHECKOUT_COURSE_CLASS_COLUMNS,
  CheckoutCurrentStep,
  CheckoutPage
} from "../constants";
import CheckoutService from "../services/CheckoutService";
import {
  checkoutCourseClassMap,
  checkoutCourseMap,
  checkoutProductMap,
  checkoutVoucherMap,
  getProductColumnsByType,
  processCheckoutSale
} from "./index";
import uniqid from "../../../common/utils/uniqid";
import { getProductAqlType } from "../../entities/sales/utils";
import { decimalPlus } from "../../../common/utils/numbers/decimalCalculation";

export const processCheckoutLeadId = async (id: string, onChangeStep, setActiveField, setCustomLoading, dispatch) => {
  setCustomLoading(true);

  const enrolments: CheckoutEnrolmentCustom[] = [];
  const purchases: CheckoutProductPurchase[] = [];

  const lead = await EntityService.getPlainRecords(
    "Lead",
    "customer.id,items.course.id,items.product.id,items.product.type",
    `id is ${id}`
  );

  const customerId = JSON.parse(lead.rows[0].values[0]);
  const leadCourseIds = JSON.parse(lead.rows[0].values[1]);
  const productIds = JSON.parse(lead.rows[0].values[2]);
  const productTypes = JSON.parse(lead.rows[0].values[3]);

  const contact = await EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${customerId}`, 1)
    .then(res => res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS))[0]);

  dispatch(addContact(contact));

  // Assemble products
  const purchase: CheckoutProductPurchase = {
    contactId: contact.id,
    items: []
  };

  for (const [index, productId] of productIds.entries()) {
    const productType = Object.keys(ProductType)[productTypes[index] - 1];
    const columns = getProductColumnsByType(productType);
    const product = await EntityService.getPlainRecords(
      getProductAqlType(productType),
      columns,
      `id is ${productId}`,
      1
    ).then(res => (productType === 'Voucher'
      ? checkoutVoucherMap(res.rows.map(getCustomColumnsMap(columns))[0])
      : checkoutProductMap(res.rows.map(getCustomColumnsMap(columns))[0])));
    processCheckoutSale(product, productType.toLowerCase());
    purchase.items.push(product as any);
  }

  purchases.push(purchase);

  // Assemble enrolments
  const courses = await EntityService.getPlainRecords("Course", "code,name,isTraineeship", `id in (${leadCourseIds})`)
    .then(res => res.rows.map(row => checkoutCourseMap(getCustomColumnsMap("code,name,isTraineeship")(row))));

  for (const plainCourse of courses) {
    const enrolment: CheckoutEnrolmentCustom = {};
    enrolment.contactId = contact.isCompany ? null : contact.id;
    const classResponse = await EntityService.getPlainRecords(
      "CourseClass",
      CHECKOUT_COURSE_CLASS_COLUMNS,
      `course.id is ${plainCourse.courseId} and isCancelled is false and isActive is true and ( (startDateTime < tomorrow and endDateTime >= today and isCancelled is false) or (startDateTime >= tomorrow and endDateTime >= tomorrow and isCancelled is false) )`,
      null,
      0,
      "startDateTime",
      true
    );
    if (classResponse.rows.length) {
      const courseClass = [classResponse.rows[0]].map(checkoutCourseClassMap)[0];
      enrolment.courseClass = {
        ...plainCourse,
        courseId: plainCourse.id,
        price: courseClass.price,
        discount: null,
        discounts: [],
        discountExTax: 0,
        studyReason: "Not stated",
        class: { ...courseClass }
      };
    }
    enrolments.push(enrolment);
  }

  const courseIds = enrolments.map(en => en.courseClass?.courseId).filter(en => en).toString();

  await enrolments.map(en => () => {
    if (en.courseClass && typeof en.contactId === "number") {
      let total = en.courseClass.price;
      const contactPurchases = purchases.filter(p => p.contactId === en.contactId);
      const prodIds = [];
      const promoIds = [];
      const membershipIds = [];
      contactPurchases.forEach(cp => {
        cp.items.forEach(i => {
          total = decimalPlus(total, i.price);
          switch (i.type) {
            case "product":
              prodIds.push(i.id);
              break;
            case "voucher":
              promoIds.push(i.id);
              break;
            case "membership":
              membershipIds.push(i.id);
              break;
          }
        });
      });

      return CheckoutService.getContactDiscounts(
        en.contactId,
        en.courseClass.class.id,
        courseIds.toString(),
        prodIds.toString(),
        promoIds.toString(),
        membershipIds.toString(),
        1,
        total
      )
      .then(res => {
        if (res.length) {
          const discounts = res.map(r => r.discount);
          en.courseClass.discounts = discounts;
          en.courseClass.discount = discounts[0];
        }
      });
    }
    return Promise.resolve();
  }).reduce(async (a, b) => {
    await a;
    await b();
  }, Promise.resolve());

  dispatch(checkoutAddItems({ enrolments, purchases }));
  dispatch(checkoutUpdateSummaryPrices());
  setCustomLoading(false);
};

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

  const courseIds = enrolments.map(en => en.courseClass?.courseId).filter(en => en).toString();

  await enrolments.map(en => () => (en.courseClass
    ? CheckoutService.getContactDiscounts(
        en.contactId,
        en.courseClass.class.id,
        courseIds,
        "",
        "",
        "",
        enrolmentsCount,
        en.courseClass.price
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

  dispatch(checkoutAddItems({ enrolments }));
  onChangeStep(CheckoutCurrentStep.summary);
  setActiveField(CheckoutPage.summary);
  dispatch(checkoutUpdateSummaryPrices());
  setCustomLoading(false);
};

export const processCheckoutContactId = (contactId, onSelectHandler, dispatch) => {
  EntityService.getPlainRecords("Contact", CHECKOUT_CONTACT_COLUMNS, `id is ${contactId}`)
    .then(res => {
      const contact = res.rows.map(getCustomColumnsMap(CHECKOUT_CONTACT_COLUMNS));
      if (contact && contact.length > 0) onSelectHandler(contact[0], "contact");
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
