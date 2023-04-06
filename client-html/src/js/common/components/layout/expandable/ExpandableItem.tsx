/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import Collapse from "@mui/material/Collapse";
import AccordionDetails from "@mui/material/AccordionDetails";
import { createStyles, withStyles } from "@mui/styles";
import { IconButton } from "@mui/material";
import { AppTheme } from "../../../../model/common/Theme";
import { stopEventPropagation } from "../../../utils/events";
import { IS_JEST } from "../../../../constants/EnvironmentConstants";

const styles = (theme: AppTheme) =>
  createStyles({
    expansionPanelRoot: {},
    expansionPanelExpanded: {
      "&:before": {
        content: "none"
      }
    },
    expansionPanelPaper: {
      margin: theme.spacing(-1, 0, 2, -1),
      padding: theme.spacing(1, 0, 0, 1),
      overflow: "hidden",
      "&:last-child": {
        marginBottom: 0
      }
    },
    expansionPanelNoPaper: {
      boxShadow: "none",
      background: "transparent"
    },
    expansionPanelSummayRoot: {
      alignItems: "flex-start",
      padding: 0
    },
    summaryContent: {
      "&, &$expansionPanelSummayContentExpanded": {
        margin: 0
      }
    },
    expansionPanelSummayContentExpanded: {},
    expansionPanelDetails: {
      marginTop: -54
    },
    expandIcon: {
      marginTop: "2px",
      marginRight: "unset",
    },
    collapseRoot: {},
  });

interface Props {
  expanded: boolean;
  keepPaper?: boolean;
  onChange?: any;
  classes?: any;
  collapsedContent?: React.ReactNode;
  buttonsContent?: React.ReactNode;
  detailsContent?: React.ReactNode;
  elevation?: number;
  expandButtonId?: string;
}

const ExpandableItem: React.FunctionComponent<Props> = props => {
  const {
    expanded, keepPaper, onChange, classes, collapsedContent, buttonsContent, detailsContent, elevation = 2, expandButtonId
  } = props;

  const buttonId = expandButtonId ? `expand-button-${expandButtonId}` : null;
  const iconButtonProps = IS_JEST ? {
    "data-testid": buttonId
  } : {};

  return (
    <Accordion
      expanded={expanded}
      TransitionProps={{ unmountOnExit: true, mountOnEnter: true }}
      classes={{
        root: clsx(classes.expansionPanelRoot,
          !expanded
          && clsx(classes.expansionPanelExpanded, keepPaper ? classes.expansionPanelPaper : classes.expansionPanelNoPaper))
      }}
      elevation={elevation}
    >
      <AccordionSummary
        classes={{
          root: classes.expansionPanelSummayRoot,
          content: classes.summaryContent,
          expanded: classes.expansionPanelSummayContentExpanded,
          expandIconWrapper: classes.expandIcon
        }}
        onClick={onChange}
        expandIcon={<IconButton id={buttonId} {...iconButtonProps}><ExpandMoreIcon /></IconButton>}
      >
        <Collapse in={!expanded} timeout="auto" mountOnEnter unmountOnExit classes={{ root: classes.collapseRoot }}>
          {collapsedContent}
        </Collapse>

        <div className="flex-fill" />

        {buttonsContent}
      </AccordionSummary>
      <AccordionDetails onClick={stopEventPropagation} className={classes.expansionPanelDetails}>
        {detailsContent}
      </AccordionDetails>
    </Accordion>
  );
};
export default withStyles(styles)(ExpandableItem);
