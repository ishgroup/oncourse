/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Assessment, Enrolment } from "@api/model";

export interface EnrolmentAssessmentExtended extends Assessment{
  tutors: {
    contactId: number;
    tutorName: string;
  }[];
}

export interface EnrolmentExtended extends Enrolment {
  assessments?: EnrolmentAssessmentExtended[];
}
