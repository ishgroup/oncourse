/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from "react";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import ChevronRight from "@mui/icons-material/ChevronRight";
import ChevronLeft from "@mui/icons-material/ChevronLeft";

const ChartViewSwitcher: React.FC<any> = ({ showAllWeeks, setShowAllWeeks, theme }) => {
  const [showTooltip, setShowTooltip] = useState(false);

  const onHover = () => setShowTooltip(true);
  const onLeave = () => setShowTooltip(false);

  useEffect(() => {
    setShowTooltip(false);
  }, [showAllWeeks]);

  return (
    <Tooltip
      open={showTooltip}
      title={showAllWeeks ? "Show six weeks only." : "Extend timeline to show all enrolments."}
    >
      <IconButton
        onMouseEnter={onHover}
        onMouseLeave={onLeave}
        className="lightGrayIconButton absolute"
        style={{ left: theme.spacing(-3), bottom: "2px" }}
        onClick={() => {
          setShowAllWeeks(prev => !prev);
        }}
      >
        {showAllWeeks ? <ChevronRight fontSize="inherit" /> : <ChevronLeft fontSize="inherit" />}
      </IconButton>
    </Tooltip>
  );
};

export default ChartViewSwitcher;
