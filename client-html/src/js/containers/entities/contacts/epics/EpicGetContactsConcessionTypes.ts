/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { ConcessionType } from "@api/model";
import { GET_CONTACTS_CONCESSION_TYPES, GET_CONTACTS_CONCESSION_TYPES_FULFILLED } from "../actions";
import ContactsService from "../services/ContactsService";

const request: EpicUtils.Request = {
  type: GET_CONTACTS_CONCESSION_TYPES,
  getData: () => ContactsService.getContactsConcessionTypes(),
  processData: (contactsConcessionTypes: ConcessionType[]) => {
    return [
      {
        type: GET_CONTACTS_CONCESSION_TYPES_FULFILLED,
        payload: { contactsConcessionTypes }
      }
    ];
  }
};

export const EpicGetContactsConcessionTypes: Epic<any, any> = EpicUtils.Create(request);
