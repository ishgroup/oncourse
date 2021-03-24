/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course, EntityRelationType, Module, Qualification, Sale, SaleType } from "@api/model";
import { Classes } from "../../../../model/entities/CourseClass";
import { EntityRelationTypeRendered } from "../../../../model/entities/EntityRelations";

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
      ? `/${Classes.path}?search=id is ${entityId}`
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
