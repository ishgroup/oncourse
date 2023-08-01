/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { History } from "history";
import { makeAppStyles } from "ish-ui";
import React from "react";
import SideBarHeader from "../../../common/components/layout/side-bar-list/SideBarHeader";

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
    case history.location.pathname.startsWith("/tags/tagGroup"):
      return 1;
    case history.location.pathname.startsWith("/tags/checklist"):
      return 2;
    default:
      return 0;
  }
};

const TagSidebar = ({
   history
 }: Props) => {
  const classes = useStyles();

  const selectHandler = route => () => {
    history.push(route);
  };

  const selected = getSelected(history);

  return (
    <div className={classes.root}>
      <SideBarHeader
        selected={selected === 1}
        label="Tag groups"
        onClick={selectHandler("/tags/tagGroups")}
      />

      <SideBarHeader
        selected={selected === 2}
        label="Checklists"
        onClick={selectHandler("/tags/checklists")}
      />
    </div>
  );
};

export default TagSidebar;
