/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { VARIANTS } from "../../../common/components/layout/swipeable-sidebar/utils";

export interface SwipeableDrawer {
  opened: boolean;
  variant?: keyof typeof VARIANTS;
  selected?: number | string;
  resetEditView?: () => void;
}
