/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { format } from "date-fns";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import { Contact } from "@api/model";
import { D_MMM_YYYY } from "../../../../common/utils/dates/format";
import { SelectItemRendererProps } from "../../../../../ish-ui/model/Fields";

const ContactSelectItemRenderer = React.memo<SelectItemRendererProps<Contact>>(({ content, data, parentProps }) => {
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
    <div {...parentProps || {}}>
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
    </div>
  );
});

export default (content, data, search, parentProps) => <ContactSelectItemRenderer data={data} content={content} parentProps={parentProps} />;
