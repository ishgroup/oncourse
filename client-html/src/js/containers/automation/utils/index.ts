/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const validateKeycode = value =>
  value && value.startsWith("ish.") ? "Custom automation key codes cannot start with 'ish.'" : undefined;
