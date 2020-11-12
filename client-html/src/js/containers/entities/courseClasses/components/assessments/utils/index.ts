/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssessmentClass } from "@api/model";
import CourseClassAssessmentService from "../services/CourseClassAssessmentService";

export const validateAssesmentCreate = (assessment: AssessmentClass) =>
  CourseClassAssessmentService.validatePost(assessment);
export const validateAssesmentUpdate = (assessment: AssessmentClass) =>
  CourseClassAssessmentService.validatePut(assessment);
