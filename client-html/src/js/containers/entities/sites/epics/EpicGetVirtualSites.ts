/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_VIRTUAL_SITES, GET_VIRTUAL_SITES_FULFILLED } from "../actions";
import { SelectItemDefault } from "../../../../model/entities/common";
import { sortDefaultSelectItems } from "../../../../common/utils/common";

const request: EpicUtils.Request<any, any> = {
  type: GET_VIRTUAL_SITES,
  getData: () => EntityService.getPlainRecords("Site", "name", "isVirtual == true"),
  processData: (response: DataResponse) => {
    const virualSites: SelectItemDefault[] = response.rows.map(({ id, values }) => ({
      value: Number(id),
      label: values[0]
    }));

    virualSites.sort(sortDefaultSelectItems);

    return [
      {
        type: GET_VIRTUAL_SITES_FULFILLED,
        payload: { virualSites }
      }
    ];
  }
};

export const EpicGetVirtualSites: Epic<any, any> = EpicUtils.Create(request);
