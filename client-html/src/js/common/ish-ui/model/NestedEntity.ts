/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export interface EntityType {
  name: string;
  count: number;
  disabled?: boolean;
  link?: string;
  timetableLink?: string;
  linkHandler?: any;
  grayOut?: boolean;
}
