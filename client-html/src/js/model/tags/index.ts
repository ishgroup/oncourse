/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";
import { MenuTag, NumberArgFunction } from "ish-ui";

export type FormMenuTag = MenuTag<Tag>;

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

export interface SpecialTypeTagsFormValues {
  types: Tag[];
}
