/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Typography from "@material-ui/core/Typography/Typography";
import { Course } from "@api/model";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";

const CourseItemRenderer = React.memo<{ content: string; search: string; data: Course }>(
  ({ content, search, data }) => {
    const highlightedCode = useMemo(
      () => (data.code ? (search ? getHighlightedPartLabel(data.code, search) : data.code) : "no course code"),
      [data.code, search]
    );

    return (
      <div className="overflow-hidden">
        <div className="text-nowrap text-truncate">
          {content}
        </div>
        <Typography variant="caption" component="div" color="textSecondary" className="text-truncate">
          {highlightedCode}
        </Typography>
      </div>
    );
  }
);

export default (content, data, search) => <CourseItemRenderer data={data} content={content} search={search} />;
