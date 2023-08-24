/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { EntityRelationType } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { DELETE_ENTITY_RELATION_TYPE_FULFILLED, DELETE_ENTITY_RELATION_TYPE_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
    type: DELETE_ENTITY_RELATION_TYPE_REQUEST,
    getData: payload => PreferencesService.deleteEntityRelationType(payload.id),
    retrieveData: () => PreferencesService.getEntityRelationTypes(),
    processData: (items: EntityRelationType[]) => {
        return [
            {
                type: DELETE_ENTITY_RELATION_TYPE_FULFILLED,
                payload: { entityRelationTypes: items }
            },
            {
                type: FETCH_SUCCESS,
                payload: { message: "Sellable item was successfully deleted" }
            }
        ];
    },
    processError: response => {
        return FetchErrorHandler(response, "Error. Sellable item was not deleted");
    }
};

export const EpicDeleteEntityRelationType: Epic<any, any> = EpicUtils.Create(request);
