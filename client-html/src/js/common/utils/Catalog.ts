/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DataRow } from "@api/model";
import { CatalogItemType } from "../../model/common/Catalog";

export const CATALOG_ITEM_COLUMNS = "name,category,automationStatus,automationTags,shortDescription,keyCode";

export const mapListToCatalogItem = (r: DataRow): CatalogItemType => ({
  id: Number(r.id),
  title: r.values[0],
  category: r.values[1],
  installed: !r.values[5]?.startsWith("ish.") || r.values[2] !== "Not Installed",
  enabled: r.values[2] === "Enabled",
  tags: !r.values[5]?.startsWith("ish.") ? (r.values[3] ? r.values[3] += ",custom" : r.values[3] = "custom") : r.values[3],
  shortDescription: r.values[4],
  keyCode: r.values[5]
});