/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import Typography from "@material-ui/core/Typography/Typography";

const LeadSelectItemRenderer = React.memo<{ content: string; data: any }>(props => {
  const { content, data } = props;

  const caption = useMemo(
    () => (
      <span>
        {data["items.course.code"].length > 2 ? data["items.course.code"].replace("[", "").replace("]", "") : "no courses"}
        &nbsp;&nbsp;
        {"- " + data.estimatedValue || "0"}
      </span>
    ),
    [data["items.course.code"], data.estimatedValue]
  );

  return (
    <div className="overflow-hidden">
      <div className="text-nowrap text-truncate">
        {content}
      </div>
      <Tooltip title={caption}>
        <Typography variant="caption" component="div" color="textSecondary" className="text-truncate">
          {caption}
        </Typography>
      </Tooltip>
    </div>
  );
});

export default (content, data) => <LeadSelectItemRenderer data={data} content={content} />;
