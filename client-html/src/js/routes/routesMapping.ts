/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Category } from "@api/model";
import { routes } from ".";
import { Classes } from "../model/entities/CourseClass";

export const getSystemRouteUrl = (title: Category) => {
  switch (title) {
    case "Change my password": {
      return "/login?updatePassword=true";
    }
    case "Community support": {
      return "http://forum.ish.com.au/";
    }
    case "Documentation": {
      return "http://www.ish.com.au/oncourse/support";
    }
    case "Release notes": {
      return "http://www.ish.com.au/s/onCourse/doc/release-notes";
    }
    case "Send support request...": {
      return "https://squish.ish.com.au/servicedesk/customer/portal/4";
    }
    case "onCourse news": {
      return "http://www.ish.com.au/blog";
    }
    case "Funding Contract": {
      return "/preferences/fundingContracts";
    }
    case "Deposit banking": {
      return "/banking/new";
    }
    case "Companies": {
      return "/contact?filter=@Companies";
    }
    case "Students": {
      return "/contact?filter=@Students";
    }
    case "Tutors": {
      return "/contact?filter=@Tutors";
    }
    case "Courses": {
      return "/course?filter=@Courses,@Enabled,@Enabled_and_visible_online";
    }
    case "Traineeship Courses": {
      return "/course?filter=@Traineeships,@Enabled,@Enabled_and_visible_online";
    }
    case "Traineeships": {
      return `/${Classes.path}?filter=@Traineeships,@Current_classes,@Future_classes,@Self_paced_classes`;
    }
    case "Classes": {
      return `/${Classes.path}?filter=@Classes,@Current_classes,@Future_classes,@Self_paced_classes`;
    }
    // case "Website setup": {
    //   return "/provisioning";
    // }

    default:
      throw `No URL found for category ${title}`;
  }
};

export const getMainRouteUrl = (title: Category) => {
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
