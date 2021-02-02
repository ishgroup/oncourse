import moment from "moment-timezone";
/**
 * this utils collect useful function to format different data types: date, string .....
 */

/**
 * https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/NumberFormat
 */
const MONEY_FORMAT = global.Intl ?
  new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    currencyDisplay: 'narrowSymbol',
    minimumFractionDigits: 2,
  }) : {
    format: val => (val.toString()),
  };

/**
 * Format date string in ISO8601 to string with this pattern
 */
export const formatDate = (dateISO8601: string, pattern: string, timezone?: string): string => {

  // apply timezone if it provided
  if (timezone) {
    return dateISO8601 ? moment(dateISO8601).tz(timezone).format(pattern) : null;
  }

  return dateISO8601 ? moment(dateISO8601).format(pattern) : null;
};

/**
 * Return string with a currency symbol
 */
export const formatMoney = (value: number): string => {
  return MONEY_FORMAT.format(value);
};
