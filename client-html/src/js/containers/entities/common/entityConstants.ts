/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

export const RELATION_COURSE_COLUMNS_DEFAULT = "code,name,currentlyOffered,isShownOnWeb";

export const REMOTE_CLASS_COLUMNS_DEFAULT = "course.name,course.code,code,feeIncGst";

export const REMOTE_CONTACT_COLUMNS_DEFAULT = "firstName,lastName,middleName,email,birthDate,street,suburb,state,postcode,invoiceTerms,taxOverride.id,fullName,id";

export const REMOTE_SITE_COLUMNS_DEFAULT = "name,localTimezone";

export const REMOTE_ROOM_COLUMNS_DEFAULT = "name,site.name,site.localTimezone,site.id";

export const REMOTE_QUALIFICATION_COLUMNS_DEFAULT = "nationalCode,title,level,fieldOfEducation,isOffered";

export const REMOTE_CODE_COLUMNS_DEFAULT = "code,name,currentlyOffered,isShownOnWeb";

export const REMOTE_ASSESSMENT_COLUMNS_DEFAULT = "code,name";

export const REMOTE_MODULE_COLUMNS_DEFAULT = "nationalCode,title";

export const REMOTE_LEAD_COLUMNS_DEFAULT = "id,items.course.code,estimatedValue," + REMOTE_CONTACT_COLUMNS_DEFAULT.split(',').map(c => `customer.${c}`).join(',');

export const getDefaultRemoteColumns = entity => {
  switch (entity) {
    case "CourseClass":
      return REMOTE_CLASS_COLUMNS_DEFAULT;
    case "Contact":
      return REMOTE_CONTACT_COLUMNS_DEFAULT;
    case "Site":
      return REMOTE_SITE_COLUMNS_DEFAULT;
    case "Room":
      return REMOTE_ROOM_COLUMNS_DEFAULT;
    case "Qualification":
      return REMOTE_QUALIFICATION_COLUMNS_DEFAULT;
    case "Course":
      return REMOTE_CODE_COLUMNS_DEFAULT;
    case "Assessment":
      return REMOTE_ASSESSMENT_COLUMNS_DEFAULT;
    case "Module":
      return REMOTE_MODULE_COLUMNS_DEFAULT;
    case "Lead":
      return REMOTE_LEAD_COLUMNS_DEFAULT;
  }
  return "";
};