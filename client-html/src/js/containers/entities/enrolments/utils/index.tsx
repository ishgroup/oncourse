/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { openInternalLink } from "ish-ui";
import React, { useMemo } from "react";
import Typography from "@mui/material/Typography";
import { getHighlightedPartLabel } from "ish-ui";
import { MenuItem } from "@mui/material";

export const openEnrolmentLink = id => openInternalLink("/enrolment/" + id);

export const EnrolmentSelectValueRenderer = (content, data, search, parentProps) => (<MenuItem {...parentProps || {}}>
  <div className="d-flex align-items-baseline overflow-hidden">
    <div className="fontWeight600 text-truncate mr-2">
      {content}
      {" "}
      ({data["courseClass.course.code"]}-{data["courseClass.code"]})
    </div>
    <Typography variant="caption" component="div" color="inherit">
      {data.displayStatus}
    </Typography>
  </div>
</MenuItem>);

const EnrolmentSelectItem = ({ content, data, search, parentProps }) => {
  const highlightedCode = useMemo(
    () => getHighlightedPartLabel(`${data["courseClass.course.code"]}-${data["courseClass.code"]}`, search),
    [data, search]
  );

  return <div {...parentProps}>
    <div className="centeredFlex flex-fill overflow-hidden">
      <div className="flex-fill text-truncate mr-2">
        {content}
        {" "}
        ({highlightedCode})
      </div>
      <Typography variant="caption" component="div" color="textSecondary">
        {data.displayStatus}
      </Typography>
    </div>
  </div>;
};

export const EnrolmentSelectItemRenderer = (content, data, search, parentProps) =>
  <EnrolmentSelectItem
    content={content}
    data={data}
    search={search}
    parentProps={parentProps}
  />;