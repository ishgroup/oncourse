/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

// extending String with capitalize method
const stringProto = String.prototype as any;

stringProto.capitalize = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};

// @ts-ignore
Date.prototype.stdTimezoneOffset = function () {
  const jan = new Date(this.getFullYear(), 0, 1);
  const jul = new Date(this.getFullYear(), 6, 1);
  return Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
};

// @ts-ignore
Date.prototype.dstOffset = function () {
  return this.getTimezoneOffset() - this.stdTimezoneOffset();
};