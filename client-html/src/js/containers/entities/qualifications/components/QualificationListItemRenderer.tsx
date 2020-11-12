/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Typography from "@material-ui/core/Typography/Typography";
import { Qualification } from "@api/model";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";

const QualificationListItemRenderer = React.memo<{ content: string; search: string; data: Qualification }>(
  ({ search, data }) => {
    const highlightedCode = useMemo(
      () => (data.nationalCode ? (search ? getHighlightedPartLabel(data.nationalCode, search) : data.nationalCode) : "no course code"),
      [data.nationalCode, search]
    );

    const highlightedTitle = useMemo(
      () => (data.title ? (search ? getHighlightedPartLabel(data.title, search) : data.title) : "no title"),
      [data.title, search]
    );

    return (
      <div className="overflow-hidden">
        <div className="text-nowrap text-truncate">
          {highlightedTitle}
        </div>
        <Typography noWrap variant="caption" component="div" color="textSecondary" className="text-truncate">
          {highlightedCode}
        </Typography>
      </div>
    );
  }
);

export default (content, data, search) => <QualificationListItemRenderer data={data} content={content} search={search} />;
