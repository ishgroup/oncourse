import { IAction } from "../../../../actions/IshAction";
import { DocumentsState } from "./state";
import { CLEAR_EDITING_DOCUMENT, SET_DOCUMENT_FILE, SET_EDITING_DOCUMENT, SET_SEARCH_DOCUMENTS } from "../actions";

class DocumentsInitialState implements DocumentsState {
  editingFormName: null;

  editingDocument: null;

  documentFile: null;

  searchDocuments: null;

  tags: [];

  viewDocument: false;
}

export const documentReducer = (state: DocumentsState = new DocumentsInitialState(), action: IAction<any>): any => {
  switch (action.type) {
    case SET_EDITING_DOCUMENT:
    case CLEAR_EDITING_DOCUMENT:
    case SET_DOCUMENT_FILE:
    case SET_SEARCH_DOCUMENTS: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
