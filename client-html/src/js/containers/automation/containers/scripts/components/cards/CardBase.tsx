import Delete from '@mui/icons-material/Delete';
import DragIndicator from '@mui/icons-material/DragIndicator';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import { AddButton } from 'ish-ui';
import * as React from 'react';
import { withStyles } from 'tss-react/mui';

const styles = (theme, p, classes) =>
  ({
    panelRoot: {
      "&:before": {
        content: "none"
      },
      [`&.${classes.panelExpandedWithoutMargin}`]: {
        "&:last-child": {
          marginBottom: 0
        }
      },
      [`&.${classes.panelExpanded}`]: {
        "&:last-child": {
          marginBottom: theme.spacing(0)
        }
      }
    },
    panelExpanded: {},
    panelExpandedWithoutMargin: {},
    panelDetailsRoot: {
      [`&.${classes.panelDetailsPadding}`]: {
        padding: theme.spacing(0, 3)
      },
      "&.p-0": {
        padding: 0
      }
    },
    panelDetailsPadding: {},
    noHoverEffect: {},
    collapse: {
      overflow: "visible"
    },
    summaryRoot: {
      borderBottom: "1px solid transparent",
      padding: theme.spacing(0, 0, 0, 2),
      [`&.${classes.noHoverEffect}:hover`]: {
        cursor: "inherit"
      }
    },
    summaryExpanded: {
      borderBottomColor: `${theme.palette.divider}`
    },
    summaryExpandIcon: {
      borderRadius: "50%",
      "&:hover": {
        backgroundColor: theme.palette.action.hover
      }
    },
    summaryContent: {
      maxWidth: "calc(100% - 48px)"
    },
    summaryContentInner: {
      maxWidth: "calc(100% - 32px)"
    }
  });

const CardBase = props => {
  const {
    classes,
    children,
    heading,
    details,
    className,
    expanded,
    noPadding,
    onDelete,
    dragHandlerProps,
    disableExpandedBottomMargin,
    onAddItem,
    onDetailsClick,
    customHeading,
    customButtons,
    onExpand
  } = props;

  // @ts-ignore
  // @ts-ignore
  // @ts-ignore
  return (
    <Accordion
      classes={{
        root: classes.panelRoot,
        expanded: disableExpandedBottomMargin ? classes.panelExpandedWithoutMargin : classes.panelExpanded
      }}
      className={className}
      expanded={onExpand ? expanded : true}
    >
      <AccordionSummary
        expandIcon={onExpand ? <ExpandMoreIcon /> : null}
        classes={{
          root: clsx(classes.summaryRoot, !onExpand && clsx("p-0", classes.noHoverEffect)),
          expanded: classes.summaryExpanded,
          expandIconWrapper: clsx("p-0-5 mr-2", classes.summaryExpandIcon),
          content: classes.summaryContent
        }}
        onClick={onExpand}
      >
        <div className={clsx("flex-fill centeredFlex w-100", classes.summaryContentInner)} {...dragHandlerProps}>
          {Boolean(dragHandlerProps) && <DragIndicator color="disabled" />}
          {customHeading ? heading : (
            <div>
              <Typography className="heading" component="div">
                {heading}
              </Typography>
              <Typography variant="subtitle1" color="textSecondary">
                {details}
              </Typography>
            </div>
            )}
          {Boolean(onAddItem) && (
            <AddButton className="addButtonColor fs2 p-1" onClick={onAddItem} iconFontSize="inherit" />
            )}
        </div>
        <div>
          {customButtons}
          {Boolean(onDelete) && (
          <IconButton className="fs2 p-1" onClick={onDelete} data-component={heading}>
            <Delete fontSize="inherit" />
          </IconButton>
            )}
        </div>
      </AccordionSummary>
      <AccordionDetails
        onClick={onDetailsClick}
        classes={{
            root: clsx("relative", classes.panelDetailsRoot)
          }}
        className={noPadding ? "p-0" : classes.panelDetailsPadding}
      >
        {children}
      </AccordionDetails>
    </Accordion>
  );
};

export default withStyles(CardBase, styles);
