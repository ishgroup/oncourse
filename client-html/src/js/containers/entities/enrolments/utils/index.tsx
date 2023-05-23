/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { openInternalLink } from "../../../../common/utils/links";
import { Enrolment } from "@api/model";
import React, { useMemo } from "react";
import Typography from "@mui/material/Typography";
import { Course } from "@api/model";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";
import { SelectItemRendererProps } from "../../../../model/common/Fields";
import LockOutlined from "@mui/icons-material/LockOutlined";
import { MenuItem } from "@mui/material";

export const openEnrolmentLink = id => openInternalLink("/enrolment/" + id);

export const EnrolmentSelectValueRenderer = (content, data, search, parentProps) => {
  console.log({ content, data, parentProps});

  return (<MenuItem {...parentProps || {}}>
    <div className="centered-flex">
      {content}
      {" "}

    </div>
  </MenuItem>);
};

export const EnrolmentSelectItemRenderer = (content, data, search, parentProps) => {
  return <div {...parentProps}>
    <div className="centered-flex">
      <div className="flex-fill">
        {content}
      </div>
      <Typography variant="caption" component="div" color="textSecondary" className="text-truncate">
        {data.status}
      </Typography>
    </div>
  </div>;
};

