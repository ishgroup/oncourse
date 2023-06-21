/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

// CRUD actions
import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { ListActionEntity } from "../../../../model/entities/common";
import { getContact } from "../../contacts/actions";
import { createCourseClass, getCourseClass } from "../../courseClasses/actions";
import { getSite } from "../../sites/actions";
import { updateVetReportEntities } from "../../vetReporting/actions";

export const GET_ENTITY_RECORD_REQUEST = _toRequestType("get/entity/record");

export const CREATE_ENTITY_RECORD_REQUEST = _toRequestType("create/entity/record");

export const UPDATE_ENTITY_RECORD_REQUEST = _toRequestType("update/entity/record");

export const DELETE_ENTITY_RECORD_REQUEST = _toRequestType("delete/entity/record");

export const getEntityRecord = (id, entity: ListActionEntity) => {
  switch (entity) {
    case "Contact":
    case "VetReport":
      return getContact(id);
    case "CourseClass":
      return getCourseClass(id);
    case "Site":
      return getSite(id);
    default: 
      return {
        type: GET_ENTITY_RECORD_REQUEST,
        payload: { id, entity }
      };
  }
};

export const createEntityRecord = (item: any, entity: ListActionEntity) => {
  switch (entity) {
    case "CourseClass":
      return createCourseClass(item);
    default:
      return {
        type: CREATE_ENTITY_RECORD_REQUEST,
        payload: { item, entity }
      };
  }
};

export const updateEntityRecord = (id: number, entity: ListActionEntity, item: any) => {
  switch (entity) {
    case "VetReport":
      return updateVetReportEntities(entity, item);
    default:
      return {
        type: UPDATE_ENTITY_RECORD_REQUEST,
        payload: { id, entity, item }
      };
  }
};

export const deleteEntityRecord = (id: number, entity: ListActionEntity) => ({
  type: DELETE_ENTITY_RECORD_REQUEST,
  payload: { id, entity }
});