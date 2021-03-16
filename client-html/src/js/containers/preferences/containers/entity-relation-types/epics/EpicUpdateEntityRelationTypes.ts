/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { UPDATE_ENTITY_RELATION_TYPES_FULFILLED, UPDATE_ENTITY_RELATION_TYPES_REQUEST } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { EntityRelationType } from "@api/model";

const request: EpicUtils.Request = {
    type: UPDATE_ENTITY_RELATION_TYPES_REQUEST,
    getData: payload => PreferencesService.updateEntityRelationTypes(payload.entityRelationTypes),
    retrieveData: () => PreferencesService.getEntityRelationTypes(),
    processData: (items: EntityRelationType[]) => {
        return [
            {
                type: UPDATE_ENTITY_RELATION_TYPES_FULFILLED,
                payload: { entityRelationTypes: items }
            },
            {
                type: FETCH_SUCCESS,
                payload: { message: "Sellable items were successfully updated" }
            }
        ];
    },
    processError: response => {
        return FetchErrorHandler(response, "Error. Sellable items were not updated");
    }
};

export const EpicUpdateEntityRelationTypes: Epic<any, any> = EpicUtils.Create(request);
