import { formatRelative, formatDistanceStrict, format } from "date-fns";

export const formatRelativeDate = (date: Date, baseDate: Date, formatString: string, excludeTime?: boolean, addDistance?: boolean) => {
  let result = formatRelative(date, baseDate);

  if (result.match(/\d\d\/\d\d\/\d\d\d\d/)) {
    result = format(date, formatString).replace(/\./g, "");

    if (addDistance) {
      return formatDistanceStrict(
        date,
        baseDate
      ) + " ago";
    }
  }

  if (excludeTime) {
    result = result.replace(/at\s\d:\d\d\s([AP])M/, "");
  }

  return result;
};
