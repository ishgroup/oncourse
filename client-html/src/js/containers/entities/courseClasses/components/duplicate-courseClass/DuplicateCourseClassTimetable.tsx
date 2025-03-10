/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import CircularProgress from '@mui/material/CircularProgress';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React from 'react';
import { TimetableMonth } from '../../../../../model/timetable';
import CalendarDayBase from '../../../../timetable/components/calendar/components/day/CalendarDayBase';
import CalendarMonthBase from '../../../../timetable/components/calendar/components/month/CalendarMonthBase';
import CalendarSession from '../../../../timetable/components/calendar/components/session/CalendarSession';

interface Props {
  months: TimetableMonth[];
  fetching: boolean;
}

const DuplicateCourseClassTimetable: React.FunctionComponent<Props> = ({ months, fetching }) => {
  const showMessage = !fetching && !months.length;

  return (
    <div className={showMessage ? "h-100 d-flex" : undefined}>
      {fetching && <CircularProgress size={40} thickness={5} />}
      {!fetching
        && months.map((m, mI) => (
          <CalendarMonthBase fullWidth key={mI} {...m}>
            {m.days.map(d => {
              if (!d.sessions.length) {
                return null;
              }
              return (
                <CalendarDayBase day={d.day} key={d.day.toString()}>
                  {d.sessions.map(s => (
                    <CalendarSession
                      key={s.id}
                      {...s}
                      tagsState="Tag names"
                    />
                  ))}
                </CalendarDayBase>
              );
            })}
          </CalendarMonthBase>
        ))}

      {showMessage && (
        <div className="noRecordsMessage">
          <Typography variant="h6" color="inherit" align="center">
            {$t('no_sessions_were_found')}
          </Typography>
        </div>
      )}
    </div>
  );
};

export default DuplicateCourseClassTimetable;
