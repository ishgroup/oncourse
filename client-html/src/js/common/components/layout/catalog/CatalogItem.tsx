/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import { ListItem, ListItemText, Divider } from "@mui/material";
import { CatalogItemType } from "../../../../model/common/Catalog";

const CatalogItem = (
  {
    title,
    category,
    installed,
    enabled,
    tag,
    shortDescription,
  }: CatalogItemType
) => (
  <>
    <ListItem button className="p-0">
      <ListItemText
        classes={{
          primary: "text-bold"
        }}
        primary={title}
        secondary={shortDescription || "No description"}
      />

    </ListItem>
    <Divider light />
  </>

);

export default CatalogItem;