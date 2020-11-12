/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { openInternalLink } from "../../../../common/utils/links";

export const openSiteLink = (siteId: number) => {
  openInternalLink("/site/" + siteId);
};

export const getAdminCenterLabel = site => site.label;
