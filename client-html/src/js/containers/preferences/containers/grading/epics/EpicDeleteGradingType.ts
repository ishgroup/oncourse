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
import { GET_GRADING_TYPES_REQUEST, DELETE_GRADING_TYPE_REQUEST } from "../../../actions";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request = {
  type: DELETE_GRADING_TYPE_REQUEST,
  getData: id => PreferencesService.deleteGradingType(id),
  processData: () => [
    {
      type: GET_GRADING_TYPES_REQUEST,
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Grading type was successfully deleted" }
    }
  ]
};

export const EpicDeleteGradingType: Epic<any, any> = EpicUtils.Create(request);
