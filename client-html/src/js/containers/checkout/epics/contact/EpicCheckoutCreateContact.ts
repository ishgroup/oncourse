/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact } from '@api/model';
import { initialize } from 'redux-form';
import { Epic } from 'redux-observable';
import { FETCH_SUCCESS } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import { CheckoutContact } from '../../../../model/checkout';
import { createEntityItem } from '../../../entities/common/entityItemsService';
import { CHECKOUT_ADD_CONTACT } from '../../actions';
import { CHECKOUT_CREATE_CONTACT } from '../../actions/checkoutContact';
import { checkoutUpdateSummaryClassesDiscounts } from '../../actions/checkoutSummary';
import { CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME } from '../../components/contact/CheckoutContactEditView';

const request: EpicUtils.Request<any, Contact> = {
  type: CHECKOUT_CREATE_CONTACT,
  getData: contact => createEntityItem("Contact", contact),
  processData: (res, s, p) => {
    const contact: CheckoutContact = {
      id: res[0],
      firstName: p.firstName,
      lastName: p.lastName,
      email: p.email,
      birthDate: p.birthDate,
      isCompany: p.isCompany,
      invoiceTerms: p.invoiceTerms ? p.invoiceTerms.toString() : null,
      relations: p.relations && p.relations.length ? p.relations.map(r => ({
        id: r.id,
        relationId: r.relationId.toString(),
        relatedContactName: r.contactToName,
        relatedContactId: r.relationId.toString()
      })) : [],
      message: p.message,
      type: "contact"
    };

    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Contact created" }
      },
      {
        type: CHECKOUT_ADD_CONTACT,
        payload: { contact }
      },
      checkoutUpdateSummaryClassesDiscounts(),
      initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: (response, contact) => [
    ...FetchErrorHandler(response, "Contact was not created"),
    initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, contact)
  ]
};

export const EpicCheckoutCreateContact: Epic<any, any> = EpicUtils.Create(request);
