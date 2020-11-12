import { Currency } from "@api/model";

export function mockCurrency(): Currency {
  return {
    currencySymbol: "AUD",
    name: "AUSTRALIA",
    shortCurrencySymbol: "$"
  };
}
