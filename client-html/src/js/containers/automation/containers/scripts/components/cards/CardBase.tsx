import * as React from "react";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import DragIndicator from "@mui/icons-material/DragIndicator";
import Typography from "@mui/material/Typography";
import { withStyles, createStyles } from "@mui/styles";
import IconButton from "@mui/material/IconButton";
import Delete from "@mui/icons-material/Delete";
import clsx from "clsx";
import AddButton from "../../../../../../common/components/icons/AddButton";

const styles = theme =>
  createStyles({
    panelRoot: {
      "&:before": {
        content: "none"
      },
      "&$panelExpandedWithoutMargin": {
        "&:last-child": {
          marginTop: theme.spacing(3),
          marginBottom: 0
        }
      },
      "&$panelExpanded": {
        "&:last-child": {
          marginBottom: theme.spacing(3)
        }
      }
    },
    panelExpanded: {},
    panelExpandedWithoutMargin: {},
    panelDetailsRoot: {
      "&$panelDetailsPadding": {
        padding: theme.spacing(0, 3)
      },
      "&.p-0": {
        padding: 0
      }
    },
    panelDetailsPadding: {},
    collapse: {
      overflow: "visible"
    },
    summaryRoot: {
      borderBottom: "1px solid transparent",
      padding: theme.spacing(0, 0, 0, 2)
    },
    summaryExpanded: {
      borderBottomColor: `${theme.palette.divider}`
    },
    summaryExpandIcon: {
      "&:hover": {
        backgroundColor: theme.palette.action.hover
      }
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
    onDetailsClick
  } = props;

  return (
    <Accordion
      classes={{
        root: classes.panelRoot,
        expanded: disableExpandedBottomMargin ? classes.panelExpandedWithoutMargin : classes.panelExpanded
      }}
      className={className}
      defaultExpanded={expanded}
    >
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        classes={{
          root: classes.summaryRoot,
          expanded: classes.summaryExpanded,
          expandIconWrapper: clsx("p-0-5 mr-2", classes.summaryExpandIcon)
        }}
      >
        <div className="flex-fill centeredFlex" {...dragHandlerProps}>
          <div>
            <Typography className="heading" component="div">
              {heading}
            </Typography>
            <Typography variant="subtitle1" color="textSecondary">
              {details}
            </Typography>
          </div>
          {Boolean(dragHandlerProps) && <DragIndicator color="disabled" />}
          {Boolean(onAddItem) && (
            <AddButton className="addButtonColor fs2 p-1" onClick={onAddItem} iconFontSize="inherit" />
          )}
        </div>
        <div>
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

export default withStyles(styles)(CardBase);
