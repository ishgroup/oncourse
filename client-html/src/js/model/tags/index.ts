/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";
import { NumberArgFunction } from "../common/CommonFunctions";

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

export interface FormTag extends Tag {
  parent?: string;
  refreshFlag?: boolean;
  childTags?: FormTag[];
}

export interface FormTagProps {
  classes: any;
  onDelete: any;
  item: FormTag;
  isEditing?: boolean;
  setIsEditing?: NumberArgFunction;
  changeVisibility?: any;
  snapshot?: any;
  provided?: any;
}
