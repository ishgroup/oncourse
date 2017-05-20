import {Formats} from "../../constants/Formats";
import moment from "moment";
/**
 * this utils collect useful function to format different data types: date, string .....
 */

/**
 * Format date string in ISO8601 to string with this pattern
 */
export const formatDate = (dateISO8601: string, pattern: string): string => {
  return moment(dateISO8601).add(moment().utcOffset(), "m").format(pattern)
};
