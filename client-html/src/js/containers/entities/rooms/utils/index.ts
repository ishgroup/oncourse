/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { openInternalLink } from "ish-ui";

export const openRoomLink = (roomId: number) => {
  openInternalLink("/room/" + roomId);
};
