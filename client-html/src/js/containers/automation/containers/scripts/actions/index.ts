/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ExecuteScriptRequest, OutputType, Script } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../../common/actions/ActionUtils";
import { ScriptComponent, ScriptViewMode } from "../../../../../model/scripts";
import { ApiMethods } from "../../../../../model/common/apiHandlers";

export const GET_SCRIPTS_LIST = _toRequestType("get/scripts/list");
export const GET_SCRIPTS_LIST_FULFILLED = FULFILLED(GET_SCRIPTS_LIST);

export const GET_SCRIPT_ENTITY_REQUEST = _toRequestType("get/scriptItem");
export const GET_SCRIPT_ENTITY_FULFILLED = FULFILLED(GET_SCRIPT_ENTITY_REQUEST);

export const UPDATE_SCRIPT_ENTITY_REQUEST = _toRequestType("put/scriptItem");
export const UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED = FULFILLED(UPDATE_SCRIPT_ENTITY_REQUEST);

export const POST_SCRIPT_ENTITY_REQUEST = _toRequestType("post/scriptItem");
export const POST_SCRIPT_ENTITY_FULFILLED = FULFILLED(POST_SCRIPT_ENTITY_REQUEST);

export const POST_SCRIPT_RUN_REQUEST = _toRequestType("post/scriptItem/execute");
export const POST_SCRIPT_RUN_REQUEST_FULFILLED = FULFILLED(POST_SCRIPT_RUN_REQUEST);

export const GET_RUN_SCRIPT_RESULT = _toRequestType("get/runScript/result");
export const GET_RUN_SCRIPT_RESULT_FULFILLED = FULFILLED(GET_RUN_SCRIPT_RESULT);

export const OPEN_RUN_SCRIPT_PDF = _toRequestType("open/runScript/pdf");
export const OPEN_RUN_SCRIPT_PDF_FULFILLED = FULFILLED(OPEN_RUN_SCRIPT_PDF);

export const DELETE_SCRIPT_ENTITY_REQUEST = _toRequestType("delete/scriptItem");
export const DELETE_SCRIPT_ENTITY_FULFILLED = FULFILLED(DELETE_SCRIPT_ENTITY_REQUEST);

export const SET_SCRIPT_COMPONENTS = "set/script/components";

export const getScriptsList = (nameToSelect?: string, selectFirst?: boolean) => ({
  type: GET_SCRIPTS_LIST,
  payload: { nameToSelect, selectFirst }
});

export const runScript = (executeScriptRequest: ExecuteScriptRequest, outputType: OutputType, name: string ) => ({
  type: POST_SCRIPT_RUN_REQUEST,
  payload: { executeScriptRequest, outputType, name }
});

export const getRunScriptResult = (processId: string, outputType: OutputType, name: string ) => ({
  type: GET_RUN_SCRIPT_RESULT,
  payload: { processId, outputType, name }
});

export const openRunScriptPdf = (processId: string) => ({
  type: OPEN_RUN_SCRIPT_PDF,
  payload: { processId }
});

export const getScriptItem = (id: number) => ({
  type: GET_SCRIPT_ENTITY_REQUEST,
  payload: id
});

export const saveScriptItem = (id: number, script: Script, method: ApiMethods, viewMode: ScriptViewMode) => ({
  type: UPDATE_SCRIPT_ENTITY_REQUEST,
  payload: {
   id, script, method, viewMode
  }
});

export const createScriptItem = (script: Script, viewMode: ScriptViewMode) => ({
  type: POST_SCRIPT_ENTITY_REQUEST,
  payload: { script, viewMode }
});

export const deleteScriptItem = (id: number) => ({
  type: DELETE_SCRIPT_ENTITY_REQUEST,
  payload: { id }
});

export const setScriptComponents = (components: ScriptComponent[]) => ({
  type: SET_SCRIPT_COMPONENTS,
  payload: { components }
});
