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

const request: EpicUtils.Request<any, any, any> = {
    type: UPDATE_ENTITY_RELATION_TYPES_REQUEST,
    getData: payload => PreferencesService.updateEntityRelationTypes(payload.contactRelationTypes),
    retrieveData: () => PreferencesService.getEntityRelationTypes(),
    processData: (items: EntityRelationType[]) => {
        return [
            {
                type: UPDATE_ENTITY_RELATION_TYPES_FULFILLED,
                payload: { contactRelationTypes: items }
            },
            {
                type: FETCH_SUCCESS,
                payload: { message: "Entity Relation Types were successfully updated" }
            }
        ];
    },
    processError: response => {
        return FetchErrorHandler(response, "Error. Entity Relation Types was not updated");
    }
};

export const EpicUpdateEntityRelationTypes: Epic<any, any> = EpicUtils.Create(request);
