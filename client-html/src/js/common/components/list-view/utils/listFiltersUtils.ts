/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { TagGroup, Tag } from "@api/model";
import { MenuTag } from "../../../../model/tags";
import { FilterGroup } from "../../../../model/common/ListView";

export const getFiltersNameString = (filterGroups: FilterGroup[]) => filterGroups
  .map(group => group.filters.filter(f => f.active).map(f => "@" + f.name.trim().replace(/\s/g, "_")).toString())
  .filter(f => f.trim())
  .toString();

export const getFiltersString = (filterGroups: FilterGroup[]) => filterGroups
  .map(group => {
    const activeFilters = group.filters.filter(i => i.active).map(j => j.expression);

    let activeFiltersFormattedString = activeFilters.map(v => `(${v})`).join(" or ");

    if (activeFilters.length > 1) {
      activeFiltersFormattedString = `( ${activeFiltersFormattedString} )`;
    }

    return activeFiltersFormattedString;
  })
  .filter(v => v.trim())
  .join(" and ");

export const getActiveTags = (tags: MenuTag[], res?: MenuTag[]): MenuTag[] => {
  const result = res || [];

  tags.forEach(i => {
    if (i.active) {
      result.push(i);
    }

    if (i.children.length) {
      getActiveTags(i.children, result);
    }
  });

  return result;
};

export const getTagGroups = (tags: MenuTag[]) => {
  const groups: TagGroup[] = [];

  tags.forEach(t => {
    const active = getActiveTags(t.children);
    if (active.length) {
      groups.push({
        tagIds: active.map(a => a.tagBody.id),
        entity: t.entity,
        path: t.path
      });
    }
  });

  return groups;
};

export const getMenuTags = (allTags: Tag[], addedTags: Tag[], prefix?: string, queryPrefix?: string, entity?: string, path?: string, parent?: MenuTag): MenuTag[] => allTags.map(t => {
  const active = addedTags.find(i => i.id === t.id);

  const tag: MenuTag = {
    active: Boolean(active),
    tagBody: t,
    parent,
    children: []
  };

  tag.children = t.childTags.length ? getMenuTags(t.childTags, addedTags, prefix, queryPrefix, entity, path, tag) : [];

  if (prefix) {
    tag.prefix = prefix;
  }
  if (queryPrefix) {
    tag.queryPrefix = queryPrefix;
  }
  if (entity) {
    tag.entity = entity;
  }
  if (path) {
    tag.path = path;
  }

  return tag;
});

const selectionTemplate = str => `id == "${str}"`;

export const getExpression = (selection: string[]): string => selection.map(selectionTemplate).join(" or ");

export const setIndeterminate = (parentTag: MenuTag) => {
  if (parentTag.children.some(c => !c.active)) {
    parentTag.active = false;
    parentTag.indeterminate = parentTag.children.some(c => c.active || c.indeterminate);
  } else {
    parentTag.indeterminate = false;
    parentTag.active = true;
  }

  if (parentTag.parent) {
    setIndeterminate(parentTag.parent);
  }
};

export const updateIndeterminateState = (tags: MenuTag[], id: string) => {
  for (let i = 0; i < tags.length; i++) {
    if (tags[i].prefix + tags[i].tagBody.id.toString() === id) {
      if (tags[i].parent) {
        setIndeterminate(tags[i].parent);
      }
      break;
    }
    updateIndeterminateState(tags[i].children, id);
  }
};

export const getUpdated = (tags: MenuTag[], id: string, active, parent?: MenuTag, allActive?: boolean) => tags.map(t => {
    const updated = { ...t, parent };
    let toggleChildrenActive = false;

    if (allActive || updated.prefix + updated.tagBody.id.toString() === id) {
      updated.active = active;
      updated.indeterminate = false;
      toggleChildrenActive = true;
    }

    if (updated.children.length) {
      updated.children = getUpdated(updated.children, id, active, updated, toggleChildrenActive);
    }

    return updated;
  });

export const getTagsUpdatedByIds = (tags: MenuTag[], activeIds: number[]) => tags.map(t => {
  const updated = { ...t };

  updated.active = activeIds.includes(updated.tagBody.id);

  if (updated.children.length) {
    updated.children = getTagsUpdatedByIds(updated.children, activeIds);
  }

  return updated;
});
