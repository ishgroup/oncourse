/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course } from "@api/model";
import { openInternalLink } from "../../../../common/utils/links";

export const courseFilterCondition = (value: Course) => `${value.name} ${value.code}`;

export const openCourseLink = (courseId: number) => {
  openInternalLink("/course/" + courseId);
};

