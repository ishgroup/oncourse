import { combineEpics } from "redux-observable";
import { EpicSearchExistingDocumentByHash } from "./EpicSearchExistingDocumentByHash";
import { EpicSearchExistingDocumentByName } from "./EpicSearchExistingDocumentByName";
import { EpicCreateNewDocument } from "./EpicCreateNewDocument";
import { EpicGetDocumentByID } from "./EpicGetDocumentByID";
import { EpicCreateAvatarDocument } from "./EpicCreateAvatarDocument";
import { EpicGetEntityDocuments } from "./EpicGetEntityDocuments";
import { EpicUpdateEntityDocuments } from "./EpicUpdateEntityDocuments";

export const EpicDocuments = combineEpics(
  EpicSearchExistingDocumentByHash,
  EpicSearchExistingDocumentByName,
  EpicCreateNewDocument,
  EpicCreateAvatarDocument,
  EpicGetDocumentByID,
  EpicGetEntityDocuments,
  EpicUpdateEntityDocuments
);
