/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SaleType } from "@api/model";

export const entityForLink = (type: SaleType) => {
  switch (type) {
    case SaleType.Class:
      return "classes";
    case SaleType.Product:
      return "product";
    case SaleType.Membership:
      return "membership";
    case SaleType.Voucher:
      return "voucher";
    case SaleType.Course:
      return "course";
    default: {
      console.error(`unknown sale type ${type}!`);
      return "";
    }
  }
};

export const formatFundingSourceId = val => (val === null ? -1 : val);

export const transformDataType = type => (type === "BOOLEAN"
  ? "Checkbox"
  : (type.substring(0, 1) + type.substring(1).toLowerCase()).replace("_", " "));
