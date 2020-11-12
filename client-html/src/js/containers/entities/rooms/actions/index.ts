/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Room } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { SelectItemDefault } from "../../../../model/entities/common";

export const GET_ROOM_ITEM = _toRequestType("get/room");
export const GET_ROOM_ITEM_FULFILLED = FULFILLED(GET_ROOM_ITEM);

export const GET_PLAIN_ROOMS = _toRequestType("get/plain/rooms");

export const SET_PLAIN_ROOMS = "set/plain/rooms";

export const SET_PLAIN_ROOMS_SEARCH = "set/plain/rooms/search";

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

export const getPlainRooms = (
  offset?: number,
  columns?: string,
  ascending?: boolean,
  sort?: string,
  search?: string
) => ({
  type: GET_PLAIN_ROOMS,
  payload: {
    offset,
    columns,
    ascending,
    sort,
    search
  }
});

export const setPlainRooms = (items: SelectItemDefault[], offset?: number, pageSize?: number) => ({
  type: SET_PLAIN_ROOMS,
  payload: { items, offset, pageSize }
});

export const setPlainRoomsSearch = (search: string) => ({
  type: SET_PLAIN_ROOMS_SEARCH,
  payload: { search }
});
