/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createStyles } from "@material-ui/core/styles";
import { AppTheme } from "../../../../model/common/Theme";

export default (theme: AppTheme) => createStyles({
  root: {
    marginTop: `-${theme.spacing(1)}px`,
    paddingBottom: `${theme.spacing(2) - 3}px`
  },
  icon: {
    fontSize: theme.spacing(2),
    display: "inline-block",
    verticalAlign: "middle",
    marginRight: theme.spacing(0.5),
  }
});
