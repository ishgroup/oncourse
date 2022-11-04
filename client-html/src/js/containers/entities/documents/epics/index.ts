import { combineEpics } from "redux-observable";
import { EpicDeleteDocument } from "./EpicDeleteDocument";
import { EpicRestoreDocument } from "./EpicRestoreDocument";

export const EpicEditDocument = combineEpics(EpicRestoreDocument, EpicDeleteDocument);
