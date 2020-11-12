/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact } from "@api/model";
import { openInternalLink } from "../../../../common/utils/links";
import { Classes, CourseClassStatus } from "../../../../model/entities/CourseClass";
import { EntityType } from "../../../../model/common/NestedEntity";

export const THEME_SPACING = 8;
export const WRAPPER_SPACING = THEME_SPACING * 9;
export const DEFAULT_TABLE_HEAD_HEIGHT = 52;
export const DEFAULT_TABLE_CELL_HEIGHT = 30;
export const DEFAULT_TITLE_HEIGHT = 33;
export const DEFAULT_TABLE_HEIGHT = 400;

export const contactLabelCondition = (data: Contact) => data && (data.firstName ? `${data.firstName} ${data.lastName}` : data.lastName);

export const getContactName = item => {
  const firstName = item.firstName || "";
  const lastName = item.lastName || "";
  return `${firstName.toLowerCase() === lastName.toLowerCase() ? "" : `${firstName} `}${lastName}`;
};

export const getContactFullName = (data: Contact) => {
  if (!data) return "";

  const { lastName, firstName, middleName } = data;

  if (!firstName) return lastName;

  if (middleName) {
    return `${firstName} ${middleName} ${lastName}`;
  }

  return `${firstName} ${lastName}`;
};

export const defaultContactName = (contactName: string) => {
  if (contactName) {
    const hasFirstName = contactName.split(", ");
    let name = contactName;

    if (hasFirstName.length > 0) {
      name = hasFirstName.reverse().join(" ");
    }
    return name;
  }
  return contactName;
};

export const openContactLink = (contactId: number) => {
  openInternalLink("/contact/" + contactId);
};

export const getTableWrapperHeight = (rowsCount: number) => Math.min(
    rowsCount === 0
      ? DEFAULT_TITLE_HEIGHT + WRAPPER_SPACING
      : rowsCount * DEFAULT_TABLE_CELL_HEIGHT + DEFAULT_TABLE_HEAD_HEIGHT + DEFAULT_TITLE_HEIGHT + WRAPPER_SPACING,
    DEFAULT_TABLE_HEIGHT
  );

export const convertSelectBooleanToString = v => (typeof v === "boolean" ? String(v) : "");
export const convertSelectStringToBoolean = v => (v === "true" ? true : v === "false" ? false : "");

export const getNestedTutorClassItem = (status: CourseClassStatus, count: number, id: number): EntityType => {
  switch (status) {
    case "Current":
      return {
        name: "Current",
        count,
        link: `/${Classes.path}?search=tutorRoles.tutor.id is ${id}&filter=@Current_classes`,
        timetableLink: `/timetable/search?query=courseClass.tutorRoles.tutor.id=${id} and courseClass.startDateTime < tomorrow and courseClass.endDateTime >= today and courseClass.isCancelled is false`
      };
    case "Future":
      return {
        name: "Future",
        count,
        link: `/${Classes.path}?search=tutorRoles.tutor.id is ${id}&filter=@Future_classes`,
        timetableLink: `/timetable/search?query=courseClass.tutorRoles.tutor.id=${id} and courseClass.startDateTime >= tomorrow and courseClass.endDateTime >= tomorrow and courseClass.isCancelled is false`
      };
    case "Self Paced":
      return {
        name: "Self Paced",
        count,
        link: `/${Classes.path}?search=tutorRoles.tutor.id is ${id}&filter=@Self_paced_classes`
      };
    case "Unscheduled":
      return {
        name: "Unscheduled",
        count,
        link: `/${Classes.path}?search=tutorRoles.tutor.id is ${id}&filter=@Unscheduled_classes`
      };
    case "Finished":
      return {
        name: "Finished",
        count,
        link: `/${Classes.path}?search=tutorRoles.tutor.id is ${id}&filter=@Finished_classes`,
        timetableLink: `/timetable/search?query=courseClass.tutorRoles.tutor.id=${id} and courseClass.isCancelled is false and courseClass.endDateTime before today`
      };
    case "Cancelled":
      return {
        name: "Cancelled",
        count,
        link: `/${Classes.path}?search=tutorRoles.tutor.id is ${id}&filter=@Cancelled_classes`,
        grayOut: true
      };
    default: {
      console.error(`Unknown Course Class status ${status} !`);
      return null;
    }
  }
};
