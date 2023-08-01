/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Launch from "@mui/icons-material/Launch";
import { IconButton } from "@mui/material";
import { openInternalLink } from "ish-ui";
import React from "react";

interface Props {
  title: string;
  type?: string;
  link?: string;
}

const CheckoutAppBar: React.FC<Props> = ({ title, type, link }) => (
  <>
    <div className="d-inline-flex-center">
      {title}
      {
        link && (
        <IconButton 
          size="small" 
          color="primary" 
          onClick={() => openInternalLink(`/${type}/${link}`)}
        >
          <Launch fontSize="inherit" />
        </IconButton>
        )
      }
    </div>
  </>
);

export default CheckoutAppBar;
