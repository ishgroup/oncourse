/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Box, CircularProgress } from "@mui/material";
import React from "react";

interface StaticProgressProps {
  color: string;
  value: number;
  size?: number;
  thickness?: number;
  className?: string;
}

const StaticProgress = ({ className, color, value, size = 24, thickness = 8 }: StaticProgressProps) => (
  <Box className={className} sx={{ position: 'relative', display: "flex", alignItems: "center" }}>
    <CircularProgress
      variant="determinate"
      sx={{
        color: theme => theme.palette.grey[theme.palette.mode === 'light' ? 200 : 800],
      }}
      size={size}
      thickness={thickness}
      value={100}
    />
    <CircularProgress
      variant="determinate"
      sx={{
        color: `#${color}`,
        position: 'absolute',
        left: 0
      }}
      size={size}
      thickness={thickness}
      value={value}
    />
  </Box>
);

export default StaticProgress;