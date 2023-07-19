/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import { utcToZonedTime, zonedTimeToUtc } from "date-fns-tz";

export const appendTimezone = (date: Date, timezone: string) => {
  let result = date;
  try {
    result = utcToZonedTime(date, timezone);
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn(e);
    return date;
  }
  return result;
};

export const prependTimezone = (date: Date, timezone: string) => {
  let result = date;

  try {
    result = zonedTimeToUtc(date, timezone);
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn(e);
    return date;
  }
  return result;
};
