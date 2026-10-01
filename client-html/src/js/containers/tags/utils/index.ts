/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DataRow, Tag } from "@api/model";
import { CatalogItemType } from "../../../model/common/Catalog";
import { FormTag, FormMenuTag } from "../../../model/tags";

export const getAllTags = (tags: Tag[], res?: Tag[]): Tag[] => {
  const result = res || [];

  for (let i = 0; i < tags.length; i++) {
    result.push(tags[i]);

    if (tags[i].childTags.length) {
      getAllTags(tags[i].childTags, result);
    }
  }

  return result;
};

export const getAllFormTags = (tags: Tag[], res?: FormTag[], rootTag?: FormTag): FormTag[] => {
  const result = res || [];
  for (let i = 0; i < tags.length; i++) {
    result.push({
      ...tags[i],
      rootTag
    });

    if (tags[i].childTags.length) {
      getAllFormTags(tags[i].childTags, result, rootTag || tags[i]);
    }
  }

  return result;
};

export interface TagTreeNode {
  id?: number;
  childrenCount?: number;
  childTags?: TagTreeNode[];
}

// the tag input builds its menu from the given tree and only renders the ids it finds there, every
// other id is shown as an "Error: Tag not found!" chip. Its first level is filtered by
// `allowParentSelect || childrenCount > 0`, so a group on the first level is only part of the menu
// when it holds tags - a group without any can never be picked and is left out here as well
export const getAvailableTagIds = (tags: TagTreeNode[] | undefined): Set<number> | undefined => {
  if (!Array.isArray(tags) || !tags.length) return undefined;

  const ids = new Set<number>();

  const collect = (nodes: TagTreeNode[], isTagGroup = false) => {
    nodes.forEach(node => {
      if (node?.id !== null && node?.id !== undefined && !(isTagGroup && !node?.childrenCount)) {
        ids.add(node.id);
      }
      if (node?.childTags?.length) collect(node.childTags);
    });
  };

  collect(tags, true);
  return ids;
};

export const removeUnavailableTagIds = (value: number[] | undefined, availableTagIds: Set<number>): number[] | undefined =>
  Array.isArray(value) ? value.filter(id => availableTagIds.has(id)) : value;

/**
 * Applies a change of the tag input to the full value of the form field.
 *
 * The tag input only knows the ids of the tag menu it was given, so the value it reports back is the
 * displayed one. Ids which are in the form value but not in the menu are kept: completed checklist
 * tasks are kept in the same field, and dropping them here would silently uncheck them on the next
 * save of the record.
 */
export const applyTagsChange = (formValue: number[] | undefined, displayed: number[] | undefined, next: number[] | undefined): number[] | undefined => {
  if (!Array.isArray(formValue) || !Array.isArray(displayed) || !Array.isArray(next)) return next;

  const removed = new Set(displayed.filter(id => !next.includes(id)));

  return [
    ...formValue.filter(id => !removed.has(id)),
    ...next.filter(id => !formValue.includes(id))
  ];
};

/**
 * Ids of a tag/checklist menu without the entries on the first level - those are the groups which
 * hold the tags/checklist tasks, they are never assigned to a record.
 */
export const getMenuTagIds = (menuTags: FormMenuTag[] | undefined): Set<number> | undefined => {
  if (!Array.isArray(menuTags) || !menuTags.length) return undefined;

  const ids = new Set<number>();

  const collect = (nodes: FormMenuTag[]) => {
    nodes.forEach(node => {
      if (node?.tagBody?.id !== null && node?.tagBody?.id !== undefined) ids.add(node.tagBody.id);
      if (node?.children?.length) collect(node.children);
    });
  };

  menuTags.forEach(group => {
    if (group?.children?.length) collect(group.children);
  });

  return ids;
};

/**
 * Tag ids which can still be assigned to an entity - the tags of the entity's tag groups plus its
 * checklists, as completed checklist tasks are kept in the same "tags" field of a record.
 * Returns undefined when the tags of the entity have not been loaded yet, so that nothing is
 * removed from records whose tags are unknown.
 */
export const getAssignableTagIds = (entityTags: TagTreeNode[] | undefined, checklists: FormMenuTag[] | undefined): Set<number> | undefined => {
  const tagIds = getAvailableTagIds(entityTags);
  if (!tagIds) return undefined;

  const checklistIds = getMenuTagIds(checklists);
  if (checklistIds) checklistIds.forEach(id => tagIds.add(id));

  return tagIds;
};

/**
 * Removes tag ids which are not available for the entity from a record before it is saved, so that
 * tags of deleted/unassigned tag groups are not persisted on the next save of the record.
 */
export const removeUnavailableEntityTags = <T extends { tags?: number[] }>(item: T, assignableTagIds: Set<number> | undefined): T => {
  if (!assignableTagIds || !Array.isArray(item?.tags)) return item;

  const tags = removeUnavailableTagIds(item.tags, assignableTagIds);

  return tags.length === item.tags.length ? item : { ...item, tags };
};

export const getTagNamesSuggestions = (tags: Tag[]) => {
  const allTags = getAllTags(tags);

  return allTags.map(i => {
    const name = i.name.replace(/\s/g, "_");

    return {
      token: "Identifier",
      value: name,
      label: name
    };
  });
};

export const COLORS = [
  "9e0142", "d53e4f", "f46d43", "fdae61", "fee08b", "ffffbf", "e6f598", "abdda4", "66c2a5", "3288bd", "5e4fa2",
  "a30fe9", "480fec", "d7e90f", "e0e0e0", "bababa", "878787", "4d4d4d", "abd9e9", "74add1", "66bd63", "1a9850",
  "006837", "a50026", "d73027"
];

export const plainTagToCatalogItem = (r: DataRow): CatalogItemType => ({
  id: Number(r.id),
  title: r.values[0],
  installed: true,
  enabled: true,
  hideDot: true,
  hideShortDescription: true
});

export const setChildTagsWeight = (items: FormTag[]): Tag[] =>
  items.map((i, index) => {
    let item = { ...i, weight: index + 1 };

    delete item.parent;
    delete item.refreshFlag;

    if (item.id.toString().includes("new")) {
      item.id = null;
    }

    if (item.childTags.length) {
      item = { ...item, childTags: setChildTagsWeight([...item.childTags]) };
    }

    return item;
  });

export const rootTagToServerModel = (formTag: FormTag): Tag => {
  const tag = { ...formTag, childTags: setChildTagsWeight([...formTag.childTags]) };

  delete tag.parent;
  delete tag.rootTag;
  delete tag.refreshFlag;

  if (!tag.weight) tag.weight = 1;
  
  return  tag;
};