/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import { User } from "@api/model";
import { Tooltip, Typography } from "@mui/material";
import { getContactFullName } from "../../../../entities/contacts/utils";

const UserSelectItemRenderer = React.memo<{ content: string; data: User, parentProps: any }>(props => {
  const { data, parentProps } = props;

  const caption = useMemo(
    () => (
      <span>
        {data.email || "no email"}
      </span>
    ),
    [data.email]
  );

  return (
    <div {...parentProps}>
      <div className="overflow-hidden">
        <div className="text-nowrap text-truncate">
          {getContactFullName(data)}
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

export default (content, data, search, parentProps) => <UserSelectItemRenderer data={data} content={content} parentProps={parentProps} />;
