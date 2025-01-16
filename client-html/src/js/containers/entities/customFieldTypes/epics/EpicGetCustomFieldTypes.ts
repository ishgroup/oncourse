/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CustomFieldType, DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_CUSTOM_FIELD_TYPES, GET_CUSTOM_FIELD_TYPES_FULFILLED } from "../actions";
import { mapCustomFieldsResponse } from "../utils";

const request: EpicUtils.Request<any, string> = {
  type: GET_CUSTOM_FIELD_TYPES,
  getData: entity => EntityService.getPlainRecords(
      "CustomFieldType",
      "key,name,defaultValue,isMandatory,dataType,sortOrder,pattern",
      `entityIdentifier=${entity} and dataType !== "File"`
    ),
  processData: (response: DataResponse, state, entity: string) => {
    const types = response.rows.map(mapCustomFieldsResponse);

    types.sort((a, b) => (a.sortOrder > b.sortOrder ? 1 : -1));

    return [
      {
        type: GET_CUSTOM_FIELD_TYPES_FULFILLED,
        payload: { types, entity }
      }
    ];
  }
};

export const EpicGetCustomFieldTypes: Epic<any, any> = EpicUtils.Create(request);
