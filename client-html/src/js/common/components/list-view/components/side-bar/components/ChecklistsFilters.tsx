/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useState } from "react";
import { alpha } from "@mui/material/styles";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { ButtonBase, Collapse } from "@mui/material";
import clsx from "clsx";
import ListTagGroup from "./ListTagGroup";
import { makeAppStyles } from "../../../../../styles/makeStyles";
import { ColoredCheckBox } from "../../../../form/ColoredCheckBox";
import { stubFunction } from "../../../../../utils/common";
import { useAppSelector } from "../../../../../utils/hooks";

const useStyles = makeAppStyles(theme => ({
  expandable: {
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-start",
    width: "100%",
    padding: theme.spacing(1),
    marginTop: theme.spacing(1),
    borderRadius: `${theme.shape.borderRadius}px`,
    backgroundColor: alpha(theme.palette.text.primary, 0.025),
    "& span": {
      textDecoration: "none",
      color: theme.palette.text.primary
    }
  },
  expandIcon: {
    transition: theme.transitions.create("transform"),
  },
  expandedIcon: {
    transform: "rotate(180deg)"
  }
}));

interface Props {
  updateChecked: any;
  updateUnChecked: any;
}

const ChecklistsFilters = (
  {
    updateChecked, 
    updateUnChecked 
}: Props
) => {
  const [expanded, setExpanded] = useState(null);
  const classes = useStyles();

  const checkedChecklists = useAppSelector(state => state.list.checkedChecklists);
  const uncheckedChecklists = useAppSelector(state => state.list.uncheckedChecklists);
  
  const onUpdateChecked = active => {
    updateChecked(checkedChecklists.map(cl => ({
      ...cl.tagBody.id === active.tagBody.id ? active : cl
    })));
  };

  const onUpdateUnChecked = active => {
    updateUnChecked(uncheckedChecklists.map(cl => ({
      ...cl.tagBody.id === active.tagBody.id ? active : cl
    })));
  };

  return (
    <div className="pr-2">
      <div className="heading mt-3 mb-2">
        Select task status
      </div>

      <ButtonBase className={classes.expandable} onClick={() => setExpanded(expanded === "1" ? null : "1")}>
        <ColoredCheckBox
          className="flex-fill"
          label="Completed"
          color="43a047"
          input={{ onChange: stubFunction, value: true } as any}
          meta={{} as any}
        />
        <ExpandMoreIcon color="secondary" className={clsx(classes.expandIcon, expanded === "1" && classes.expandedIcon)} />
      </ButtonBase>
      
      <Collapse in={expanded === "1"}>
        {checkedChecklists.map((t, index) => {
          if (!t.children.length) {
            return null;
          }
          return (
            <ListTagGroup
              key={t.prefix + t.tagBody.id.toString()}
              dndKey={index}
              rootTag={t}
              classes={{}}
              updateActive={onUpdateChecked}
              showColoredDots={false}
              dndEnabled={false}
            />
          );
        })}
      </Collapse>

      <ButtonBase className={classes.expandable} onClick={() => setExpanded(expanded === "2" ? null : "2")}>
        <ColoredCheckBox
          className="flex-fill"
          label="Incomplete"
          color="43a047"
          input={{ onChange: stubFunction, value: false } as any}
          meta={{} as any}
        />
        <ExpandMoreIcon color="secondary" className={clsx(classes.expandIcon, expanded === "2" && classes.expandedIcon)} />
      </ButtonBase>

      <Collapse in={expanded === "2"}>
        {uncheckedChecklists.map((t, index) => {
          if (!t.children.length) {
            return null;
          }
          return (
            <ListTagGroup
              key={t.prefix + t.tagBody.id.toString()}
              dndKey={index}
              rootTag={t}
              classes={{}}
              updateActive={onUpdateUnChecked}
              showColoredDots={false}
              dndEnabled={false}
            />
          );
        })}
      </Collapse>
    </div>
);
};

export default ChecklistsFilters;