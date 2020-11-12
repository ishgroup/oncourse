/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_RECORDS_FOR_FIELD_REQUEST } from "../actions";
import { Contact, DataResponse } from "@api/model";
import { change } from "redux-form";

const request: EpicUtils.Request<
  any,
  any,
  { entityName: string; columns: string[]; form: string; field: string; search?: string }
> = {
  type: GET_RECORDS_FOR_FIELD_REQUEST,
  getData: payload => {
    return EntityService.getPlainRecords(payload.entityName, payload.columns.join(","));
  },
  processData: (response: DataResponse, state, payload) => {
    const items: Contact[] = response.rows.map(({ id, values }) => {
      const record = { id: Number(id) };
      payload.columns.forEach((value, index) => (record[value] = values[index]));
      return record;
    });

    return [change(payload.form, payload.field, items)];
  }
};

export const EpicGetRecords: Epic<any, any> = EpicUtils.Create(request);
