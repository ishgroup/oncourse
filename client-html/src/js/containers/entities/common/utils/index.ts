/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, Course, EntityRelationType, Module, Qualification, Sale, SaleType } from '@api/model';
import { format } from 'date-fns';
import { EEE_D_MMM_YYYY } from 'ish-ui';
import { initialize } from 'redux-form';
import {
  checkPermissionsRequestFulfilled,
  clearActionsQueue,
  executeActionsQueue,
  FETCH_SUCCESS
} from '../../../../common/actions';
import { getNoteItems } from '../../../../common/components/form/notes/actions';
import { getRecords, SET_LIST_EDIT_RECORD, setListSelection } from '../../../../common/components/list-view/actions';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import AccessService from '../../../../common/services/AccessService';
import { NOTE_ENTITIES } from '../../../../constants/Config';
import { AccessByPath, EntityName, ListActionEntity } from '../../../../model/entities/common';
import { EntityRelationTypeRendered } from '../../../../model/entities/EntityRelations';
import { State } from '../../../../reducers/state';
import { getEntityRecord } from '../actions';

export const mapEntityDisplayName = (entity: ListActionEntity) => {
  switch (entity) {
    case "VoucherProduct":
      return "Voucher";
    case "MembershipProduct":
      return "Membership";
    case "ArticleProduct":
      return "Product";
    case "Article":
    case "ProductItem":
    case "Voucher":
    case "Membership":
      return "Sale";
    case "AbstractInvoice":
    case "Invoice":
      return "Invoice";
    case "CourseClass":
      return "Class";
    case "PaymentIn":
      return "Payment In";
    case "PaymentOut":
      return "Payment Out";
    default:
      return entity.split(/(?=[A-Z])/).join(" ").toLowerCase().capitalize();
  }
};

export const mapEntityListDisplayName = (entity: EntityName, item: any, state: State) => {
  switch (entity) {
    case "Account":
      return `${item.accountCode} ${item.description}`;
    case "Application":
      return item.courseName;
    case "AssessmentSubmission":
      return item.id;
    case "Banking":
      return `${format(new Date(item.settlementDate), EEE_D_MMM_YYYY)}${item.adminSite ? " for " + item.adminSite : ""}`;
    case "CorporatePass":
      return item.contactFullName;
    case "Certificate":
      return item.studentName;
    case "Course":
      return `${item.name} ${item.code}`;
    case "Outcome":
    case "Enrolment":
      return item.studentName;
    case "Invoice":
    case "AbstractInvoice":
      return item.invoiceNumber;
    case "Lead":
      return item.contactName;
    case "Message":
      return `${item.sentToContactFullname} (${item.subject})`;
    case "Qualification":
    case "PriorLearning":
    case "Module":
      return item.title;
    case "PaymentIn":
      return item.payerName;
    case "PaymentOut":
      return item.payeeName;
    case "Payslip":
      return item.tutorFullName;
    case "ProductItem":
      return item.productName;
    case "Survey":
      return item.studentName + " - " + item.className;
    case "AccountTransaction": {
      let name: any = item.fromAccount;

      if (state.plainSearchRecords.Account.items) {
        const account = state.plainSearchRecords.Account.items.find((acc: Account) => acc.id === item.fromAccount);

        if (account) {
          name = `${account.description} ${account.accountCode}`;
        }
      }

      return name;
    }
    case "WaitingList":
      return item.courseName;

    default:
      return item.name;
  }
};

export const entityForLink = (type: SaleType) => {
  switch (type) {
    case SaleType.Class:
      return "classes";
    case SaleType.Product:
      return "product";
    case SaleType.Membership:
      return "membership";
    case SaleType.Voucher:
      return "voucher";
    case SaleType.Course:
      return "course";
    case SaleType.Module:
      return "module";
    case SaleType.Qualification:
      return "qualification";
    default: {
      console.error(`unknown sale type ${type}!`);
      return "";
    }
  }
};

export const formatFundingSourceId = val => (val === null ? -1 : val);

export const transformDataType = type => (type === "BOOLEAN"
  ? "Checkbox"
  : (type.substring(0, 1) + type.substring(1).toLowerCase()).replace("_", " "));

