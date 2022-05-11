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
        label="Tag groups"
        onClick={selectHandler("/tags/tagGroups/", 1)}
      />

      <SideBarHeader
        selected={selected === 2}
        label="Checklists"
        onClick={selectHandler("/tags/checklists/", 2)}
      />
    </div>
  );
};

export default TagSidebar;
