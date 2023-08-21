/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Course } from "@api/model";
import Typography from "@mui/material/Typography";
import { getHighlightedPartLabel, SelectItemRendererProps } from "ish-ui";
import React, { useMemo } from "react";

const CourseItemRenderer = React.memo<SelectItemRendererProps<Course>>(
  ({
 content, search, data, parentProps 
}) => {
    const highlightedCode = useMemo(
      () => (data.code ? (search ? getHighlightedPartLabel(data.code, search) : data.code) : "no course code"),
      [data.code, search]
    );

    return (
      <div {...parentProps}>
        <div className="overflow-hidden">
          <div className="text-nowrap text-truncate">
            {content}
          </div>
          <Typography variant="caption" component="div" color="textSecondary" className="text-truncate">
            {highlightedCode}
          </Typography>
        </div>
      </div>
    );
  }
);

export default (content, data, search, parentProps) => <CourseItemRenderer data={data} content={content} search={search} parentProps={parentProps} />;
