/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Typography from "@mui/material/Typography";
import { makeAppStyles } from "../../../../../styles/makeStyles";

const useStyles = makeAppStyles(theme => ({
  root: {
    minHeight: "unset",
    marginRight: theme.spacing(1)
  },
  tab: {
    textTransform: "none",
    padding: theme.spacing(0.5),
    minHeight: "unset",
    fontSize: "13px"
  }
}));

export default function FiltersSwitcher({ setValue, value }) {
  const classes = useStyles();

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  return (
    <div className="w100 centeredFlex mt-2">
      <Typography variant="caption" className="flex-fill">
        Filter by
      </Typography>
      <Tabs classes={{ root: classes.root }} value={value} onChange={handleChange}>
        <Tab
          classes={{
            root: classes.tab
          }}
          label="Filters & Tags"
        />
        <Tab
          classes={{
            root: classes.tab
          }}
          label="Checklists"
        />
      </Tabs>
    </div>
  );
}