/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact } from '@api/model';
import { initialize } from 'redux-form';
import { Epic } from 'redux-observable';
import { clearActionsQueue, getUserPreferences } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { getNoteItems } from '../../../../common/components/form/notes/actions';
import { SET_LIST_EDIT_RECORD } from '../../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import {
  plainCertificatePath,
  plainEnrolmentPath,
  plainOutcomePath,
  plainPaymentInPath,
  plainPriorLearningPath
} from '../../../../constants/Api';
import { AVETMIS_ID_KEY, REPLICATION_ENABLED_KEY } from '../../../../constants/Config';
import { AccessByPath } from '../../../../model/entities/common';
import { VetReport } from '../../../../model/entities/VetReporting';
import { getEntityItemById } from '../../common/entityItemsService';
import { getAccessesByPath } from '../../common/utils';
import {
  GET_CONTACT,
  getContactCertificates,
  getContactEnrolments,
  getContactOutcomes,
  getContactPriorLearnings,
  getContactsStoredCc
} from '../actions';

export const formatContactRelationIds = relations => relations.map(r => {
    if (r.contactFromId) {
      r.relationId += "r";
    }

    return {
      id: r.id,
      relationId: String(r.relationId),
      relatedContactName: r.contactFromName || r.contactToName,
      relatedContactId: r.contactFromId || r.contactToId
    };
  });

const request: EpicUtils.Request<{
  contact: Contact
  enrolmentsPermissions: AccessByPath,
  priorLearningsPermissions: AccessByPath,
  outcomesPermissions: AccessByPath,
  certificatesPermissions: AccessByPath,
  paymentInPermissions: AccessByPath
}> = {
  type: GET_CONTACT,
  getData: async (id: number, state) => {
    const contact = await getEntityItemById("Contact", id);
    const [
      enrolmentsPermissions,
      priorLearningsPermissions,
      outcomesPermissions,
      certificatesPermissions,
      paymentInPermissions,
    ] = await getAccessesByPath(
      [
        plainEnrolmentPath,
        plainPriorLearningPath,
        plainOutcomePath,
        plainCertificatePath,
        plainPaymentInPath
      ],
      state
    );

    return {
      contact,
      enrolmentsPermissions,
      priorLearningsPermissions,
      outcomesPermissions,
      certificatesPermissions,
      paymentInPermissions
    };
  },
  processData: ({
    contact,
    enrolmentsPermissions,
    priorLearningsPermissions,
    outcomesPermissions,
    certificatesPermissions,
    paymentInPermissions
  }, s, id) => {

    const studentActions = [];

    if (contact.student) {
      if (enrolmentsPermissions.hasAccess) {
        studentActions.push(getContactEnrolments(contact.id));
      }
      if (priorLearningsPermissions.hasAccess) {
        studentActions.push(getContactPriorLearnings(contact.id));
      }
      if (outcomesPermissions.hasAccess) {
        studentActions.push(getContactOutcomes(contact.id));
      }
      if (certificatesPermissions.hasAccess) {
        studentActions.push(getContactCertificates(contact.id));
      }
    }

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
    }

    if (contact.financialData.length > 0) {
      contact.financialData = contact.financialData.sort((a, b) => (new Date(b.createdOn) > new Date(a.createdOn) ? 1 : -1));
    }

    let editRecord = contact;
    
    if (s.list.customTableModel === "VetReport") {
      editRecord = {
        id: contact.id,
        student: contact,
        enrolment: {},
        outcome: {}
      } as VetReport;
    }

    return [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord, name: contact.lastName }
      },
      getUserPreferences([REPLICATION_ENABLED_KEY, AVETMIS_ID_KEY]),
      getNoteItems("Contact", id, LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, editRecord),
      ...[
        enrolmentsPermissions,
        priorLearningsPermissions,
        outcomesPermissions,
        certificatesPermissions,
        paymentInPermissions
      ].filter(p => p.action).map(p => p.action),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : []),
      ...paymentInPermissions ? [getContactsStoredCc(id)] : [],
      ...studentActions
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetContact: Epic<any, any> = EpicUtils.Create(request);