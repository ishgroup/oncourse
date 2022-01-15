/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { changePhase, GET_CART_DATA, getCheckoutModelFromBackend, setPayer } from '../actions/Actions';
import * as EpicUtils from '../../common/epics/EpicUtils';
import CartService from '../../services/CartService';
import CheckoutServiceV2 from '../services/CheckoutServiceV2';
import { ContactNode } from '../../model';
import { addContactNodeToState } from '../containers/summary/actions/Actions';
import { requestCourseClass, requestProduct } from '../../web/actions/Actions';
import { Phase } from '../reducers/State';
import { addContact } from '../containers/contact-add/actions/Actions';

const request: any = {
  type: GET_CART_DATA,
  getData: (payload) => CartService.get(payload).then((response) => {
    const arrayOfPromises = [];
    for (const key in response) {
      if (response[key] && typeof response[key] === 'object' && response[key].contactId) {
        const currentCart = response[key];
        arrayOfPromises.push(CheckoutServiceV2.getContactNodeForCart({ ...currentCart }));
      }
    }
    return Promise.all(arrayOfPromises).then((contactNodes) => ({ contactNodes, payerId: response.payerId }));
  }),
  processData: ({ contactNodes, payerId }: { contactNodes: ContactNode[], payerId: string }) => {
    if (!contactNodes) return [];

    const setOfClassIds = new Set();

    const setOfProductIds = new Set();

    contactNodes.forEach((node) => {
      node.enrolments.forEach((enrolment) => enrolment.classId && setOfClassIds.add(enrolment.classId));
      node.vouchers.forEach((v) => v.productId && setOfProductIds.add(v.productId));
      node.memberships.forEach((v) => v.productId && setOfProductIds.add(v.productId));
      node.articles.forEach((v) => v.productId && setOfProductIds.add(v.productId));
    });

    const getCourses = Array.from(setOfClassIds).map((id: string) => requestCourseClass(id, true));

    const getProducts = Array.from(setOfProductIds).map((id: string) => requestProduct(id, true));

    const contacts = contactNodes.map((node) => addContact({
      email: node.contactEmail,
      firstName: node.contactFirstName,
      id: node.contactId,
      lastName: node.contactLastName,
    }));

    const nodes = contactNodes.map((node) => addContactNodeToState(node));

    return [
      ...contacts,
      ...nodes,
      ...getCourses,
      ...getProducts,
      setPayer(payerId),
      changePhase(Phase.Summary),
      getCheckoutModelFromBackend()
    ];
  },
};

export const EpicGetCartData: Epic<any, any> = EpicUtils.Create(request);
