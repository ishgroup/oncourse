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
 * Identifies the group a tag tree is published under.
 *
 * The same tags are published several times over on some lists - the course tags appear on the
 * contact list as both `Enrolled` and `Teaching`, the sale checklists once per product entity -
 * and each publication filters by a path of its own. A selection therefore only means something
 * together with the group it was made in, so a restored selection has to be matched per group
 * rather than by tag id alone.
 */
export const getTagGroupKey = (tag: FormMenuTag): string => tag.prefix || tag.entity || "";

/**
 * Identifies one rendered tag group. The sale checklists are published once per product entity
 * off the same checklist tag, so the root id alone does not tell two of them apart.
 */
export const getTagGroupId = (group: FormMenuTag): string =>
  `${getTagGroupKey(group)}/${group.tagBody.id}`;

/**
 * A tag selection read out of the url: the ids selected in each group, plus the ids of an older
 * url that named no group at all.
 */
export interface TagSelection {
  byGroup: Map<string, number[]>;
  ungrouped: number[];
}

/**
 * Ids to activate in one group.
 *
 * An url that names no group anywhere was written before the group was recorded: its ids cannot
 * be placed, so they are offered to every group and only tick where the tag actually exists -
 * the reading those urls were written under. As soon as a single group is named the url is
 * authoritative, and bare ids belong to the unnamed group alone.
 */
const getGroupActiveIds = (group: FormMenuTag, selection: TagSelection): Set<number> => {
  const ids = new Set(selection.byGroup.get(getTagGroupKey(group)) || []);

  if (!getTagGroupKey(group) || !selection.byGroup.size) {
    selection.ungrouped.forEach(id => ids.add(id));
  }

  return ids;
};

/**
 * Rebuilds a tag tree from a selection read out of the url.
 *
 * Parent links and the indeterminate flag are derived here so that a selection restored from
 * the url renders exactly like the same selection made by clicking - `setIndeterminate` is not
 * usable for that because it walks upwards from an already linked tree.
 */
export const getTagsUpdatedByIds = (tags: FormMenuTag[], selection: TagSelection) => {
  const updateTag = (tag: FormMenuTag, activeIds: Set<number>): FormMenuTag => {
    const updated = { ...tag };

    updated.active = activeIds.has(updated.tagBody.id);

    if (updated.children.length) {
      updated.children = updated.children.map(child => updateTag(child, activeIds));
    }

    return updated;
  };

  return tags.map(group => updateTag(group, getGroupActiveIds(group, selection)));
};

export const getTagsUpdatedByIdsWithIndeterminate = (
  tags: FormMenuTag[],
  selection: TagSelection,
): FormMenuTag[] => {
  const updateTag = (tag: FormMenuTag, activeIds: Set<number>): FormMenuTag => {
    const updated = {
      ...tag,
      children: tag.children.map(child => updateTag(child, activeIds)),
    };

    // strictly what the url named: deriving a parent from its children here would write that
    // parent back into the url on the next render, growing the stored selection on every load
    updated.active = activeIds.has(updated.tagBody.id);

    updated.indeterminate =
      !updated.active &&
      updated.children.some(child => child.active || child.indeterminate);

    return updated;
  };

  return tags.map(group => updateTag(group, getGroupActiveIds(group, selection)));
};

/**
 * Applies the tree view's selection to the whole tag tree.
 *
 * The tree view can only carry a selection through the items it currently has mounted, so ticking
 * a collapsed tag leaves its children behind and it catches up only once the branch is opened -
 * which it then reports as a fresh change, growing the stored selection on every page load.
 * Resolving the selection against the full tree here makes the stored state complete at the moment
 * of the click instead, and leaves nothing for the mount to report.
 */
export const getTagsUpdatedBySelection = (
  tags: FormMenuTag[],
  selectedIds: number[],
): FormMenuTag[] => {
  const selected = new Set(selectedIds);

  const visit = (tag: FormMenuTag, inherited: boolean): FormMenuTag => {
    const active = inherited || selected.has(tag.tagBody.id);

    const updated = {
      ...tag,
      children: tag.children.map(child => visit(child, active)),
    };

    updated.active = active;
    updated.indeterminate = !active
      && updated.children.some(child => child.active || child.indeterminate);

    return updated;
  };

  return tags.map(tag => visit(tag, false));
};

/**
 * Ids of every tag holding a selected tag somewhere below it.
 *
 * The tree view derives a parent checkbox from the descendants it currently has mounted, so every
 * ancestor of a selection has to be expanded - a collapsed parent is read as a childless leaf and
 * renders unselected until it is opened, then jumps straight to selected.
 */
export const getTagIdsWithActiveDescendants = (
  tags: FormMenuTag[],
  activeIds: string[],
): string[] => {
  const activeIdsSet = new Set(activeIds);
  const result: string[] = [];

  // maps before it reduces so that every branch is visited and collected, never short circuited
  const visit = (tag: FormMenuTag): boolean => {
    const hasActiveDescendant = tag.children.map(visit).some(Boolean);

    if (hasActiveDescendant) {
      result.push(tag.tagBody.id.toString());
    }

    return hasActiveDescendant || activeIdsSet.has(tag.tagBody.id.toString());
  };

  tags.forEach(visit);

  return result;
};
