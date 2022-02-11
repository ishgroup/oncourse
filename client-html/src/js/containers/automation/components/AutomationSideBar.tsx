/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { History } from "history";
import SideBarHeader from "../../../common/components/layout/side-bar-list/SideBarHeader";
import { makeAppStyles } from "../../../common/styles/makeStyles";

interface Props {
  history: History;
}

const useStyles = makeAppStyles(theme => ({
  root: {
    color: theme.palette.secondary.main,
    padding: theme.spacing(3),
    marginTop: theme.spacing(3)
  }
}));

const AutomationSideBar = React.memo<Props>(
  ({
    history
  }) => {
    const classes = useStyles();

    return (
      <div className={classes.root}>
        <SideBarHeader
          selected={history.location.pathname.startsWith("/automation/script")}
          label="Automations"
          onClick={() => history.push("/automation/scripts")}
        />

        <SideBarHeader
          selected={history.location.pathname.startsWith("/automation/import-templates/")}
          label="Import Templates"
          onClick={() => history.push("/automation/import-templates/")}
        />

        <SideBarHeader
          selected={history.location.pathname.startsWith("/automation/export-templates/")}
          label="Export Templates"
          onClick={() => history.push("/automation/export-templates/")}
        />

        <SideBarHeader
          selected={history.location.pathname.startsWith("/automation/email-templates/")}
          label="Message Templates"
          onClick={() => history.push("/automation/email-templates/")}
        />

        <SideBarHeader
          selected={history.location.pathname.startsWith("/automation/pdf-backgrounds/")}
          label="PDF Backgrounds"
          onClick={() => history.push("/automation/pdf-backgrounds/")}
        />

        <SideBarHeader
          selected={history.location.pathname.startsWith("/automation/pdf-reports/")}
          label="PDF Reports"
          onClick={() => history.push("/automation/pdf-reports/")}
        />

        <SideBarHeader
          selected={history.location.pathname.startsWith("/automation/integrations/list")}
          label="Integrations"
          onClick={() => history.push("/automation/integrations/list")}
        />
      </div>
    );
  }
);

export default AutomationSideBar;
