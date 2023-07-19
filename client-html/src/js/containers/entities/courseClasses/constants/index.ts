/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassFundingSource } from "@api/model";
import { mapSelectItems } from "../../../../common/utils/common";

export const COURSE_CLASS_COST_DIALOG_FORM = "CourseClassCostDialogForm";

export const fundingSourceValues = Object.keys(ClassFundingSource).map(mapSelectItems);
