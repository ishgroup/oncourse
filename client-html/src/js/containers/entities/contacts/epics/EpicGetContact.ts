/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PermissionRequest } from '@api/model';
import { initialize } from 'redux-form';
import { Epic } from 'redux-observable';
import { clearActionsQueue, getUserPreferences } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { getNoteItems } from '../../../../common/components/form/notes/actions';
import { SET_LIST_EDIT_RECORD } from '../../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import AccessService from '../../../../common/services/AccessService';
import {
  plainCertificatePath,
  plainEnrolmentPath,
  plainOutcomePath,
  plainPaymentInPath,
  plainPriorLearningPath
} from '../../../../constants/Api';
import { AVETMIS_ID_KEY, REPLICATION_ENABLED_KEY } from '../../../../constants/Config';
import { VetReport } from '../../../../model/entities/VetReporting';
import { getEntityItemById } from '../../common/entityItemsService';
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

const enrolmentAccessRequest: PermissionRequest = { path: plainEnrolmentPath, method: "GET" };
const priorLearningtAccessRequest: PermissionRequest = { path: plainPriorLearningPath, method: "GET" };
const outcomesAccessRequest: PermissionRequest = { path: plainOutcomePath, method: "GET" };
const certificatesAccessRequest: PermissionRequest = { path: plainCertificatePath, method: "GET" };
const paymentInAccessRequest: PermissionRequest = { path: plainPaymentInPath, method: "GET" };

const request: EpicUtils.Request = {
  type: GET_CONTACT,
  getData: async (id: number, state) => {
    const contact = await getEntityItemById("Contact", id);
    
    const enrolmentsPermissions = state.access[plainEnrolmentPath]
      ? state.access[plainEnrolmentPath]['GET']
      : await AccessService.checkPermissions(enrolmentAccessRequest);

    const priorLearningsPermissions = state.access[plainPriorLearningPath]
      ? state.access[plainPriorLearningPath]['GET']
      : await AccessService.checkPermissions(priorLearningtAccessRequest);

    const outcomesPermissions = state.access[plainOutcomePath]
      ? state.access[plainOutcomePath]['GET']
      : await AccessService.checkPermissions(outcomesAccessRequest);

    const certificatesPermissions = state.access[plainCertificatePath]
      ? state.access[plainCertificatePath]['GET']
      : await AccessService.checkPermissions(certificatesAccessRequest);

    const paymentInPermissions = state.access[plainPaymentInPath]
      ? state.access[plainPaymentInPath]['GET']
      : await AccessService.checkPermissions(paymentInAccessRequest);
    
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
  }: any, s, id) => {

    const studentActions = [];

    if (contact.student) {
      if (enrolmentsPermissions) {
        studentActions.push(getContactEnrolments(contact.id));
      }
      if (priorLearningsPermissions) {
        studentActions.push(getContactPriorLearnings(contact.id));
      }
      if (outcomesPermissions) {
        studentActions.push(getContactOutcomes(contact.id));
      }
      if (certificatesPermissions) {
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
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : []),
      ...paymentInPermissions ? [getContactsStoredCc(id)] : [],
      ...studentActions
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetContact: Epic<any, any> = EpicUtils.Create(request);