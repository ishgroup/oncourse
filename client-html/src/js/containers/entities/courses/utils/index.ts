/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course } from "@api/model";
import { format, sub } from "date-fns";
import { openInternalLink } from "../../../../common/utils/links";

export const START_DAY_VALUE = 180;
export const END_DAY_VALUE = 60;

export const courseFilterCondition = (value: Course) => `${value.name} ${value.code}`;

export const openCourseLink = (courseId: number) => {
  openInternalLink("/course/" + courseId);
};

export const initDataForGraph = () => {
  const result = [];
  const currentDate = new Date();

  for (let index = -START_DAY_VALUE; index <= END_DAY_VALUE; index++) {
    result.push({
      availablePlaces: 0,
      classCreated: [],
      classStarted: [],
      dayDate: format(sub(currentDate, { days: -index }), "yyyy-MM-dd"),
      dayNumber: index === 0 ? "Today" : index,
    });
  }

  return result;
};
