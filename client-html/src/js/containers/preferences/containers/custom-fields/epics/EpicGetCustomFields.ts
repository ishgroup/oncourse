/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CustomFieldType } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_CUSTOM_FIELDS_FULFILLED, GET_CUSTOM_FIELDS_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request<any, any> = {
  type: GET_CUSTOM_FIELDS_REQUEST,
  getData: () => PreferencesService.getCustomFields(),
  processData: (items: CustomFieldType[]) => [
      {
        type: GET_CUSTOM_FIELDS_FULFILLED,
        payload: { customFields: items }
      },
      initialize("CustomFieldsForm", { types: items })
    ]
};

export const EpicGetCustomFields: Epic<any, any> = EpicUtils.Create(request);
