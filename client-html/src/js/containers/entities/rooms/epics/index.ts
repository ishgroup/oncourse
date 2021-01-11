/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetRoom } from "./EpicGetRoom";
import { EpicUpdateRoom } from "./EpicUpdateRoom";
import { EpicCreateRoom } from "./EpicCreateRoom";
import { EpicDeleteRoom } from "./EpicDeleteRoom";
import { EpicValidateDeleteRoom } from "./EpicValidateDeleteRoom";

export const EpicRoom = combineEpics(
  EpicGetRoom,
  EpicUpdateRoom,
  EpicCreateRoom,
  EpicDeleteRoom,
  EpicValidateDeleteRoom
);
