/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ADD_CONTACT_FROM_CHECKOUT_MODEL_TO_STATE, changePhase, GET_CART_DATA, updateAmount } from '../actions/Actions';
import * as EpicUtils from '../../common/epics/EpicUtils';
import { Epic } from 'redux-observable';
import CartService from '../../services/CartService';
import CheckoutServiceV2 from '../services/CheckoutServiceV2';
import { CheckoutApi } from '../../http/CheckoutApi';
import { DefaultHttpService } from '../../common/services/HttpService';
import { ContactNode } from '../../model';
import { rewriteContactNodesToState } from '../containers/summary/actions/Actions';
import { Phase } from '../reducers/State';
import { Actions } from '../../web/actions/Actions';

const request: any = {
  type: GET_CART_DATA,
  getData: payload => {
    return CartService.get(payload).then(responce => {
      const arrayOfPromises = [];
      for (const key in responce) {
        if (responce[key] && typeof responce[key] === "object" && responce[key].contactId) {
          const currentCart = responce[key];
          arrayOfPromises.push(CheckoutServiceV2.getContactNodeForCart({...currentCart}));
        }
      }

      return Promise.all(arrayOfPromises).then((values: ContactNode[]) => {
        const checkoutApi = new CheckoutApi(new DefaultHttpService());

        const checkoutModelRequest = {
          contactNodes: values,
          payerId: responce.payerId,
        };

        return checkoutApi.getCheckoutModel(checkoutModelRequest);
      });
    });
  },
  processData: (responce: any) => {
    if (!responce) return [];

    const setOfClassIds = new Set();

    responce.contactNodes.forEach(node => node.enrolments.forEach(enrolment => enrolment.classId && setOfClassIds.add(enrolment.classId)));
    const getClassesIds = Array.from(setOfClassIds).map(id => ({
      type: Actions.REQUEST_COURSE_CLASS,
      payload: id,
    }));

    const users = responce.contactNodes.map(node => ({
      type: ADD_CONTACT_FROM_CHECKOUT_MODEL_TO_STATE,
      payload: {
        email: node.contactEmail,
        firstName: node.contactFirstName,
        id: node.contactId,
        lastName: node.contactLastName,
      },
    }));

    return [
      ...users,
      ...getClassesIds,
      rewriteContactNodesToState(responce.contactNodes),
      updateAmount(responce.amount),
      changePhase(Phase.Summary),
    ];
  },
};

export const EpicGetCartData: Epic<any, any> = EpicUtils.Create(request);
