/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Typography from "@mui/material/Typography";
import { Course } from "@api/model";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";
import { SelectItemRendererProps } from "../../../../../ish-ui/model/Fields";

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
