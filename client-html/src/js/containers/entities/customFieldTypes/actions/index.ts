/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { EntityName } from "../../../../model/entities/common";

export const GET_CUSTOM_FIELD_TYPES = _toRequestType("get/customFieldTypes");
export const GET_CUSTOM_FIELD_TYPES_FULFILLED = FULFILLED(GET_CUSTOM_FIELD_TYPES);

export const getCustomFieldTypes = (entity: EntityName) => ({
  type: GET_CUSTOM_FIELD_TYPES,
  payload: entity
});
