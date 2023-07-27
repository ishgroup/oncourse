/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Alert from "@mui/lab/Alert";
import Tooltip from "@mui/material/Tooltip";
import { makeAppStyles } from  "ish-ui";

const useStyles = makeAppStyles(() => ({
  alertRoot: {
    padding: 3,
    fontSize: "0.75em",
    alignItems: "center"
  },
  alertIcon: {
    padding: 0,
    marginRight: 3,
    "& > svg": {
      width: "0.85em",
      height: "0.85em"
    }
  }
}));

interface Props {
  message: string;
}

const CheckoutAlertTextMessage: React.FC<Props> = (props => {
  const classes  = useStyles();
  const { message } = props;

  return message && (
    <Tooltip title={message} arrow>
      <Alert
        severity="error"
        classes={{
          root: classes.alertRoot,
          message: "w-100 text-truncate text-nowrap d-block p-0",
          icon: classes.alertIcon
        }}
      >
        {message}
      </Alert>
    </Tooltip>
  );
});

export default CheckoutAlertTextMessage;
