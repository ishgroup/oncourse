/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_SCRIPT_ENTITY_FULFILLED, GET_SCRIPT_ENTITY_REQUEST } from "../actions";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "../constants";
import ScriptsService from "../services/ScriptsService";
import { getQueryReturnValueForRender, ParseScriptBody } from "../utils";

const request: EpicUtils.Request = {
  type: GET_SCRIPT_ENTITY_REQUEST,
  getData: id => ScriptsService.getScriptItem(id).then(response => ParseScriptBody(response)),
  processData: (editRecord: any) => {
    const editRecordCopy = JSON.parse(JSON.stringify(editRecord));
    const valueToSave = [];

    const updatedComponents = editRecordCopy?.components.map(component => {
      if (component?.type === "Query") {
        const returnedQueryValue = getQueryReturnValueForRender(component.queryClosureReturnValue) as any;

        if (returnedQueryValue.value) {
          component.queryClosureReturnValue = returnedQueryValue.value;
          valueToSave.push({
            id: component.id,
            prefixValue: returnedQueryValue.prefix.join(" "),
          });
        } else {
          component.queryClosureReturnValue = returnedQueryValue;
        }
      }
      return component;
    });

    editRecordCopy.components = updatedComponents;
    if (valueToSave.length) editRecordCopy.savedQuerypPrefixes = valueToSave;

    return [
      { type: GET_SCRIPT_ENTITY_FULFILLED },
      initialize(SCRIPT_EDIT_VIEW_FORM_NAME, editRecordCopy),
    ];
  },
  processError: response => [...FetchErrorHandler(response, "Failed to get script"), initialize(SCRIPT_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetScriptItem: Epic<any, any> = EpicUtils.Create(request);
