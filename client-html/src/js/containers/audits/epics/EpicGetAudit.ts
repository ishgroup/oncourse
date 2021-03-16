/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_AUDIT_ITEM_FULFILLED, GET_AUDIT_ITEM_REQUEST } from "../actions";
import { getEntityItemById } from "../../entities/common/entityItemsService";
import { SET_LIST_EDIT_RECORD } from "../../../common/components/list-view/actions";
import { initialize } from "redux-form";
import { format } from "date-fns";
import { EEE_D_MMM_YYYY } from "../../../common/utils/dates/format";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_AUDIT_ITEM_REQUEST,
  getData: payload => getEntityItemById("Audit", payload),
  processData: (item: any) => {
    return [
      {
        type: GET_AUDIT_ITEM_FULFILLED
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: {
          editRecord: item,
          name: `${item.entityIdentifier} ${item.action} ${format(new Date(item.created), EEE_D_MMM_YYYY)}`
        }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, item)
    ];
  }
};

export const EpicGetAudit: Epic<any, any> = EpicUtils.Create(request);
