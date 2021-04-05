/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const getLabelWithCount = (label: string, count: number): string =>
  (count > 0 ? `${count} ${label}${count > 1 ? "s" : ""}` : label + "s");

// extending String with capitalize method
const stringProto = String.prototype as any;

stringProto.capitalize = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};
