import { combineEpics } from "redux-observable";
import { EpicDeleteDocument } from "./EpicDeleteDocument";
import { EpicUpdateDocumentItem } from "./EpicUpdateDocument";
import { EpicRestoreDocument } from "./EpicRestoreDocument";

export const EpicEditDocument = combineEpics(EpicRestoreDocument, EpicDeleteDocument, EpicUpdateDocumentItem);
