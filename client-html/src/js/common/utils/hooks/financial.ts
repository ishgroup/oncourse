/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Decimal } from 'decimal.js-light';

export const getTotalByFeeExTax = (taxRate: number, feeExTax: number): number => new Decimal(feeExTax || 0).mul(new Decimal(1).plus(taxRate)).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN).toNumber();

export const getFeeExTaxByFeeIncTax = (taxRate: number, feeIncTax: number): number => new Decimal(feeIncTax || 0).div(new Decimal(1).plus(taxRate)).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN).toNumber();
