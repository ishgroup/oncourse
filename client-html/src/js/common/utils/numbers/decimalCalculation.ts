/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Decimal } from "decimal.js-light";

export const decimalMul = (...args): number => args
  .reduce((p, c) => p.mul(c || 0), new Decimal(1))
  .toDecimalPlaces(2)
  .toNumber();

export const decimalPlus = (...args): number => args
  .reduce((p, c) => p.plus(c || 0), new Decimal(0))
  .toDecimalPlaces(2)
  .toNumber();

export const decimalMinus = (...args): number => args
  .reduce((p, c) => new Decimal(p).minus(c))
  .toDecimalPlaces(2)
  .toNumber();

export const decimalDivide = (a: number, b: number): number => new Decimal(a)
  .div(b)
  .toDecimalPlaces(2)
  .toNumber();
