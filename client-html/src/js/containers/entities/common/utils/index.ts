/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
 Account, Course, EntityRelationType, Module, Qualification, Sale, SaleType 
} from "@api/model";
import { format } from "date-fns";
import { EntityRelationTypeRendered } from "../../../../model/entities/EntityRelations";
import { EntityName } from "../../../../model/entities/common";
import { EEE_D_MMM_YYYY } from "../../../../common/utils/dates/format";
import { State } from "../../../../reducers/state";

export const mapEntityDisplayName = (entity: EntityName) => {
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
