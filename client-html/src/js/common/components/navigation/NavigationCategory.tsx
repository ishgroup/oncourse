/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { IconButton, Typography } from "@mui/material";
import Close from "@mui/icons-material/Close";
import React, { useMemo } from "react";
import { makeAppStyles } from "../../styles/makeStyles";
import navigation from "./navigation.json";

const useStyles = makeAppStyles(() => ({
  root: {
    
  }
}));

interface Props {
  selected: string;
  onClose: any;
}

const NavigationCategory = (
  {
    selected,
    onClose
  }:Props
) => {
  const category = useMemo(() => navigation.categories.find(c => c.key === selected), [selected]);

  console.log({ category });
  
  return (
    <div className="flex-fill p-3">
      <div className="d-flex">
        <Typography variant="h4" className="flex-fill">{category?.title}</Typography>
        <IconButton size="large" onClick={onClose}>
          <Close />
        </IconButton>
      </div>
    </div>
);
};

export default NavigationCategory;