/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DataResponse } from "@api/model";
import {
  GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES,
  GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES_FULFILLED
} from "../actions";
import EntityService from "../../../../common/services/EntityService";
import { NestedListPanelItem } from "../../../../common/components/form/nestedList/NestedList";

const request: EpicUtils.Request<any, any, any> = {
  type: GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES,
  getData: () => {
    return EntityService.getPlainRecords("ContactRelationType", "toContactName", "");
  },
  processData: (response: DataResponse) => {
    return [
      {
        type: GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES_FULFILLED,
        payload: {
          contactRelationTypes: response.rows
            .map(
              ({ id, values }) =>
                ({
                  id: Number(id),
                  description: values[0]
                } as NestedListPanelItem)
            )
            .sort((a, b) => (a.id < b.id ? -1 : a.id > b.id ? 1 : 0))
        }
      }
    ];
  }
};

export const EpicGetMembershipProductContactRelationTypes: Epic<any, any> = EpicUtils.Create(request);
