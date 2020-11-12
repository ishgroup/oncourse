/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Collapse from "@material-ui/core/Collapse";
import ExpansionPanelDetails from "@material-ui/core/ExpansionPanelDetails";
import { createStyles, withStyles } from "@material-ui/core/styles";
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
    <ExpansionPanel
      expanded={expanded}
      TransitionProps={{ unmountOnExit: true, mountOnEnter: true }}
      classes={{
        root:
          !expanded
          && clsx(classes.expansionPanelRoot, keepPaper ? classes.expansionPanelPaper : classes.expansionPanelNoPaper)
      }}
    >
      <ExpansionPanelSummary
        classes={{
          root: classes.expansionPanelSummayRoot,
          content: classes.summaryContent,
          expanded: classes.expansionPanelSummayContentExpanded,
          expandIcon: classes.expandIcon
        }}
        onClick={onChange}
        expandIcon={<ExpandMoreIcon />}
      >
        <Collapse in={!expanded} timeout="auto" mountOnEnter unmountOnExit>
          {collapsedContent}
        </Collapse>

        <div className="flex-fill" />

        {buttonsContent}
      </ExpansionPanelSummary>
      <ExpansionPanelDetails onClick={stopEventPropagation} className={classes.expansionPanelDetails}>
        {detailsContent}
      </ExpansionPanelDetails>
    </ExpansionPanel>
  );
};
export default withStyles(styles)(ExpandableItem);
