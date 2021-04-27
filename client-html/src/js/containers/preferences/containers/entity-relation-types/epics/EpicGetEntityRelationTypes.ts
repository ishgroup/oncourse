/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_ENTITY_RELATION_TYPES_FULFILLED, GET_ENTITY_RELATION_TYPES_REQUEST } from "../../../actions";
import { EntityRelationType } from "@api/model";

const request: EpicUtils.Request = {
    type: GET_ENTITY_RELATION_TYPES_REQUEST,
    getData: () => PreferencesService.getEntityRelationTypes(),
    processData: (items: EntityRelationType[]) => {
        return [
            {
                type: GET_ENTITY_RELATION_TYPES_FULFILLED,
                payload: { entityRelationTypes: items }
            }
        ];
    }
};

export const EpicGetEntityRelationTypes: Epic<any, any> = EpicUtils.Create(request);
