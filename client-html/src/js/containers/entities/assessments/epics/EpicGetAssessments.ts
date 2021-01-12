/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Assessment } from "@api/model";
import { clearActionsQueue } from "../../../../common/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_ASSESSMENT_ITEMS, GET_ASSESSMENT_ITEMS_FULFILLED } from "../actions";
import { getCustomColumnsMap } from "../../../../common/utils/common";

export const defaultAssessmentMap = ({ id, values }) => ({
  id: Number(id),
  code: values[0],
  name: values[1]
});

const request: EpicUtils.Request<any, any, any> = {
  type: GET_ASSESSMENT_ITEMS,
  hideLoadIndicator: true,
  getData({ offset, columns, ascending }, { assessments: { search } }) {
    return EntityService.getPlainRecords("Assessment", columns || "code,name", search, 100, offset, "", ascending);
  },
  processData({ rows, offset, pageSize }, s, { columns }) {
    const items: Assessment[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultAssessmentMap);

    return [
      {
        type: GET_ASSESSMENT_ITEMS_FULFILLED,
        payload: { items, offset, pageSize }
      },
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ];
  }
};

export const EpicGetAssessments: Epic<any, any> = EpicUtils.Create(request);
