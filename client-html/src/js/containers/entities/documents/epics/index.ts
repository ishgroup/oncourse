import { combineEpics } from "redux-observable";
import { EpicCreateDocument } from "./EpicCreateDocument";
import { EpicDeleteDocument } from "./EpicDeleteDocument";
import { EpicGetDocument } from "./EpicGetDocument";
import { EpicUpdateDocumentItem } from "./EpicUpdateDocument";
import { EpicRestoreDocument } from "./EpicRestoreDocument";

export const EpicEditDocument = combineEpics(EpicGetDocument, EpicRestoreDocument, EpicDeleteDocument, EpicCreateDocument, EpicUpdateDocumentItem);
