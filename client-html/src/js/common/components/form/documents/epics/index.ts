import { combineEpics } from "redux-observable";
import { EpicCreateAvatarDocument } from "./EpicCreateAvatarDocument";
import { EpicCreateNewDocument } from "./EpicCreateNewDocument";
import { EpicGetDocumentByID } from "./EpicGetDocumentByID";
import { EpicSearchExistingDocumentByHash } from "./EpicSearchExistingDocumentByHash";
import { EpicSearchExistingDocumentByName } from "./EpicSearchExistingDocumentByName";

export const EpicDocuments = combineEpics(
  EpicSearchExistingDocumentByHash,
  EpicSearchExistingDocumentByName,
  EpicCreateNewDocument,
  EpicCreateAvatarDocument,
  EpicGetDocumentByID
);
