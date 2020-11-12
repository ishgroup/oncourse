/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { VARIANTS } from "../utils";

export const TOGGLE_SWIPEABLE_DRAWER = "common/swipeableDrawer/toggle";
export const SET_SWIPEABLE_DRAWER_DIRTY_FORM = "common/set/swipeableDrawer/dirty/form";

export const toggleSwipeableDrawer = (variant = VARIANTS.temporary) => ({
  type: TOGGLE_SWIPEABLE_DRAWER,
  payload: { variant }
});

export const setSwipeableDrawerDirtyForm = (isDirty?: boolean, resetEditView?: any) => ({
  type: SET_SWIPEABLE_DRAWER_DIRTY_FORM,
  payload: { isDirty, resetEditView }
});
