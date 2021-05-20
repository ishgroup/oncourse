import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import DocumentsService from "../../../../common/components/form/documents/services/DocumentsService";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_DOCUMENT_ITEM, CREATE_DOCUMENT_ITEM_FULFILLED } from "../actions";

let savedItem;

const request: EpicUtils.Request = {
  type: CREATE_DOCUMENT_ITEM,
  getData: payload => {
    savedItem = payload.document;
    const {
      name,
      description,
      shared,
      access,
      content,
      tags,
      versions
    } = payload.document;

    return DocumentsService.createDocument(
      name,
      description,
      shared,
      access,
      content,
      tags,
      (Array.isArray(versions) && versions[0].fileName) || content.name
    );
  },
  processData: () => [
    {
      type: CREATE_DOCUMENT_ITEM_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Document was created" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "Document" }
    },
    setListSelection([]),
    clearListNestedEditRecord(0),
    initialize(LIST_EDIT_VIEW_FORM_NAME, null)
  ],
  processError: response => [
    ...FetchErrorHandler(response, "Document Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateDocument: Epic<any, any> = EpicUtils.Create(request);
