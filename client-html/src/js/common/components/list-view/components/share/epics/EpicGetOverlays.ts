/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { change } from "redux-form";
import { DataResponse, ReportOverlay } from "@api/model";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { GET_OVERLAY_ITEMS, GET_OVERLAY_ITEMS_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../services/EntityService";
import { CommonListItem } from "../../../../../../model/common/sidebar";

const request: EpicUtils.Request<any, { overlayToSelect?: string }> = {
  type: GET_OVERLAY_ITEMS,
  getData: () => EntityService.getPlainRecords("ReportOverlay", "name", null, null, null, "name", true),
  processData: (response: DataResponse, s, p) => {
    const overlays: CommonListItem[] = response.rows.map(r => ({
      id: Number(r.id),
      name: r.values[0]
    }));

    let selectItem: CommonListItem;

    if (p && p.overlayToSelect) {
      selectItem = overlays.find(i => i.name === p.overlayToSelect);
    }

    return [
      {
        type: GET_OVERLAY_ITEMS_FULFILLED,
        payload: { overlays }
      },
      ...(selectItem ? [change("ListShareForm", "backgroundId", selectItem.id)] : [])
    ];
  },
  processError: response => {
    if (response && response.status === 403) {
      return [];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicGetOverlays: Epic<any, any> = EpicUtils.Create(request);
