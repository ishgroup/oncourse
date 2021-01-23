/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Room } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_ROOM_ITEM = _toRequestType("get/room");
export const GET_ROOM_ITEM_FULFILLED = FULFILLED(GET_ROOM_ITEM);

export const DELETE_ROOM_ITEM = _toRequestType("delete/room");
export const DELETE_ROOM_ITEM_FULFILLED = FULFILLED(DELETE_ROOM_ITEM);

export const UPDATE_ROOM_ITEM = _toRequestType("put/room");
export const UPDATE_ROOM_ITEM_FULFILLED = FULFILLED(UPDATE_ROOM_ITEM);

export const CREATE_ROOM_ITEM = _toRequestType("post/room");
export const CREATE_ROOM_ITEM_FULFILLED = FULFILLED(CREATE_ROOM_ITEM);

export const GET_ROOM_DELETE_VALIDATION = _toRequestType("get/list/entity/room/validation");
export const GET_ROOM_DELETE_VALIDATION_FULFILLED = FULFILLED(GET_ROOM_DELETE_VALIDATION);

export const validateDeleteRoom = (id: string, callback: any) => ({
  type: GET_ROOM_DELETE_VALIDATION,
  payload: { id, callback }
});

export const getRoom = (id: string) => ({
  type: GET_ROOM_ITEM,
  payload: id
});

export const removeRoom = (id: string) => ({
  type: DELETE_ROOM_ITEM,
  payload: id
});

export const updateRoom = (id: string, room: Room) => ({
  type: UPDATE_ROOM_ITEM,
  payload: { id, room }
});

export const createRoom = (room: Room) => ({
  type: CREATE_ROOM_ITEM,
  payload: { room }
});

