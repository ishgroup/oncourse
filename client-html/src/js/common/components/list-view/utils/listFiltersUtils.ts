/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import { TagGroup, Tag } from "@api/model";
import { FormMenuTag } from "../../../../model/tags";
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

export const setActiveFiltersBySearch = (search: string, filters: FilterGroup[]) => {
  const filterNames = search ? search.replace(/[@_]/g, " ")
    .split(",")
    .map(f => f.trim()) : [];
  filters.forEach(g => {
    g.filters.forEach(f => {
      // eslint-disable-next-line no-param-reassign
      f.active = filterNames.includes(f.name);
    });
  });
};

export const getActiveTags = (tags: FormMenuTag[], res?: FormMenuTag[]): FormMenuTag[] => {
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

export const getTagGroups = (tags: FormMenuTag[]) => {
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

export const getMenuTags = (allTags: Tag[], addedTags: Tag[], prefix?: string, queryPrefix?: string, entity?: string, path?: string, parent?: FormMenuTag): FormMenuTag[] => allTags.map(t => {
  const active = addedTags.find(i => i.id === t.id);

  const tag: FormMenuTag = {
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

export const setIndeterminate = (parentTag: FormMenuTag) => {
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

export const updateIndeterminateState = (tags: FormMenuTag[], id: string) => {
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

export const getUpdated = (tags: FormMenuTag[], id: string, active, parent?: FormMenuTag, allActive?: boolean) => tags.map(t => {
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

export const getTagsUpdatedByIds = (tags: FormMenuTag[], activeIds: number[]) => tags.map(t => {
  const updated = { ...t };

  updated.active = activeIds.includes(updated.tagBody.id);

  if (updated.children.length) {
    updated.children = getTagsUpdatedByIds(updated.children, activeIds);
  }

  return updated;
});
