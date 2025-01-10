import { combineEpics } from "redux-observable";
import { EpicCreateTutorRole } from "./EpicCreateTutorRole";
import { EpicDeleteTutorRole } from "./EpicDeleteTutorRole";
import { EpicGetTutorRole } from "./EpicGetTutorRole";
import { EpicGetTutorRoles } from "./EpicGetTutorRoles";
import { EpicUpdateTutorRole } from "./EpicUpdateTutorRole";

export const EpicTutorRoles = combineEpics(
  EpicGetTutorRoles,
  EpicGetTutorRole,
  EpicUpdateTutorRole,
  EpicCreateTutorRole,
  EpicDeleteTutorRole
);
