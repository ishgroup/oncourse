/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { FieldProcessingAction } from "../../model/common/FieldProcessing";

export const START_FIELD_PROCESSING_ACTION = "add/field/processing/action";
export const END_FIELD_PROCESSING_ACTION = "remove/field/processing/action";

export const startFieldProcessingAction = (payload: FieldProcessingAction) => ({
  type: START_FIELD_PROCESSING_ACTION,
  payload
});

export const endFieldProcessingAction = (id: string) => ({
  type: END_FIELD_PROCESSING_ACTION,
  payload: id
});