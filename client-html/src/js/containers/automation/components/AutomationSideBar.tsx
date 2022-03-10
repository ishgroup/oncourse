/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useState
} from "react";
import { History } from "history";
import debounce from "lodash.debounce";
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

const getSelected = (history: History) => {
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
};

const AutomationSideBar = ({
    history
  }: Props) => {
  const [selected, setSelected] = useState(() => getSelected(history));

  const classes = useStyles();

  // Animation timeout
  const updateHistory = useCallback(debounce(route => {
    history.push(route);
  }, 250), []);
  
  const selectHandler = (route, index) => () => {
    setSelected(index);
    updateHistory(route);
  };

  return (
    <div className={classes.root}>
      <SideBarHeader
        selected={selected === 1}
        label="Automations"
        onClick={selectHandler("/automation/scripts", 1)}
      />

      <SideBarHeader
        selected={selected === 2}
        label="Import Templates"
        onClick={selectHandler("/automation/import-templates/", 2)}
      />

      <SideBarHeader
        selected={selected === 3}
        label="Export Templates"
        onClick={selectHandler("/automation/export-templates/", 3)}
      />

      <SideBarHeader
        selected={selected === 4}
        label="Message Templates"
        onClick={selectHandler("/automation/email-templates/", 4)}
      />

      <SideBarHeader
        selected={selected === 5}
        label="PDF Backgrounds"
        onClick={selectHandler("/automation/pdf-backgrounds/", 5)}
      />

      <SideBarHeader
        selected={selected === 6}
        label="PDF Reports"
        onClick={selectHandler("/automation/pdf-reports/", 6)}
      />

      <SideBarHeader
        selected={selected === 7}
        label="Integrations"
        onClick={selectHandler("/automation/integrations", 7)}
      />
    </div>
  );
};

export default AutomationSideBar;
