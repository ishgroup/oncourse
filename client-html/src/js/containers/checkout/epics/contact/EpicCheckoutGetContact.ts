/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Contact } from "@api/model";
import { clearActionsQueue, getUserPreferences } from "../../../../common/actions";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { AVETMIS_ID_KEY, REPLICATION_ENABLED_KEY } from "../../../../constants/Config";
import { formatContactRelationIds } from "../../../entities/contacts/epics/EpicGetContact";
import { getEntityItemById } from "../../../entities/common/entityItemsService";
import {
  getContactCertificates,
  getContactEnrolments,
  getContactOutcomes,
  getContactPriorLearnings,
  getContactsStoredCc
} from "../../../entities/contacts/actions";
import {
  CHECKOUT_GET_CONTACT,
  CHECKOUT_GET_CONTACT_FULFILLED,
  CHECKOUT_UPDATE_CONTACT_RELATIONS
} from "../../actions/checkoutContact";
import { CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME } from "../../components/contact/CheckoutContactEditView";

const request: EpicUtils.Request = {
  type: CHECKOUT_GET_CONTACT,
  getData: (id: number) => getEntityItemById("Contact", id),
  processData: (contact: Contact, s, id) => {
    const studentActions = contact.student
      ? [
          getContactEnrolments(contact.id),
          getContactPriorLearnings(contact.id),
          getContactOutcomes(contact.id),
          getContactCertificates(contact.id)
        ]
      : [];

    const updateContactRelationsAction = [];

    if (contact.student && contact.student.usiStatus) {
      const status = contact.student.usiStatus;

      if (status === "Exemption") {
        contact.student.usi = "INDIV";
      }

      if (status === "International") {
        contact.student.usi = "INTOFF";
      }
    }

    if (contact.relations && contact.relations.length) {
      contact.relations = formatContactRelationIds(contact.relations);
      updateContactRelationsAction.push({
        type: CHECKOUT_UPDATE_CONTACT_RELATIONS,
        payload: { id: contact.id, relations: contact.relations }
      });
    }

    if (contact.financialData.length > 0) {
      contact.financialData = contact.financialData.sort((a, b) =>
        (new Date(b.createdOn) > new Date(a.createdOn) ? 1 : -1));
    }

    return [
      {
        type: CHECKOUT_GET_CONTACT_FULFILLED,
        payload: { editRecord: contact }
      },
      ...updateContactRelationsAction,
      getUserPreferences([REPLICATION_ENABLED_KEY, AVETMIS_ID_KEY]),
      getNoteItems("Contact", id, CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME),
      getContactsStoredCc(id),
        initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, contact),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : []),
      ...studentActions
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(CHECKOUT_CONTACT_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicCheckoutGetContact: Epic<any, any> = EpicUtils.Create(request);
