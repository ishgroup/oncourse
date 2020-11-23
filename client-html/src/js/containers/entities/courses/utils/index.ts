/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course, SaleType } from "@api/model";
import { openInternalLink } from "../../../../common/utils/links";
import { Classes } from "../../../../model/entities/CourseClass";
import { entityForLink } from "../../common/utils";

export const courseFilterCondition = (value: Course) => `${value.name} ${value.code}`;

export const openCourseLink = (courseId: number) => {
  openInternalLink("/course/" + courseId);
};

export const formatRelatedSalablesAfterFetch = relatedItems => relatedItems.map(r => {
  if (r.entityFromId) r.relationId = String(r.relationId).includes("r") ? r.relationId : r.relationId + "r";
  return {
    ...r,
    relationId: r.relationId ? String(r.relationId) : ""
  };
});

export const formatRelatedSalables = relatedItems => relatedItems.map(r => {
  if (r.entityFromId) r.relationId = String(r.relationId).includes("r") ? r.relationId : r.relationId + "r";
  const entityId = r.entityToId ? r.entityToId : r.entityFromId ? r.entityFromId : r.id.toString();
  return {
    id: r.id.toString(),
    entityId,
    entityName: r.type,
    primaryText: r.name,
    secondaryText: r.code,
    link: r.type === SaleType.Class
      ? `/${Classes.path}?search=id is ${entityId}`
      : `/${entityForLink(r.type)}/${entityId}`,
    active: typeof r.active === "boolean" ? r.active : true,
    relationId: r.relationId ? String(r.relationId) : ""
  };
});

export const formatRelatedSalablesBeforeSave = relatedlSalables => {
  if (Array.isArray(relatedlSalables) && relatedlSalables.length) {
    return relatedlSalables
      .map(r => {
        const isReverseRelation = String(r.relationId).includes("r");
        const entityId = r.entityToId ? r.entityToId : r.entityFromId ? r.entityFromId : r.id;
        return {
          ...r,
          entityToId: isReverseRelation ? null : entityId,
          entityFromId: isReverseRelation ? entityId : null,
          relationId: r.relationId ? parseInt(r.relationId, 10) : null
        };
      });
  }
  return relatedlSalables;
};

export const formattedEntityRelationTypes = types => {
  if (!types) return [];
  const relations = [];
  types.forEach(type => {
    relations.push({
      ...type, label: type.toName, value: type.id, isReverseRelation: false
    });
    relations.push({
      ...type, label: type.fromName, value: type.id, isReverseRelation: true
    });
  });
  return relations;
};

export const uniqueEntityRelationTypes = (types, relationId) => {
  let uniqueTypes = [];
  const sameTypes = [];
  const relations: any = Array.from(types);
  relations.forEach(r => {
    if (!uniqueTypes.includes(r.label)) {
      uniqueTypes.push(r.label);
    } else {
      sameTypes.push(r.label);
    }
  });

  uniqueTypes = uniqueTypes.filter(l => !sameTypes.includes(l));

  const isReverseRelation = String(relationId).includes("r");

  return relations
    .map(r => {
      const isSameType = sameTypes.includes(r.label);

      if (isSameType) {
        if (r.isReverseRelation === isReverseRelation) {
          return undefined;
        }
      }
      return {
        ...r,
        value: String(r.isReverseRelation ? r.value : r.value + "r")
      };
    })
    .filter(r => !!r);
};
