/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_GRADING_TYPES_REQUEST, GET_GRADING_TYPES_FULFILLED } from "../../../actions";

const request: EpicUtils.Request = {
  type: GET_GRADING_TYPES_REQUEST,
  getData: () => PreferencesService.getGradingTypes(),
  processData: types => [
      {
        type: GET_GRADING_TYPES_FULFILLED,
        payload: types
      }
    ]
};

export const EpicGetGradingTypes: Epic<any, any> = EpicUtils.Create(request);
