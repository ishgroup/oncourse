/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Qualification } from "@api/model";
import { _toRequestType } from "../../../../common/actions/ActionUtils";

export const UPDATE_QUALIFICATION_ITEM = _toRequestType("put/qualification");

export const updateQualification = (id: string, qualification: Qualification) => ({
  type: UPDATE_QUALIFICATION_ITEM,
  payload: { id, qualification }
});