/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SaleType } from "@api/model";
import { Classes } from "../../../../model/entities/CourseClass";

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

export const formatRelatedSalables = relatedItems => relatedItems.map(r => {
  const entityId = r.entityToId ? r.entityToId : r.entityFromId ? r.entityFromId : r.id;
  return {
    id: r.id,
    entityId,
    entityName: r.type,
    primaryText: r.name,
    secondaryText: r.code,
    link: r.type === SaleType.Class
      ? `/${Classes.path}?search=id is ${entityId}`
      : `/${entityForLink(r.type)}/${entityId}`,
    active: typeof r.active === "boolean" ? r.active : true,
    ...r
  };
});

export const formattedEntityRelationTypes = types => {
  if (!types) return [];
  const relations = [];
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
