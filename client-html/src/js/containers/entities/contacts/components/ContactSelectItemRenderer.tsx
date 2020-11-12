/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { format } from "date-fns";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import Typography from "@material-ui/core/Typography/Typography";
import { Contact } from "@api/model";
import { D_MMM_YYYY } from "../../../../common/utils/dates/format";

const ContactSelectItemRenderer = React.memo<{ content: string; data: Contact }>(props => {
  const { content, data } = props;

  const caption = useMemo(
    () => (
      <span>
        {data.birthDate ? format(new Date(data.birthDate), D_MMM_YYYY) : "no birth date"}
        &nbsp;&nbsp;
        {data.email || "no email"}
      </span>
    ),
    [data.birthDate, data.email]
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

export default (content, data) => <ContactSelectItemRenderer data={data} content={content} />;
