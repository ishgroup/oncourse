/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_ROOM_DELETE_VALIDATION = _toRequestType("get/list/entity/room/validation");
export const GET_ROOM_DELETE_VALIDATION_FULFILLED = FULFILLED(GET_ROOM_DELETE_VALIDATION);

export const validateDeleteRoom = (id: string, callback: any) => ({
  type: GET_ROOM_DELETE_VALIDATION,
  payload: { id, callback }
});