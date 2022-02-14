/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
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

const AutomationSideBar = ({
    history
  }: Props) => {
    const classes = useStyles();

    const selectedIndex = useMemo(() => {
      switch (true) {
        case history.location.pathname.startsWith("/automation/script"):
          return 1;
        case history.location.pathname.startsWith("/automation/import-template"):
          return 2;
        case history.location.pathname.startsWith("/automation/export-template"):
          return 3;
        case history.location.pathname.startsWith("/automation/email-template"):
          return 4;
        case history.location.pathname.startsWith("/automation/pdf-background"):
          return 5;
        case history.location.pathname.startsWith("/automation/pdf-report"):
          return 6;
        case history.location.pathname.startsWith("/automation/integration"):
          return 7;
        default:
          return 0;
      }
    }, [history.location.pathname]);


    return (
      <div className={classes.root}>
        <SideBarHeader
          selected={selectedIndex === 1}
          label="Automations"
          onClick={() => history.push("/automation/scripts")}
        />

        <SideBarHeader
          selected={selectedIndex === 2}
          label="Import Templates"
          onClick={() => history.push("/automation/import-templates/")}
        />

        <SideBarHeader
          selected={selectedIndex === 3}
          label="Export Templates"
          onClick={() => history.push("/automation/export-templates/")}
        />

        <SideBarHeader
          selected={selectedIndex === 4}
          label="Message Templates"
          onClick={() => history.push("/automation/email-templates/")}
        />

        <SideBarHeader
          selected={selectedIndex === 5}
          label="PDF Backgrounds"
          onClick={() => history.push("/automation/pdf-backgrounds/")}
        />

        <SideBarHeader
          selected={selectedIndex === 6}
          label="PDF Reports"
          onClick={() => history.push("/automation/pdf-reports/")}
        />

        <SideBarHeader
          selected={selectedIndex === 7}
          label="Integrations"
          onClick={() => history.push("/automation/integrations")}
        />
      </div>
    );
  };

export default AutomationSideBar;
