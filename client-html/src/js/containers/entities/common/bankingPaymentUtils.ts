import { Decimal } from "decimal.js-light";
import { formatCurrency } from "../../../common/utils/numbers/numbersNormalizing";
import { Currency, Payment } from "@api/model";

export const defaultCurrencySymbol = "$";

export const paymentInTypeName = "payment in";

export const calculatePaymentsTotal = (payments: Payment[], onlySelected: boolean) => {
  const filtered = payments.filter(v => {
    if (onlySelected) {
      // @ts-ignore
      return !!v.selected;
    }
    return true;
  });
  return filtered.reduce(
    (a, v) => (v.paymentTypeName === paymentInTypeName ? a.plus(v.amount) : a.minus(v.amount)),
    new Decimal(0)
  );
};

export const getFormattedTotal = (payments: Payment[], currency: Currency, onlySelected: boolean): string => {
  if (!payments) {
    return "No value";
  }
  const shortCurrencySymbol = currency != null ? currency.shortCurrencySymbol : defaultCurrencySymbol;
  return formatCurrency(calculatePaymentsTotal(payments, onlySelected), shortCurrencySymbol);
};
