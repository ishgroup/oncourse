/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import { User } from "@api/model";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import Typography from "@material-ui/core/Typography";
import { contactLabelCondition } from "../../../../entities/contacts/utils";

const UserSelectItemRenderer = React.memo<{ content: string; data: User }>(props => {
  const { data } = props;

  const caption = useMemo(
    () => (
      <span>
        {data.email || "no email"}
      </span>
    ),
    [data.email]
  );

  return (
    <div className="overflow-hidden">
      <div className="text-nowrap text-truncate">
        {contactLabelCondition(data)}
      </div>
      <Tooltip title={caption}>
        <Typography variant="caption" component="div" color="textSecondary" className="text-truncate">
          {caption}
        </Typography>
      </Tooltip>
    </div>
  );
});

export default (content, data) => <UserSelectItemRenderer data={data} content={content} />;
