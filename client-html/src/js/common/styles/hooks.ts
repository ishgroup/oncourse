/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import makeStyles from "@material-ui/core/styles/makeStyles";

export const useHoverShowStyles = makeStyles({
  container: {
    "&:hover $target": {
      visibility: "visible"
    }
  },
  target: {
    visibility: "hidden"
  }
});
