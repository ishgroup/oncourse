/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import Typography from "@material-ui/core/Typography";
import { LinkAdornment } from "../../../common/components/form/FieldAdornments";
import { openInternalLink } from "../../../common/utils/links";

interface Props {
  title: string;
  type?: string;
  link?: string;
}

const CheckoutAppBar: React.FC<Props> = ({ title, type, link }) => (
  <>
    <div className="overflow-hidden">
      <Typography className="appHeaderFontSize" variant="body2">
        <span className="text-truncate text-nowrap d-block">
          {title}
        </span>
      </Typography>
    </div>
    {link && (
      <LinkAdornment
        linkColor="inherit"
        linkHandler={() => openInternalLink(`/${type}/${link}`)}
        link={link}
        className="appHeaderFontSize ml-1"
      />
    )}
    <div className="flex-fill" />
  </>
);

export default CheckoutAppBar;
