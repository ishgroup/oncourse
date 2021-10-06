/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import Collapse from "@mui/material/Collapse";
import AccordionDetails from "@mui/material/AccordionDetails";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import { AppTheme } from "../../../../model/common/Theme";
import { stopEventPropagation } from "../../../utils/events";

const styles = (theme: AppTheme) =>
  createStyles({
    expansionPanelRoot: {
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
      marginRight: "unset",
      "&:hover": {
        backgroundColor: theme.palette.action.hover
      }
    }
  });

interface Props {
  expanded: boolean;
  keepPaper?: boolean;
  onChange?: any;
  classes?: any;
  collapsedContent?: React.ReactNode;
  buttonsContent?: React.ReactNode;
  detailsContent?: React.ReactNode;
}

const ExpandableItem: React.FunctionComponent<Props> = props => {
  const {
 expanded, keepPaper, onChange, classes, collapsedContent, buttonsContent, detailsContent
} = props;

  return (
    <Accordion
      expanded={expanded}
      TransitionProps={{ unmountOnExit: true, mountOnEnter: true }}
      classes={{
        root:
          !expanded
          && clsx(classes.expansionPanelRoot, keepPaper ? classes.expansionPanelPaper : classes.expansionPanelNoPaper)
      }}
    >
      <AccordionSummary
        classes={{
          root: classes.expansionPanelSummayRoot,
          content: classes.summaryContent,
          expanded: classes.expansionPanelSummayContentExpanded,
          expandIconWrapper: classes.expandIcon
        }}
        onClick={onChange}
        expandIcon={<ExpandMoreIcon />}
      >
        <Collapse in={!expanded} timeout="auto" mountOnEnter unmountOnExit>
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
