/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  ReactNode
} from "react";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import Typography from "@mui/material/Typography";
import ListItem from "@mui/material/ListItem";
import { makeAppStyles } from "../../../styles/makeStyles";

interface Props {
  selected: boolean;
  label: string;
  onClick: any;
  labelAdornment?: ReactNode;
}

const useStyles = makeAppStyles(theme => ({
  root: {
    alignItems: "flex-start",
    marginBottom: theme.spacing(3),
    color: "inherit",
    fontWeight: 600,
    opacity: 0.6,
    padding: 0,
    position: "relative",
    cursor: 'pointer',
    width: "100%",
    willChange: "transform, visibility, backgroundColor, opacity",
    "&$selected": {
      opacity: 1,
      backgroundColor: "inherit",
      "& $arrowIcon": {
        transform: "translateX(0)",
        visibility: "visible"
      },
      "& $label": {
        paddingLeft: 30,
      },
    },
    "&:hover": {
      opacity: 0.8,
    }
  },
  label: {
    textTransform: "uppercase",
    fontFamily: theme.typography.fontFamily,
    fontSize: theme.typography.fontSize,
    fontWeight: "bold" as "bold",
    lineHeight: 1.22,
    transition: "all 0.2s ease-in-out",
  },
  selected: {},
  arrowIcon: {
    visibility: "hidden",
    position: "absolute",
    transform: "translateX(-30px)",
    transition: "all 0.2s ease-in-out",
  }
}));

const SideBarHeader = (
  {
    selected,
    label,
    onClick,
    labelAdornment
  }: Props
) => {
  const classes = useStyles();

  return (
    <ListItem
      selected={selected}
      classes={{
        root: classes.root,
        selected: classes.selected
      }}
      onClick={onClick}
    >
      <div>
        <div className="centeredFlex mb-1">
          <ArrowForwardIcon color="inherit" fontSize="small" className={classes.arrowIcon} />
          <div className={classes.label}>{label}</div>
        </div>
        {labelAdornment && (
          <Typography variant="caption" component="div" className="text-pre-wrap">
            {labelAdornment}
          </Typography>
        )}
      </div>
    </ListItem>
);
};

export default SideBarHeader;