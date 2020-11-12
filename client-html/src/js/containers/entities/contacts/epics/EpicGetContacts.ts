/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Contact, DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_CONTACTS, GET_CONTACTS_FULFILLED } from "../actions";
import { State } from "../../../../reducers/state";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const defaultContactMap = ({ id, values }) => ({
  id: Number(id),
  firstName: values[0],
  lastName: values[1],
  email: values[2],
  birthDate: values[3]
});

const request: EpicUtils.Request<
  DataResponse,
  State,
  { offset?: number; columns?: string; ascending?: boolean; sort?: string; pageSize?: number }
> = {
  type: GET_CONTACTS,
  hideLoadIndicator: true,
  getData: ({
    offset,
    columns,
    ascending,
    sort,
    pageSize
  },
  {
    contacts: { search }
  }) => EntityService.getPlainRecords(
    "Contact",
    columns || "firstName,lastName,email,birthDate",
    search,
    pageSize || 100,
    offset,
    sort || "firstName,lastName",
    ascending
  ),
  processData: (records, s, { columns }) => {
    const { rows, offset, pageSize } = records;
    const items: Contact[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultContactMap);

    return [
      {
        type: GET_CONTACTS_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetContacts: Epic<any, any> = EpicUtils.Create(request);
