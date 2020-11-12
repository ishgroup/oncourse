import { combineEpics } from "redux-observable";
import { EpicCreateDocument } from "./EpicCreateDocument";
import { EpicDeleteDocument } from "./EpicDeleteDocument";
import { EpicGetDocument } from "./EpicGetDocument";
import { EpicUpdateDocumentItem } from "./EpicUpdateDocument";

export const EpicEditDocument = combineEpics(EpicGetDocument, EpicDeleteDocument, EpicCreateDocument, EpicUpdateDocumentItem);
