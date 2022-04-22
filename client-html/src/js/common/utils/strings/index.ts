/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const getPluralSuffix = (count: number) => (count === 1 ? "" : "s");

export const getLabelWithCount = (label: string, count: number): string =>
  (count > 0 ? `${count} ${label}${getPluralSuffix(count)}` : label + "s");

export const normalizeString = v => (v ? String(v) : v);

