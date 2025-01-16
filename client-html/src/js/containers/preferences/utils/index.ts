/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Account, AccountType, SpecialTagType } from "@api/model";
import { sortDefaultSelectItems } from "ish-ui";
import UserPreferenceService from "../../../common/services/UserPreferenceService";
import { SPECIAL_TYPES_DISPLAY_KEY } from "../../../constants/Config";
import { EntityName } from "../../../model/entities/common";

export const getAccountsList = (accounts: Account[], type: AccountType) => {
  const list = accounts
    .filter(acc => type === acc.type)
    .map(acc => ({
      value: String(acc.id),
      label: `${acc.description} ${acc.accountCode}`
    }));

  list.sort(sortDefaultSelectItems);

  return list;
};

export const getEntityBySpecialTagType = (type: SpecialTagType): EntityName => {
  switch (type) {
    case "Class extended types":
      return 'CourseClass';
    case "Course extended types":
      return 'Course';
  }
};

export const getSpecialTagTypeByEntity = (entity: EntityName): SpecialTagType => {
  switch (entity) {
    case "CourseClass":
      return 'Class extended types';
    case "Course":
      return 'Course extended types';
    default:
      throw 'Entity not supported!';
  }
};

export const checkSpecialTypesAsync = async () => {
  const searchTypesPref = await UserPreferenceService.getUserPreferencesByKeys([SPECIAL_TYPES_DISPLAY_KEY]);
  return searchTypesPref[SPECIAL_TYPES_DISPLAY_KEY] === 'true';
};