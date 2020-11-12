/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Session } from "@api/model";
import { AnyArgFunction, DateArgFunction, StringArgFunction } from "../common/CommonFunctions";
import { CoreFilter, SavingFilterState } from "../common/ListView";

export type CalendarMode = "Compact" | "Gap(Days)" | "Gap(Hours)";

export interface TimetableState {
  months?: TimetableMonth[];
  selectedMonthSessionDays?: string[];
  sessionsLoading?: boolean;
  search?: string;
  usersSearch?: string;
  searchError?: boolean;
  filters: CoreFilter[];
  savingFilter?: SavingFilterState;
}

export interface TimetableMonth {
  month: Date;
  days: TimetableDay[];
  hasSessions: boolean;
}

export interface TimetableSession extends Session {
  tags?: { [key: string]: string };
  index?: number;
}

export interface TimetableDay {
  day: Date;
  updated: boolean;
  tagsUpdated: boolean;
  sessions: TimetableSession[];
  timezone?: string;
}

export interface TimetableContextState {
  calendarMode: CalendarMode;
  targetDay: Date;
  selectedMonth: Date;
  selectedWeekDays: boolean[];
  selectedDayPeriods: boolean[];
  setSelectedWeekDays?: (arg: boolean[]) => void;
  setSelectedDayPeriods?: (arg: boolean[]) => void;
  setCalendarMode?: StringArgFunction;
  setPrevious?: AnyArgFunction;
  setNext?: AnyArgFunction;
  setTargetDay?: DateArgFunction;
  setSelectedMonth?: DateArgFunction;
}
