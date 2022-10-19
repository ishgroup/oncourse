/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { prefixer } from "./prefixer";

export const transform = (value = "none") => prefixer("transform", value);

export const transition = (theme, value = "none", options = null) => {
  const optionItems = options || {
    duration: theme.transitions.duration.standard,
    easing: theme.transitions.easing.easeInOut
  };

  if (value === "none") {
    return {
      transition: "none",
    };
  }

  if (value === "all") {
    return {
      willChange: "auto",
      transition: theme.transitions.create(value, optionItems),
    };
  }

  const values = value.split(",").map(v => v.trim());
  const transitionValue = [];

  values.forEach(v => {
    transitionValue.push(theme.transitions.create(v, optionItems));
  });

  return {
    willChange: value,
    transition: transitionValue.join(", ")
  };
};