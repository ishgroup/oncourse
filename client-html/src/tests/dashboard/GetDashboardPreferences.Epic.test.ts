/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DASHBOARD_CATEGORY_WIDTH_KEY } from "../../js/constants/Config";
import { GetUserPreferences } from "../common/GetUserPreferences.Epic";

describe("Get dashboard preferences epic tests", () => {
  it("should return correct actions", () =>
    GetUserPreferences([DASHBOARD_CATEGORY_WIDTH_KEY]));
});