export const formatRelatedSalables = (relatedItems, type?: SaleType) => relatedItems.map(r => {
  const item: Qualification & Module & Sale & Course = { ...r };

  const entityName = type || item.type;

  if (item.title) {
    item.name = item.title;
  }

  if (item.nationalCode) {
    item.code = item.nationalCode;
  }

  const entityId = item.entityToId ? item.entityToId : item.entityFromId ? item.entityFromId : item.id;

  return {
    id: item.id,
    entityId,
    entityName,
    primaryText: item.name,
    secondaryText: item.code,
    link: entityName === SaleType.Class
      ? `/class?search=id is ${entityId}`
      : `/${entityForLink(entityName)}/${entityId}`,
    active: typeof r.active === "boolean"
      ? r.active
      : typeof r.isOffered === "boolean"
        ? r.isOffered
        : typeof r.currentlyOffered === "boolean"
          ? r.currentlyOffered
          : typeof r.isShownOnWeb === "boolean"
            ? r.isShownOnWeb
            : true,
    ...r
  };
});

export const formattedEntityRelationTypes = (types: EntityRelationType[]): EntityRelationTypeRendered[] => {
  const relations: EntityRelationTypeRendered[] = [];
  types.forEach(type => {
    let combined = false;
    if (type.toName !== type.fromName) {
      relations.push({
        ...type, label: `${type.toName} (${type.name})`, isReverseRelation: true, combined, value: type.id + "r"
      });
    } else {
      combined = true;
    }
    relations.push({
      ...type, label: `${type.fromName} (${type.name})`, isReverseRelation: false, combined, value: String(type.id)
    });
  });
  return relations;
};

export const salesSort = (a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1);

export const mapRelatedSalables = (s): Sale & { tempId: any } => ({
  id: s.entityId,
  tempId: s.entityId,
  name: s.primaryText,
  code: s.secondaryText,
  active: s.active,
  type: s.entityName,
  expiryDate: null,
  entityFromId: s.entityId,
  entityToId: null,
  relationId: -1
});

export const getListRecordAfterUpdateActions = (entity: EntityName, state: State, id: number) => [
  executeActionsQueue(),
  {
    type: FETCH_SUCCESS,
    payload: { message: `${mapEntityDisplayName(entity)} updated` }
  },
  getRecords({ entity, listUpdate: true, savedID: id }),
  ...state.list.fullScreenEditView || state.list.records.layout === "Three column" ? [
    getEntityRecord(id, entity)
  ] : []
];

export const getListRecordAfterGetActions = (item: any, entity: EntityName, state: State) => [
  {
    type: SET_LIST_EDIT_RECORD,
    payload: { editRecord: item, name: mapEntityListDisplayName(entity, item, state) }
  },
  ...NOTE_ENTITIES.includes(entity) ? [getNoteItems(entity, item.id, LIST_EDIT_VIEW_FORM_NAME)] : [],
  initialize(LIST_EDIT_VIEW_FORM_NAME, item),
  ...(state.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
];

export const getListRecordAfterBulkDeleteActions = (entity: ListActionEntity) => [
  {
    type: FETCH_SUCCESS,
    payload: { message: `${mapEntityDisplayName(entity)} records deleted` }
  },
  getRecords({ entity, listUpdate: true }),
  setListSelection([]),
  initialize(LIST_EDIT_VIEW_FORM_NAME, null)
];

export const getListRecordAfterDeleteActions = (entity: EntityName) => [
  {
    type: FETCH_SUCCESS,
    payload: { message: `${mapEntityDisplayName(entity)} deleted` }
  },
  getRecords({ entity, listUpdate: true }),
  setListSelection([]),
  initialize(LIST_EDIT_VIEW_FORM_NAME, null)
];

export const getListRecordAfterCreateActions = (entity: EntityName) => [
  executeActionsQueue(),
  {
    type: FETCH_SUCCESS,
    payload: { message: `${mapEntityDisplayName(entity)} created` }
  },
  getRecords({ entity, listUpdate: true }),
  setListSelection([]),
  initialize(LIST_EDIT_VIEW_FORM_NAME, null)
];

export const getAccessesByPath = async (pathes: string[], state: State, method = 'GET'): Promise<AccessByPath[]> => {
  const accesses = [];

  for (const path of pathes) {
    const accessValue = state.access[path];

    if (accessValue) {
      accesses.push( { hasAccess: accessValue[method] });
    } else {
      const request = { path, method };
      const { hasAccess } = await AccessService.checkPermissions(request);
      accesses.push({
          hasAccess,
          action: checkPermissionsRequestFulfilled({
            ...request,
            hasAccess
          }),
        }
      );
    }
  }

  return accesses;
};