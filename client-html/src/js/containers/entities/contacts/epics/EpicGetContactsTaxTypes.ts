/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tax } from "@api/model";
import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_CONTACTS_TAX_TYPES, GET_CONTACTS_TAX_TYPES_FULFILLED } from "../actions";
import ContactsService from "../services/ContactsService";

const request: EpicUtils.Request = {
  type: GET_CONTACTS_TAX_TYPES,
  getData: () => ContactsService.getContactsTaxTypes(),
  processData: (taxTypes: Tax[]) => {
    return [
      {
        type: GET_CONTACTS_TAX_TYPES_FULFILLED,
        payload: { taxTypes: taxTypes.filter(t => t.code !== "*") }
      }
    ];
  }
};

export const EpicGetContactsTaxTypes: Epic<any, any> = EpicUtils.Create(request);
