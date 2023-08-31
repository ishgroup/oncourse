import { combineEpics } from "redux-observable";
import { EpicCreateIntegration } from "./EpicCreateIntegration";
import { EpicDeleteIntegration } from "./EpicDeleteIntegration";
import { EpicGetIntegrations } from "./EpicGetIntegrations";
import { EpicUpdateIntegration } from "./EpicUpdateIntegration";

export const EpicIntegrations = combineEpics(
  EpicGetIntegrations,
  EpicUpdateIntegration,
  EpicDeleteIntegration,
  EpicCreateIntegration
);
