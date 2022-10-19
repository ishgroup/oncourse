/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact } from "@api/model";
import { openInternalLink } from "../../../../common/utils/links";
import { CourseClassStatus } from "../../../../model/entities/CourseClass";
import { EntityType } from "../../../../model/common/NestedEntity";

const getContactName = item => {
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

  return getContactName(data);
};

export const openContactLink = (contactId: number) => {
  openInternalLink("/contact/" + contactId);
};

export const convertSelectBooleanToString = v => (typeof v === "boolean" ? String(v) : "");
export const convertSelectStringToBoolean = v => (v === "true" ? true : v === "false" ? false : "");

export const getNestedTutorClassItem = (status: CourseClassStatus, count: number, id: number): EntityType => {
  switch (status) {
    case "Current":
      return {
        name: "Current",
        count,
        link: `/class?search=tutorRoles.tutor.id is ${id}&filter=@Current_classes`,
        timetableLink: `/timetable?search=courseClass.tutorRoles.tutor.id=${id} and courseClass.startDateTime < tomorrow and courseClass.endDateTime >= today and courseClass.isCancelled is false`
      };
    case "Future":
      return {
        name: "Future",
        count,
        link: `/class?search=tutorRoles.tutor.id is ${id}&filter=@Future_classes`,
        timetableLink: `/timetable?search=courseClass.tutorRoles.tutor.id=${id} and courseClass.startDateTime >= tomorrow and courseClass.endDateTime >= tomorrow and courseClass.isCancelled is false`
      };
    case "Self Paced":
      return {
        name: "Self Paced",
        count,
        link: `/class?search=tutorRoles.tutor.id is ${id}&filter=@Self_paced_classes`
      };
    case "Unscheduled":
      return {
        name: "Unscheduled",
        count,
        link: `/class?search=tutorRoles.tutor.id is ${id}&filter=@Unscheduled_classes`
      };
    case "Finished":
      return {
        name: "Finished",
        count,
        link: `/class?search=tutorRoles.tutor.id is ${id}&filter=@Finished_classes`,
        timetableLink: `/timetable?search=courseClass.tutorRoles.tutor.id=${id} and courseClass.isCancelled is false and courseClass.endDateTime before today`
      };
    case "Cancelled":
      return {
        name: "Cancelled",
        count,
        link: `/class?search=tutorRoles.tutor.id is ${id}&filter=@Cancelled_classes`,
        grayOut: true
      };
    default: {
      console.error(`Unknown Course Class status ${status} !`);
      return null;
    }
  }
};
