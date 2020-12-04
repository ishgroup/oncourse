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
