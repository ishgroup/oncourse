/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const PREVENT_EDIT_ON_FOCUS = "prevent/edit/on/focus";

export const setPreventOnFocus = (prevent: boolean) => ({
  type: PREVENT_EDIT_ON_FOCUS,
  payload: { prevent }
});
