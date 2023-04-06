/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { parse, addWeeks, addDays, subDays, addMonths, addYears, setHours, setMinutes } from "date-fns";
import { DD_MMM_YYYY, DD_MMM_YYYY_HH_MM, HH_MM_COLONED } from "./format";

const transitionDate = new Date(2000, 0, 1);

const formatsWithLetters = [
  "dMMMyy",
  "dMMMMyy",
  "ddMMMyy",
  "ddMMMMyy",
  "dMMMyyyy",
  "dMMMMyyyy",
  "ddMMMMyyyy",
  "ddMMMyyyy"
];

const specialOptions = ["tm", "yd", "tomorrow", "yesterday"];

const operatorsRegexp = /\s*[-+]\s*[0-9][0-9]?[dDmMyYwW]?/g;

const dividersRegexp = /[.\/\-]/g;

const timeRegexp = /(\s|^)[0-9][0-9]?:[0-9][0-9]?/;

const manageDatePart = (date: Date, part: string) => {
  const numberPart = parseInt(part, 10);
  switch (true) {
    case /[mM]/.test(part): {
      return addMonths(date, numberPart);
    }
    case /[yY]/.test(part): {
      return addYears(date, numberPart);
    }
    case /[wW]/.test(part): {
      return addWeeks(date, numberPart);
    }
    case /[dD]/.test(part):
    default: {
      return addDays(date, numberPart);
    }
  }
};

const checkInvalidDate = (date: Date) => date.toString() !== "Invalid Date";

export const formatStringDate = (dateString: string, type: string, baseDate: Date, customFormat?: string) => {
  let result;
  let parsed;
  let formatted = dateString;

  if (type === "date") {
    baseDate.setHours(0, 0, 0, 0);
  }

  if (customFormat) {
    parsed = parse(dateString.replace(/^\w{3}/, "").trim(), customFormat, baseDate);
    if (checkInvalidDate(parsed)) {
      return parsed;
    }
    parsed = null;
  }

  if (type === "date") {
    parsed = parse(dateString.replace(/^\w{3}/, "").trim(), DD_MMM_YYYY, baseDate);
    if (checkInvalidDate(parsed)) {
      return parsed;
    }
    parsed = null;
  }

  if (type === "datetime") {
    parsed = parse(dateString.replace(/^\w{3}/, "").trim(), DD_MMM_YYYY_HH_MM, baseDate);
    if (checkInvalidDate(parsed)) {
      return parsed;
    }
    parsed = null;
  }

  if (type === "time") {
    parsed = parse(dateString, HH_MM_COLONED, baseDate);
    if (checkInvalidDate(parsed)) {
      return parsed;
    }
    parsed = parse(dateString, "HH", baseDate);
    if (checkInvalidDate(parsed)) {
      return parsed;
    }
    parsed = null;
  }

  const dividers = dateString.match(dividersRegexp);

  const operators = dateString.match(operatorsRegexp);

  const time = dateString.match(timeRegexp);

  if (time) {
    formatted = formatted.replace(timeRegexp, "");
  }

  if (operators) {
    formatted = formatted.replace(operatorsRegexp, "");
  }

  const hasLetters = formatted.match(/[a-zA-Z]/);

  if (hasLetters) {
    for (const format of formatsWithLetters) {
      parsed = parse(formatted.replace(/\s/g, ""), format, baseDate);
      if (checkInvalidDate(parsed)) {
        result = parsed;
        break;
      }
    }
  }

  if (dividers && !hasLetters) {
    const dateParts = formatted.trim().split(dividersRegexp);
    formatted = formatted.replace(dividersRegexp, " ");

    let formatString = `${dateParts[0] ? dateParts[0].replace(/./g, "d") : ""} ${
      dateParts[1] ? dateParts[1].replace(/./g, "M") : ""
    } ${dateParts[2] ? dateParts[2].replace(/./g, "y") : ""}`;

    if (dateParts[2] && dateParts[2].length === 1) {
      formatted = formatted.replace(new RegExp(dateParts[2] + "$"), `0${dateParts[2]}`);
      formatString = formatString.replace(new RegExp("y$"), "yy");
    }

    parsed = parse(formatted, formatString.trim(), baseDate);
    if (checkInvalidDate(parsed)) {
      result = parsed;
    }
  }

  if (!dividers && !hasLetters) {
    switch (formatted.length) {
      case 1:
      case 2: {
        parsed = parse(formatted, "dd", baseDate);
        if (checkInvalidDate(parsed)) {
          result = parsed;
        }
        break;
      }
      case 3: {
        parsed = parse(formatted, "ddM", baseDate);
        if (checkInvalidDate(parsed)) {
          result = parsed;
        }
        break;
      }
      case 4: {
        parsed = parse(`${formatted[0]} ${formatted[1]} ${formatted[2] + formatted[3]}`, "d M yy", transitionDate);
        if (checkInvalidDate(parsed)) {
          result = parsed;
        }
        break;
      }
      case 5: {
        parsed = parse(formatted, "ddMyy", transitionDate);
        if (checkInvalidDate(parsed)) {
          result = parsed;
        }
        break;
      }
      case 6: {
        parsed = parse(formatted, "ddMMyy", transitionDate);
        if (checkInvalidDate(parsed)) {
          result = parsed;
          break;
        }

        break;
      }
      case 8: {
        parsed = parse(formatted, "ddMMyyyy", transitionDate);
        if (checkInvalidDate(parsed)) {
          result = parsed;
        }
      }
    }
  }

  if (!(result instanceof Date)) {
    result = baseDate;
    for (const option of specialOptions) {
      if (formatted === option) {
        switch (option) {
          case "tomorrow":
          case "tm": {
            result = addDays(result, 1);
            break;
          }
          case "yesterday":
          case "yd": {
            result = subDays(result, 1);
          }
        }
        break;
      }
    }
  }

  if (operators) {
    operators.forEach(op => {
      result = manageDatePart(result, op.trim());
    });
  }

  if (time && (type === "time" || type === "datetime")) {
    parsed = parse(time[0].trim(), "HH:mm", baseDate);
    if (checkInvalidDate(parsed)) {
      result = setHours(result, parsed.getHours());
      result = setMinutes(result, parsed.getMinutes());
    }
  }

  return result;
};

export const formatDurationMinutes = (minutes: number): string => {
  const duration = (minutes / 60).toString().split(".");

  return `${duration[0] === "0" ? "" : duration[0] + "h"} ${
    duration[1] ? (parseFloat("0." + duration[1]) * 60).toFixed() + "min" : ""
  }`.trim();
};
