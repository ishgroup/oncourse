import moment from "moment";
/**
 * this utils collect useful function to format different data types: date, string .....
 */

/**
 * https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/NumberFormat
 */
const MONEY_FORMAT = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  minimumFractionDigits: 2,
});

/**
 * Format date string in ISO8601 to string with this pattern
 */
export const formatDate = (dateISO8601: string, pattern: string): string => {
  return dateISO8601 ? moment(dateISO8601).add(moment().utcOffset(), "m").format(pattern) : null;
};

/**
 * Return string with a currency symbol
 */
export const formatMoney = (value: string): string => {
  return MONEY_FORMAT.format(Number.parseFloat(value))
};
