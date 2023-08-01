/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ContactRelationType } from "@api/model";
import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_CONTACT_RELATION_TYPES_FULFILLED, GET_CONTACT_RELATION_TYPES_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_CONTACT_RELATION_TYPES_REQUEST,
  getData: () => PreferencesService.getContactRelationTypes(),
  processData: (items: ContactRelationType[]) => {
    return [
      {
        type: GET_CONTACT_RELATION_TYPES_FULFILLED,
        payload: { contactRelationTypes: items }
      }
    ];
  }
};

export const EpicGetContactRelationTypes: Epic<any, any> = EpicUtils.Create(request);
