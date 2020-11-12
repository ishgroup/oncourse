import { combineEpics } from "redux-observable";
import { EpicSearchExistingDocumentByHash } from "./EpicSearchExistingDocumentByHash";
import { EpicSearchExistingDocumentByName } from "./EpicSearchExistingDocumentByName";
import { EpicCreateNewDocument } from "./EpicCreateNewDocument";
import { EpicGetDocumentByID } from "./EpicGetDocumentByID";
import { EpicCreateAvatarDocument } from "./EpicCreateAvatarDocument";

export const EpicDocuments = combineEpics(
  EpicSearchExistingDocumentByHash,
  EpicSearchExistingDocumentByName,
  EpicCreateNewDocument,
  EpicCreateAvatarDocument,
  EpicGetDocumentByID
);
