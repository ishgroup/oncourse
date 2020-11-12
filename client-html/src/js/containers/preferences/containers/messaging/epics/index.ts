import { combineEpics } from "redux-observable";

import { EpicGetMessageQueued } from "./EpicGetMessageQueued";

export const EpicMessaging = combineEpics(EpicGetMessageQueued);
