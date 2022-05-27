/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ReactNode } from "react";

export interface CatalogItemType {
  id: number;
  title: string;
  category?: string;
  titleAdornment?: ReactNode;
  installed?: boolean;
  enabled?: boolean;
  tags?: string;
  shortDescription?: ReactNode;
  keyCode?: string;
  hideShortDescription?: boolean;
  hideDot?: boolean;
}

export interface CatalogData {
  installed: CatalogItemType[],
  other: CatalogItemType[],
  categories: {
    [key: string]: CatalogItemType[]
  }
}