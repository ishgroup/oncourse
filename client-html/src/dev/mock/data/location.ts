import { Location } from "@api/model";

export function mockLocation(): Location {
  return {
    id: 11111,
    currency: {
      currencySymbol: "AUD",
      name: "AUSTRALIA",
      shortCurrencySymbol: "$"
    },
    countryCode: 'AU',
    languageCode: 'en'
  };
}
