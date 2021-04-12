/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const prefixer = (property, value, prefixes = ["webkit", "moz", "ms", "spec"]) => {
  const prefixedItem = {};
  const propertyKey = property.charAt(0).toUpperCase() + property.slice(1);
  prefixes.forEach(item => {
    if (item === "webkit") {
      prefixedItem[`Webkit${propertyKey}`] = value;
    } else if (item === "moz") {
      prefixedItem[`Moz${propertyKey}`] = value;
    } else if (item === "ms") {
      prefixedItem[`Ms${propertyKey}`] = value;
    } else if (item === "spec") {
      prefixedItem[property] = value;
    }
  });
  return prefixedItem;
};
