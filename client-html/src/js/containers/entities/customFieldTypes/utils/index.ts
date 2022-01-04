/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CustomFieldType } from "@api/model";
import { transformDataType } from "../../common/utils";

export const processCustomFields = (entity: any, customFieldsKey?: string) => {
  if (!customFieldsKey) {
    customFieldsKey = "customFields";
  }

  if (entity && entity[customFieldsKey]) {
    Object.keys(entity[customFieldsKey]).forEach(k => {
      if (!entity[customFieldsKey][k]) {
        delete entity[customFieldsKey][k];
      }
    });
  }
};

export const mapCustomFieldsResponse = item =>
  ({
    id: item.id,
    fieldKey: item.values[0],
    name: item.values[1],
    defaultValue: item.values[2],
    mandatory: item.values[3] === "true",
    dataType: item.values[4] === "URL" ? item.values[4] : transformDataType(item.values[4]),
    sortOrder: Number(item.values[5]),
    pattern: item.values[6]
  } as CustomFieldType);
