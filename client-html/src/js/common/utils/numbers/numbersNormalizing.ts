import Decimal from "decimal.js-light";

const currencyFormatter = new Intl.NumberFormat("en-US", { maximumFractionDigits: 2 });

export const normalizeNumber = v => (typeof v === "number" ? v : typeof v === "string" && v ? parseFloat(v) : v);

export const normalizeNumberToZero = v => (typeof v === "number" ? v : typeof v === "string" && v ? parseFloat(v) : 0);

export const normalizeNumberToPositive = v => {
  const parsed = normalizeNumber(v);
  return parsed < 0 ? Math.abs(parsed) : parsed;
};

export const formatCurrency = (v, cs: string) => {
  const formatted = currencyFormatter.format(v);
  let value = `${cs}0.00`;
  if (formatted !== "NaN") {
    const formattedValue = parseFloat(v);
    if (formattedValue < 0) value = `-${cs}${currencyFormatter.format(-formattedValue)}`;
    else value = `${cs}${currencyFormatter.format(formattedValue)}`;
  }
  return value;
};

export const idsToString = (items: any[]): string => items
    .filter(item => item.id)
    .map(item => item.id)
    .map(id => (Number(id) < 0 ? `'${id}'` : id))
    .toString();

export const formatPercent = val => {
  const result = Number(val);
  if (isNaN(result)) {
    return "";
  }
  return `${new Decimal(result * 100).toDecimalPlaces(2).toString()}%`;
};

export const formatFieldPercent = val => val && Math.round(+val * 10000) / 100;
export const parseFieldPercent = val => val ? Math.round(+val * 100) / 10000 : 0;

export const preventNegativeOrLogEnter = ev => {
  if (ev.key === "-" || ev.key === "e") {
    ev.preventDefault();
  }
};

export const preventDecimalEnter = ev => {
  if (ev.key === ".") {
    ev.preventDefault();
  }
};
