/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { FormTag } from "../../model/tags";
import { COLORS } from "./utils";

export const TAGS_FORM_NAME = "TagsForm";

export const ENTITY_TAGS_EXPAND_SETTINGS_KEY = "EntityTagExpandSettings";

export const EmptyTag: FormTag = {
  id: null,
  name: "",
  type: "Tag",
  status: "Private",
  system: false,
  urlPath: null,
  content: "",
  weight: 1,
  taggedRecordsCount: 0,
  created: null,
  modified: null,
  color: COLORS[Math.floor(Math.random() * COLORS.length)],
  requirements: [],
  childTags: [],
};
