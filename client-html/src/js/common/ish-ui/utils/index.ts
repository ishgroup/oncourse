/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { MenuTag, TagLike } from "../model/Fields";

export const getAllMenuTags = (tags: MenuTag<TagLike>[], res?: MenuTag<TagLike>[], path = ""): MenuTag<TagLike>[] => {
  const result = res || [];

  for (let i = 0; i < tags.length; i++) {
    const tag = tags[i];
    tag.path = path;
    result.push(tag);

    if (tag.children.length) {
      getAllMenuTags(tags[i].children, result, `${path ? `${path} / ` : ""}${(tag.tagBody && tag.tagBody.name) || ""}`);
    }
  }
  return result;
};