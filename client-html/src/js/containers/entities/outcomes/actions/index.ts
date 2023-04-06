/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../../common/actions/ActionUtils";

export const GET_OUTCOME_TAGS = _toRequestType("get/outcome/tags");

export const getOutcomeTags = () => ({
  type: GET_OUTCOME_TAGS,
});