/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { Diff } from "@api/model";
import { Epic } from "redux-observable";
import { EntityName } from "../../../../model/entities/common";
import { FETCH_SUCCESS } from "../../../actions";
import FetchErrorHandler from "../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../epics/EpicUtils";
import EntityService from "../../../services/EntityService";
import { BULK_CHANGE_RECORDS, GET_RECORDS_REQUEST } from "../actions";

const request: EpicUtils.Request<any, { entity: EntityName, diff: Diff }> = {
  type: BULK_CHANGE_RECORDS,
  getData: ({ entity, diff }) => EntityService.bulkChange(entity, diff),
  processData: (v, s, { entity }) => [
    {
      type: FETCH_SUCCESS,
      payload: { message: "Bulk change completed" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity, listUpdate: true }
    }
  ],
  processError: response => {
    const isClientReject = typeof response === "string";

    // @ts-ignore
    return FetchErrorHandler(...isClientReject ? [null, response] : [response]);
  }
};

export const EpicBulkChangeRecords: Epic<any, any> = EpicUtils.Create(request);
