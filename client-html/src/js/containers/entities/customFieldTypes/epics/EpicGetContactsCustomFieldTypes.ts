/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { DataResponse} from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import {
  GET_CONTACT_CUSTOM_FIELD_TYPES,
  GET_CUSTOM_FIELD_TYPES_FULFILLED,
} from "../actions";
import { mapCustomFieldsResponse } from "../utils";

const request: EpicUtils.Request<any, string> = {
  type: GET_CONTACT_CUSTOM_FIELD_TYPES,
  getData: async () => {
    const result = {};
    for (const key of ["Contact", "Student", "Tutor"]) {
      const entity = key;
      result[entity] = await EntityService.getPlainRecords(
        "CustomFieldType",
        "key,name,defaultValue,isMandatory,dataType,sortOrder,pattern",
        `entityIdentifier=${entity}`
      ).then((response: DataResponse) => {
        const types = response.rows.map(mapCustomFieldsResponse);
        types.sort((a, b) => (a.sortOrder > b.sortOrder ? 1 : -1));
        return types;
      });
    }
    return result;
  },
  processData: result => Object.keys(result).map(entity => ({
    type: GET_CUSTOM_FIELD_TYPES_FULFILLED,
    payload: { types: result[entity], entity }
  }))
};

export const EpicGetContactsCustomFieldsTypes: Epic<any, any> = EpicUtils.Create(request);