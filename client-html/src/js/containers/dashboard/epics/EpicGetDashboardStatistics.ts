/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_DASHBOARD_STATISTIC, GET_DASHBOARD_STATISTIC_FULFILLED } from "../actions";
import DashboardService from "../services/DashboardService";
import { StatisticData } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_DASHBOARD_STATISTIC,
  getData: () => DashboardService.getStatisticData(),
  processData: (data: StatisticData) => {
    return [
      {
        type: GET_DASHBOARD_STATISTIC_FULFILLED,
        payload: { statistics: { updating: false, data } }
      }
    ];
  }
};

export const EpicGetDashboardStatistics: Epic<any, any> = EpicUtils.Create(request);
