/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Site } from "@api/model";
import { PlainEntityState } from "../../../../model/common/Plain";
import { SelectItemDefault } from "../../../../model/entities/common";

export interface SiteState extends PlainEntityState<Site> {
  adminSites?: SelectItemDefault[];
  virualSites?: SelectItemDefault[];
}
