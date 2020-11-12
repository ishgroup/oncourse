import { getDay, getDaysInMonth, lastDayOfMonth } from "date-fns";

const generateOneMonth = (today, status) =>
  Array(getDaysInMonth(today))
    .fill(null)
    .map((el, id) => ({
      day: id + 1,
      date: new Date(today.getFullYear(), today.getMonth(), id + 1),
      status
    }));

export const getCalendarDays = today => {
  const currentMonth = generateOneMonth(today, "current");
  const previousMonth = generateOneMonth(new Date(today.getFullYear(), today.getMonth() - 1), "previous");
  const nextMonth = generateOneMonth(new Date(today.getFullYear(), today.getMonth() + 1), "next");

  const firstDayIndex = getDay(new Date(today.getFullYear(), today.getMonth(), 1));
  const lastDayIndex = getDay(lastDayOfMonth(today));

  const binaryCalendarState = (firstDayIndex, lastDayIndex) =>
    parseInt(String(+!!firstDayIndex) + +(lastDayIndex < 6), 2);

  switch (binaryCalendarState(firstDayIndex, lastDayIndex)) {
    case 0:
      return currentMonth;
    case 1:
      return currentMonth.concat(nextMonth.slice(0, 6 - lastDayIndex));
    case 2:
      return previousMonth.slice(previousMonth.length - firstDayIndex).concat(currentMonth);
    case 3:
      return previousMonth
        .slice(previousMonth.length - firstDayIndex)
        .concat(currentMonth)
        .concat(nextMonth.slice(0, 6 - lastDayIndex));
    default:
      return currentMonth;
  }
};
