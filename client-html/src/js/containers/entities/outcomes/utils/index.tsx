/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */


import { openInternalLink } from "../../../../common/utils/links";
import { MenuItem } from "@mui/material";
import Typography from "@mui/material/Typography";
import { useMemo } from "react";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";
import * as React from "react";

export const openOutcomeLink = id => openInternalLink("/outcome/" + id);

export const OutcomeSelectValueRenderer = (content, data, search, parentProps) => (<MenuItem {...parentProps || {}}>
  <div className="d-flex align-items-baseline overflow-hidden">
    <div className="fontWeight600 text-truncate mr-2">
      {content}
      {" "}
      ({data["module.title"]})
    </div>
    <Typography variant="caption" component="div" color="inherit">
      {data.status}
    </Typography>
  </div>
</MenuItem>);

const OutcomeSelectItem = ({ content, data, search, parentProps }) => {
  const highlightedTitle = useMemo(
    () => getHighlightedPartLabel(data["module.title"], search),
    [data.code, search]
  );

  return <div {...parentProps}>
    <div className="centeredFlex flex-fill overflow-hidden">
      <div className="flex-fill text-truncate mr-2">
        {content}
        {" "}
        {highlightedTitle}
      </div>
      <Typography variant="caption" component="div" color="textSecondary">
        {data.status}
      </Typography>
    </div>
  </div>;
};

export const OutcomeSelectItemRenderer = (content, data, search, parentProps) =>
  <OutcomeSelectItem
    content={content}
    data={data}
    search={search}
    parentProps={parentProps}
  />;