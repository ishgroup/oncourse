
/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export type GAEventTypes = "timing" | "screenview";

export const initGAEvent = (event: GAEventTypes, screen: string, time?: number) => {
  if (!window["dataLayer"]) {
    return;
  }

  switch (event) {
    case "screenview": {
      window["dataLayer"].push({
        event: "screenview",
        screenName: screen
      });
      break;
    }
    case "timing": {
      window["dataLayer"].push({
        event: "timing",
        timingCategory: screen,
        timingValue: time
      });
      break;
    }
  }
};
