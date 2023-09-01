/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ContactRelationType } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { UPDATE_CONTACT_RELATION_TYPES_FULFILLED, UPDATE_CONTACT_RELATION_TYPES_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: UPDATE_CONTACT_RELATION_TYPES_REQUEST,
  getData: payload => PreferencesService.updateContactRelationTypes(payload.contactRelationTypes),
  retrieveData: () => PreferencesService.getContactRelationTypes(),
  processData: (items: ContactRelationType[]) => {
    return [
      {
        type: UPDATE_CONTACT_RELATION_TYPES_FULFILLED,
        payload: { contactRelationTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Contact Relation Types were successfully updated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Contact Relation Types was not updated");
  }
};

export const EpicUpdateContactRelationTypes: Epic<any, any> = EpicUtils.Create(request);
