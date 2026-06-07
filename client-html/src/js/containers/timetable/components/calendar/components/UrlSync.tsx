import { useContext, useEffect } from 'react';
import { useLocation, useRouteMatch } from 'react-router';
import { updateHistory } from '../../../../../common/utils/common';
import { useAppSelector } from '../../../../../common/utils/hooks';
import { TimetableContext } from '../../../Timetable';

export default function UrlSync(
  {

  }) {

  const {
    targetDay,
    calendarMode
  } = useContext(
    TimetableContext
  );

  const location = useLocation();
  const { url } = useRouteMatch();

  const params = new URLSearchParams(location.search);

  const months = useAppSelector(state => state.timetable.months);

  useEffect(() => {
    const calendarModeUrl = params.get("calendarMode");
    if (calendarModeUrl !== calendarMode) {
      params.set("calendarMode", calendarMode);
      updateHistory(params, url);
    }
  }, [calendarMode]);

  useEffect(() => {
    const selectedDateCurrent = params.get('selectedDate');
    if (selectedDateCurrent !== targetDay) {
      params.set("selectedDate", targetDay);
      updateHistory(params, url);
    }
  }, [targetDay]);



  return null;
}