/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { DELETE_CONTACT_RELATION_TYPE_FULFILLED, DELETE_CONTACT_RELATION_TYPE_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { ContactRelationType } from "@api/model";

const request: EpicUtils.Request = {
  type: DELETE_CONTACT_RELATION_TYPE_REQUEST,
  getData: payload => PreferencesService.deleteContactRelationType(payload.id),
  retrieveData: () => PreferencesService.getContactRelationTypes(),
  processData: (items: ContactRelationType[]) => {
    return [
      {
        type: DELETE_CONTACT_RELATION_TYPE_FULFILLED,
        payload: { contactRelationTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Contact Relation Type was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Contact Relation Type was not deleted");
  }
};

export const EpicDeleteContactRelationType: Epic<any, any> = EpicUtils.Create(request);
