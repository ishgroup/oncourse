/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DataRow, Tag } from "@api/model";
import { CatalogItemType } from "../../../model/common/Catalog";
import { FormTag } from "../../../model/tags";

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