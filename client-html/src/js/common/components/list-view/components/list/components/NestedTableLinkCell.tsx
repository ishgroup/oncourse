/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import OpenInNew from "@mui/icons-material/OpenInNew";
import { openInternalLink } from "../../../../../utils/links";

const NestedTableLinkCell = props => {
  const {
 classes, value, link
} = props;

  return (
    <div className="centeredFlex linkDecoration" onClick={() => openInternalLink(link)}>
      <span className="text-truncate">{value}</span>
      <OpenInNew className={classes.linkIcon} color="primary" />
    </div>
  );
};

export default NestedTableLinkCell;
