import React, { useState } from "react";
import CircularProgress from "@material-ui/core/CircularProgress";
import Typography from "@material-ui/core/Typography";
import { TimetableMonth } from "../../../../../model/timetable";
import CalendarMonthBase from "../../../../timetable/components/calendar/components/month/CalendarMonthBase";
import CalendarDayBase from "../../../../timetable/components/calendar/components/day/CalendarDayBase";
import CalendarSession from "../../../../timetable/components/calendar/components/session/CalendarSession";

interface Props {
  months: TimetableMonth[];
  fetching: boolean;
}

const DuplicateCourseClassTimetable: React.FunctionComponent<Props> = ({ months, fetching }) => {
  const [tagsExpanded, setTagsExpanded] = useState(true);

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
                      setTagsExpanded={setTagsExpanded}
                      tagsExpanded={tagsExpanded}
                      inView
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
            No sessions were found
          </Typography>
        </div>
      )}
    </div>
  );
};

export default DuplicateCourseClassTimetable;
