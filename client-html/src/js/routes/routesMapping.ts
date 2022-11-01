/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { routes } from ".";
import { getCookie } from "../common/utils/Cookie";

export const getPrivisioningLink = (link: string) => {
  if (link === "https://provisioning.ish.com.au/") {
    return `https://provisioning.ish.com.au?token=${getCookie("JSESSIONID")}`;
  }
  return link;
};

export const getSystemRouteUrl = (title: string) => {
  switch (title) {
    case "Website setup": {
      return `https://provisioning.ish.com.au?token=${getCookie("JSESSIONID")}`;
    }
    default:
      throw `No URL found for category ${title}`;
  }
};

export const getMainRouteUrl = (title: string) => {
  let match;
  let response: string;

  if ( (title === "Courses") || (title === "Traineeship Courses") || (title === "Traineeships") || (title === "Classes")) {
    match = null;
  } else {
    match = routes.find(r => r.title === title);
  }

  if (match) {
    response = match.url;
  } else {
    response = getSystemRouteUrl(title);
  }

  return response;
};
