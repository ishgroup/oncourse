/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import { TagGroup } from '@api/model';
import { FilterGroup } from '../../../../model/common/ListView';
import { FormMenuTag } from '../../../../model/tags';

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

export const setActiveFiltersBySearch = (search: string, filters: FilterGroup[]): FilterGroup[]  => {
  const filterNames = search ? search.replace(/[@_]/g, " ")
    .split(",")
    .map(f => f.trim()) : [];
  return filters.map(g => ({ ...g, filters: g.filters.map(f => ({ ...f, active: filterNames.includes(f.name) })) }));
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

/**
 * Rebuilds a tag tree from a flat list of active ids.
 *
 * Parent links and the indeterminate flag are derived here so that a selection restored from
 * the url renders exactly like the same selection made by clicking - `setIndeterminate` is not
 * usable for that because it walks upwards from an already linked tree.
 */
export const getTagsUpdatedByIds = (tags: FormMenuTag[], activeIds: number[]) => tags.map(t => {
  const updated = { ...t };

  updated.active = activeIds.includes(updated.tagBody.id);

  if (updated.children.length) {
    updated.children = getTagsUpdatedByIds(updated.children, activeIds);
  }

  return updated;
});

export const getTagsUpdatedByIdsWithIndeterminate = (
  tags: FormMenuTag[],
  activeIds: number[],
): FormMenuTag[] => {
  const activeIdsSet = new Set(activeIds);

  const updateTag = (tag: FormMenuTag): FormMenuTag => {
    const updated = {
      ...tag,
      children: tag.children.map(updateTag),
    };

    updated.active = activeIdsSet.has(updated.tagBody.id);

    const hasActiveChild = updated.children.some(
      child => child.active || child.indeterminate,
    );

    const allChildrenActive =
      updated.children.length > 0 &&
      updated.children.every(child => child.active);

    updated.indeterminate =
      !updated.active &&
      hasActiveChild &&
      !allChildrenActive;

    return updated;
  };

  return tags.map(updateTag);
};

export const getIndeterminateTagIds = (
  tags: FormMenuTag[],
  activeIds: string[],
): string[] => {
  const activeIdsSet = new Set(activeIds);
  const indeterminateIds: string[] = [];

  const visit = (
    tag: FormMenuTag,
  ): { hasActive: boolean; allActive: boolean } => {
    const isActive = activeIdsSet.has(tag.tagBody.id.toString());

    if (!tag.children.length) {
      return {
        hasActive: isActive,
        allActive: isActive,
      };
    }

    const children = tag.children.map(visit);

    const hasActiveChild = children.some(child => child.hasActive);
    const allChildrenActive = children.every(child => child.allActive);

    if (!isActive && hasActiveChild && !allChildrenActive) {
      indeterminateIds.push(tag.tagBody.id.toString());
    }

    return {
      hasActive: isActive || hasActiveChild,
      allActive: isActive || allChildrenActive,
    };
  };

  tags.forEach(visit);

  return indeterminateIds;
};