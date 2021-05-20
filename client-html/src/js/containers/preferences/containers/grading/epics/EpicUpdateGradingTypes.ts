/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_GRADING_TYPES_REQUEST, UPDATE_GRADING_TYPES_REQUEST } from "../../../actions";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request = {
  type: UPDATE_GRADING_TYPES_REQUEST,
  getData: types => PreferencesService.updateGradingTypes(types),
  processData: () => [
    {
      type: GET_GRADING_TYPES_REQUEST,
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Grading types were successfully updated" }
    }
  ]
};

export const EpicUpdateGradingTypes: Epic<any, any> = EpicUtils.Create(request);
