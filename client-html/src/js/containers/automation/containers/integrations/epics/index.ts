import { combineEpics } from "redux-observable";
import { EpicGetIntegrations } from "./EpicGetIntegrations";
import { EpicUpdateIntegration } from "./EpicUpdateIntegration";
import { EpicDeleteIntegration } from "./EpicDeleteIntegration";
import { EpicCreateIntegration } from "./EpicCreateIntegration";

export const EpicIntegrations = combineEpics(
  EpicGetIntegrations,
  EpicUpdateIntegration,
  EpicDeleteIntegration,
  EpicCreateIntegration
);
