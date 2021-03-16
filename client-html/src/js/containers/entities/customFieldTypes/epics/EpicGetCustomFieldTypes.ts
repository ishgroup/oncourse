/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { CustomFieldType, DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { transformDataType } from "../../common/utils";
import { GET_CUSTOM_FIELD_TYPES, GET_CUSTOM_FIELD_TYPES_FULFILLED } from "../actions";

const request: EpicUtils.Request<any, string> = {
  type: GET_CUSTOM_FIELD_TYPES,
  getData: entity => EntityService.getPlainRecords(
      "CustomFieldType",
      "key,name,defaultValue,isMandatory,dataType,sortOrder",
      `entityIdentifier=${entity}`
    ),
  processData: (response: DataResponse, state, entity: string) => {
    const types = response.rows.map(
      item =>
        ({
          id: item.id,
          fieldKey: item.values[0],
          name: item.values[1],
          defaultValue: item.values[2],
          mandatory: item.values[3] === "true",
          dataType: item.values[4] === "URL" ? item.values[4] : transformDataType(item.values[4]),
          sortOrder: Number(item.values[5])
        } as CustomFieldType)
    );

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
