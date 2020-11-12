/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from "react";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import ChevronRight from "@material-ui/icons/ChevronRight";
import ChevronLeft from "@material-ui/icons/ChevronLeft";

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
