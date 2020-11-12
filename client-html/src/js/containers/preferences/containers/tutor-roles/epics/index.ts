import { combineEpics } from "redux-observable";
import { EpicGetTutorRoles } from "./EpicGetTutorRoles";
import { EpicGetTutorRole } from "./EpicGetTutorRole";
import { EpicUpdateTutorRole } from "./EpicUpdateTutorRole";
import { EpicCreateTutorRole } from "./EpicCreateTutorRole";
import { EpicDeleteTutorRole } from "./EpicDeleteTutorRole";

export const EpicTutorRoles = combineEpics(
  EpicGetTutorRoles,
  EpicGetTutorRole,
  EpicUpdateTutorRole,
  EpicCreateTutorRole,
  EpicDeleteTutorRole
);
