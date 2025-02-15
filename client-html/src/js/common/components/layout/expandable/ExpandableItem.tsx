/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { IconButton } from '@mui/material';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Collapse from '@mui/material/Collapse';
import clsx from 'clsx';
import { AppTheme, stopEventPropagation } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import { IS_JEST } from '../../../../constants/EnvironmentConstants';

const styles = (theme: AppTheme, p, classes) =>
  ({
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
      [`&, &.${classes.expansionPanelSummayContentExpanded}`]: {
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
    expanded,
    keepPaper,
    onChange,
    classes,
    collapsedContent,
    buttonsContent,
    detailsContent,
    elevation = 2,
    expandButtonId
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
        expandIcon={<IconButton id={buttonId} {...iconButtonProps}><ExpandMoreIcon/></IconButton>}
      >
        <Collapse in={!expanded} timeout="auto" mountOnEnter unmountOnExit classes={{ root: classes.collapseRoot }}>
          {collapsedContent}
        </Collapse>

        <div className="flex-fill"/>

        {buttonsContent}
      </AccordionSummary>
      <AccordionDetails onClick={stopEventPropagation} className={classes.expansionPanelDetails}>
        {detailsContent}
      </AccordionDetails>
    </Accordion>
  );
};
export default withStyles(ExpandableItem, styles);
