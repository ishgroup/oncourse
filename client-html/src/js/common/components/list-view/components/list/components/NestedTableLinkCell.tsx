/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import OpenInNew from "@mui/icons-material/OpenInNew";
import { openInternalLink } from "ish-ui";
import React from "react";

const NestedTableLinkCell = props => {
  const {
    classes, value, link
  } = props;

  return (
    <div className="centeredFlex linkDecoration" onClick={() => openInternalLink(link)}>
      <span className="text-truncate">{value}</span>
      <OpenInNew className={classes.linkIcon} color="primary"/>
    </div>
  );
};

export default NestedTableLinkCell;
