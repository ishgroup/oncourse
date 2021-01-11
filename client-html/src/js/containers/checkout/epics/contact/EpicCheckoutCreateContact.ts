/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Contact } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { CheckoutContact } from "../../../../model/checkout";
import ContactsService from "../../../entities/contacts/services/ContactsService";
import { CHECKOUT_CREATE_CONTACT } from "../../actions/checkoutContact";
import { CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS } from "../../actions/checkoutSummary";
import { CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME } from "../../components/contact/CheckoutContactEditView";
import { CHECKOUT_ADD_CONTACT } from "../../actions";

const request: EpicUtils.Request<any, any, Contact> = {
  type: CHECKOUT_CREATE_CONTACT,
  getData: contact => ContactsService.createContact(contact),
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
      {
        type: CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS
      },
      initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: (response, contact) => [
    ...FetchErrorHandler(response, "Contact was not created"),
    initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, contact)
  ]
};

export const EpicCheckoutCreateContact: Epic<any, any> = EpicUtils.Create(request);
