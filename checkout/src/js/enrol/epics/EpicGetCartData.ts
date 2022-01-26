/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import {
  changePhase,
  GET_CART_DATA,
  getCheckoutModelFromBackend,
  resetCheckoutState,
  setPayer
} from '../actions/Actions';
import * as EpicUtils from '../../common/epics/EpicUtils';
import CartService from '../../services/CartService';
import CheckoutServiceV2 from '../services/CheckoutServiceV2';
import { Application, Article, ContactNode, Enrolment, Membership, Voucher, WaitingList } from '../../model';
import { addContactNodeToState } from '../containers/summary/actions/Actions';
import { requestCourseClass, requestProduct, requestWaitingCourse } from '../../web/actions/Actions';
import { Phase } from '../reducers/State';
import { addContact } from '../containers/contact-add/actions/Actions';
import { StoreCartContact, StoreCartItem, StoreCartRequest } from '../../model/checkout/request/StoreCart';
import { BuildContactNodeRequest } from '../services/CheckoutService';

const commonMap = (item: Enrolment | Application | WaitingList | Article | Membership | Voucher, storedItem: StoreCartItem) => ({
  ...item,
  selected: typeof storedItem?.selected === 'boolean' ? storedItem.selected : item.selected,
  fieldHeadings: item.fieldHeadings.map((fh, fhIndex) => ({
    ...fh,
    fields: fh.fields.map((field) => ({
      ...field,
      defaultValue: storedItem?.customFields && storedItem?.customFields[fhIndex]?.fields.find((fhf) => fhf.id === field.id)?.value || field.defaultValue
    }))
  }))
});

const mapEnrolment = (enrolment: Enrolment, c: StoreCartContact) => {
  const stored = c.classes.find((cl) => cl.id === enrolment.classId);
  return commonMap(enrolment, stored);
};

const mapApplication = (application: Application, c: StoreCartContact) => {
  const stored = c.applications.find((ap) => ap.id === application.classId);
  return commonMap(application, stored);
};

const mapWaitingList = (waitingList: WaitingList, c: StoreCartContact) => {
  const stored = c.waitingCourses.find((wl) => wl.id === waitingList.courseId);
  return commonMap(waitingList, stored);
};

const mapProduct = (product: Article | Membership | Voucher, c: StoreCartContact) => {
  const stored = c.products.find((pr) => pr.id === product.productId);
  return commonMap(product, stored);
};

const request: any = {
  type: GET_CART_DATA,
  getData: async (payload) => {
    const cartData: StoreCartRequest = await CartService.get(payload);

    const contactNodes = [];

    for (const c of cartData.contacts) {
      const node = await CheckoutServiceV2.getContactNodeForCart(
        BuildContactNodeRequest.fromStoredCartContact(c, c.contactId === cartData.payerId ? cartData.promotionIds : [])
      ).then((res) => ({
        ...res,
        applications: res.applications.map((ap) => mapApplication(ap, c)),
        enrolments: res.enrolments.map((en) => mapEnrolment(en, c)),
        waitingLists: res.waitingLists.map((wl) => mapWaitingList(wl, c)),
        articles: res.articles.map((p) => mapProduct(p, c)),
        vouchers: res.vouchers.map((p) => mapProduct(p, c)),
        memberships: res.memberships.map((p) => mapProduct(p, c)),
      }))
        .catch((e) => console.error(e));
      contactNodes.push(node);
    }

    return { contactNodes, payerId: cartData.payerId };
  },
  processData: ({ contactNodes, payerId }: { contactNodes: ContactNode[], payerId: string }) => {
    if (!contactNodes) return [];

    const setOfClassIds = new Set();

    const setOfWaitingCourseIds = new Set();

    const setOfProductIds = new Set();

    contactNodes.forEach((node) => {
      node.enrolments.forEach((enrolment) => enrolment.classId && setOfClassIds.add(enrolment.classId));
      node.applications.forEach((application) => application.classId && setOfClassIds.add(application.classId));
      node.vouchers.forEach((v) => v.productId && setOfProductIds.add(v.productId));
      node.memberships.forEach((v) => v.productId && setOfProductIds.add(v.productId));
      node.articles.forEach((v) => v.productId && setOfProductIds.add(v.productId));
      node.waitingLists.forEach((v) => v.courseId && setOfWaitingCourseIds.add(v.courseId));
    });

    const getCourses = Array.from(setOfClassIds).map((id: string) => requestCourseClass(id, true));

    const getWaitingCourses = Array.from(setOfWaitingCourseIds).map((id: string) => requestWaitingCourse(id, true));

    const getProducts = Array.from(setOfProductIds).map((id: string) => requestProduct(id, true));

    const contacts = contactNodes.map((node) => addContact({
      email: node.contactEmail,
      firstName: node.contactFirstName,
      id: node.contactId,
      lastName: node.contactLastName,
    }));

    const nodes = contactNodes.map((node) => addContactNodeToState(node));

    return [
      resetCheckoutState(),
      ...contacts,
      ...nodes,
      ...getCourses,
      ...getProducts,
      ...getWaitingCourses,
      setPayer(payerId),
      changePhase(Phase.Summary),
      getCheckoutModelFromBackend()
    ];
  },
};

export const EpicGetCartData: Epic<any, any> = EpicUtils.Create(request);
