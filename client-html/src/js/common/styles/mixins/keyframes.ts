/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { prefixer } from "./prefixer";

export const keyframes = (name, content, otherStyle = {}) => ({
  [`@keyframes ${name}`]: {
    ...content
  },
  [`.${name}`]: {
    ...prefixer("animationName", name, ["webkit", "spec"]),
    ...otherStyle
  }
});
