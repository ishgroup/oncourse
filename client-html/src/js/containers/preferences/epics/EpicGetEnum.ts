/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import PreferencesService from "../services/PreferencesService";
import { GET_ENUM_FULFILLED, GET_ENUM_REQUEST } from "../actions";
import { EnumItem } from "@api/model";
import { sortDefaultSelectItems } from "../../../common/utils/common";

const request: EpicUtils.Request = {
  type: GET_ENUM_REQUEST,
  getData: payload => PreferencesService.getEnum(payload.name),
  processData: (enums: EnumItem[], state: any, payload) => {
    enums.sort(sortDefaultSelectItems);

    return [
      {
        type: GET_ENUM_FULFILLED,
        payload: { enums, type: payload.name }
      }
    ];
  }
};

export const EpicGetEnum: Epic<any, any> = EpicUtils.Create(request);
