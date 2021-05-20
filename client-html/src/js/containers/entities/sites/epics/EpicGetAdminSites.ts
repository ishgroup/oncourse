/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_ADMINISTRATION_SITES, GET_ADMINISTRATION_SITES_FULFILLED } from "../actions";
import { SelectItemDefault } from "../../../../model/entities/common";
import { sortDefaultSelectItems } from "../../../../common/utils/common";

const request: EpicUtils.Request = {
  type: GET_ADMINISTRATION_SITES,
  getData: () => EntityService.getPlainRecords("Site", "name", "isAdministrationCentre == true"),
  processData: (response: DataResponse) => {
    const adminSites: SelectItemDefault[] = response.rows.map(({ id, values }) => ({
      value: Number(id),
      label: values[0]
    }));

    adminSites.sort(sortDefaultSelectItems);

    return [
      {
        type: GET_ADMINISTRATION_SITES_FULFILLED,
        payload: { adminSites }
      }
    ];
  }
};

export const EpicGetAdminSites: Epic<any, any> = EpicUtils.Create(request);
