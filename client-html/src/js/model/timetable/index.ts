/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Session } from '@api/model';
import { AnyArgFunction, DateArgFunction, StringArgFunction } from 'ish-ui';
import { CoreFilter, SavingFilterState } from '../common/ListView';

export type CalendarMode = "Compact" | "Gap(Days)" | "Gap(Hours)";

export type CalendarTagsState = "Tag names" | "Tag dots" | "Tag off";

export type CalendarGroupingState = "Group by tutor" | "Group by room" | "No grouping";

export interface CalendarGrouping {
  tutor?: string;
  room?: string;
  site?: string;
  sessions: Session[];
}

export interface TimetableState {
  months: TimetableMonth[];
  selectedMonthSessionDays: number[];
  sessionsLoading: boolean;
  search: string;
  searchError?: boolean;
  scrollToDay?: string;
  filters: CoreFilter[];
  filtersLoading: boolean;
  savingFilter: SavingFilterState;
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

export interface TimetableProps {
  calendarMode: CalendarMode;
  calendarGrouping: CalendarGroupingState;
  tagsState: CalendarTagsState;
  targetDay: string;
  selectedMonth: Date;
  selectedWeekDays: boolean[];
  selectedDayPeriods: boolean[];
  scrolledDayNode?: Element;
}

export interface TimetableContextState extends TimetableProps {
  setSelectedWeekDays?: (arg: boolean[]) => void;
  setSelectedDayPeriods?: (arg: boolean[]) => void;
  setCalendarMode?: AnyArgFunction<CalendarMode>;
  setCalendarGrouping?: AnyArgFunction<CalendarMode>;
  setTagsState?: AnyArgFunction<CalendarGroupingState>;
  setPrevious?: AnyArgFunction;
  setNext?: AnyArgFunction;
  setTargetDay?: StringArgFunction;
  setSelectedMonth?: DateArgFunction;
}

export interface TimetableAction {
  type: keyof TimetableProps;
  payload: any;
}