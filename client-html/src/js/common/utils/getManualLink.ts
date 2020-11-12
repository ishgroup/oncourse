/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ManualType } from "../../model/common/Manual";

export const getManualLink = (docFor: ManualType, subtype?: string): string => {
  let hash: string;

  if (docFor === "externalintegrations") {
    hash = `externalintegrations-${subtype}`;
  } else {
    hash = docFor.replace('_', '-');
  }

  const search = new URLSearchParams("");
  search.append("utm_source", "help");
  search.append("utm_medium", "app");
  search.append("utm_campaign", "manual_links");
  search.append("host", window.location.host);

  return "https://www.ish.com.au/onCourse/doc/manual/" + `?${search.toString()}#${hash}`;
};
