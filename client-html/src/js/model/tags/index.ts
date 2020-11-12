/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";

export interface MenuTag {
  active: boolean;
  tagBody: Tag;
  children: MenuTag[];
  parent?: MenuTag;
  entity?: string;
  path?: string;
  prefix?: string;
  queryPrefix?: string;
  indeterminate?: boolean;
}
