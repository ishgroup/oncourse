/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse } from "@api/model";
import { SelectItemDefault, sortDefaultSelectItems } from "ish-ui";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_VIRTUAL_SITES, GET_VIRTUAL_SITES_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: GET_VIRTUAL_SITES,
  getData: () => EntityService.getPlainRecords("Site", "name,rooms.id,rooms.name", "isVirtual == true"),
  processData: (response: DataResponse) => {
    const virualSites: SelectItemDefault[] = response.rows.map(({ id, values }) => {
      const roomNames = values[2].replace(/[\[\]]/g, '')?.split(',');
      return {
        value: Number(id),
        label: values[0],
        rooms: JSON.parse(values[1])?.map((id, index) => ({ id, name: roomNames[index]?.trim() }))
      };
    });

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