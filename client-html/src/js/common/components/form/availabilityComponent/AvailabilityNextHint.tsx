/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Typography from "@mui/material/Typography";
import { format as formatDate } from "date-fns";
import { III_DD_MMM_YYYY } from "ish-ui";
import { Holiday, RepeatEndEnum, RepeatEnum } from "@api/model";
import { appendTimezone } from "ish-ui";

function encreaseDate(date, step) {
  switch (step) {
    case "hour":
      return date.setHours(date.getHours() + 1);
    case "day":
      return date.setDate(date.getDate() + 1);
    case "week":
      return date.setDate(date.getDate() + 7);
    case "month":
      return date.setMonth(date.getMonth() + 1);
    case "year":
      return date.setFullYear(date.getFullYear() + 1);
    default:
      return date;
  }
}

function getDatesFromRepeatEndDate(dates, endDate, allDay) {
  const todayDate = new Date();

  if (allDay) {
    todayDate.setHours(0, 0, 0, 0);
  }

  if (todayDate > endDate) {
    return [];
  }

  const afterTodayIndex = dates.findIndex(item => item > todayDate);

  return dates.slice(afterTodayIndex, afterTodayIndex + 3);
}

function getDatesFromDate(eventStartDate, repeatTimes, repeat) {
  const clone = new Date(eventStartDate.valueOf());
  for (let i = 1; i < repeatTimes + 1; i++) {
    encreaseDate(clone, repeat);
  }

  return clone;
}

function computeDateStrings(start, end, repeat, repeatEndObj, noRepeat, allDay, timezone) {
  const today = new Date();

  if (allDay) {
    today.setHours(0, 0, 0, 0);
  }

  const eventStartDate = start && new Date(start);

  eventStartDate.setHours(0, 0, 0, 0);

  const repeatTimes = repeatEndObj.endOn === "never" ? 3 : +repeatEndObj.repeatTimes <= 3 ? +repeatEndObj.repeatTimes : 3;

  const dates = [];

  const encreasedToday = new Date(today.valueOf());

  for (let i = 0; i < repeatTimes; i++) {
    encreaseDate(encreasedToday, repeat);
  }

  const repeatEndDateSpecified = Boolean(repeatEndObj.endOn === "onDate" && repeatEndObj.repeatOn);

  const repeatThreshold = repeatEndDateSpecified
    ? new Date(repeatEndObj.repeatOn)
    : eventStartDate > today
      ? getDatesFromDate(eventStartDate, repeatTimes, repeat)
      : encreasedToday;

  let counter = 0;

  while (!noRepeat && eventStartDate <= repeatThreshold) {
    dates.push(new Date(eventStartDate.valueOf()));
    encreaseDate(eventStartDate, repeat);
    if (repeatEndObj.endOn === "after" && counter >= +repeatEndObj.repeatTimes) {
      break;
    }
    counter++;
  }

  const nextDates = repeatEndDateSpecified
    ? getDatesFromRepeatEndDate(dates, new Date(repeatEndObj.repeatOn), allDay)
    : eventStartDate > today
      ? getDatesFromRepeatEndDate(dates, getDatesFromDate(eventStartDate, +repeatEndObj.repeatTimes, repeat), allDay)
      : dates.slice(dates.length - repeatTimes);

  if (nextDates[nextDates.length - 1] <= today || !nextDates.length) {
    return [formatDate(timezone ? appendTimezone(new Date(start), timezone) : new Date(start), III_DD_MMM_YYYY)];
  }

  return nextDates
    .filter(item => item > today)
    .map(item => formatDate(timezone ? appendTimezone(item, timezone) : item, III_DD_MMM_YYYY));
}

function computeRepeatDates(item: Holiday, allDay, timezone) {
  const noRepeat = RepeatEnum[item.repeat] === "none";

  const repeatStep = RepeatEnum[item.repeat];
  const repeatEndObj = {
    endOn: RepeatEndEnum[item.repeatEnd],
    repeatTimes: item.repeatEndAfter,
    repeatOn: item.repeatOn
  };
  const dateStrings = computeDateStrings(
    item.startDate,
    item.endDate,
    repeatStep,
    repeatEndObj,
    noRepeat,
    allDay,
    timezone
  );

  return dateStrings.map((date, index) => (
    <Typography variant="body2" key={index + date}>
      {date}
    </Typography>
  ));
}

interface HintProps {
  className: string;
  item: Holiday;
  timezone?: string;
}

const AvailabilityNextHint = (props: HintProps) => {
  const {
    startDate, startDateTime, endDate, endDateTime, ...rest
  } = props.item;

  const computed: Holiday = {...rest};

  const allDay = Boolean(startDate && endDate);

  computed.startDate = startDate || startDateTime;
  computed.endDate = startDate || startDateTime;

  const dates = computed.startDate && computed.endDate && computeRepeatDates(computed, allDay, props.timezone);

  return (
    <div className={props.className}>
      <Typography variant="caption">Next</Typography>
      {dates}
    </div>
  );
};

export default AvailabilityNextHint;
