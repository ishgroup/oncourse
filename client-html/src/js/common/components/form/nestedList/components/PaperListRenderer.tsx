import Delete from "@mui/icons-material/Delete";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { Checkbox, Collapse, List, ListItem, ListItemText } from "@mui/material";
import IconButton from "@mui/material/IconButton";
import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import clsx from "clsx";
import { openInternalLink } from "ish-ui";
import React, { PureComponent } from "react";
import { NestedListItem, NestedListPanelItem } from "../NestedList";

const styles = theme =>
  createStyles({
    root: {
      display: "grid",
      gridTemplateColumns: "repeat(2, 1fr)",
      gridGap: theme.spacing(1)
    },
    root__item: {
      padding: theme.spacing(1)
    },
    primaryLine: {
      display: "flex",
      justifyContent: "space-between",
      alignItems: "flex-start"
    },
    deleteButton: {
      margin: `-${theme.spacing(1)} -${theme.spacing(1)} -${theme.spacing(1)} 0`
    },
    fade: {
      opacity: 0.5
    },
    expand: {
      padding: 0,
      transform: "rotate(0deg)",
      marginLeft: "auto",
      marginRight: theme.spacing(0.5),
      transition: theme.transitions.create("transform", {
        duration: theme.transitions.duration.shortest
      })
    },
    expandOpen: {
      transform: "rotate(180deg)"
    },
    inlinePanelCaption: {
      lineHeight: "1.9em",
      paddingLeft: "1em"
    },
    panelFixedHeight: {
      maxHeight: "200px",
      overflow: "auto"
    },
    listItemCheckbox: {
      padding: 0,
      marginRight: theme.spacing(1),
      "& svg": {
        fontSize: "18px"
      }
    },
    listItemText: {
      fontSize: "18px",
      margin: 0
    },
    listItem: {
      padding: 0
    },
    list: {
      paddingLeft: "3px"
    }
  });

interface PaperListRendererProps {
  items: NestedListItem[];
  onDelete: (item: NestedListItem, index: number) => void;
  fade: boolean;
  disabled?: boolean;
  classes?: any;
  inlineSecondaryText?: boolean;
  panelItems?: NestedListPanelItem[];
  panelCaption?: string;
  onChangePanelItem?: (message: PanelItemChangedMessage) => void;
}

interface PaperPanelProps {
  panelItems?: NestedListPanelItem[];
  onChangePanelItem?: (message: PanelItemChangedMessage) => void;
  item?: NestedListItem;
  panelCaption?: string;
  classes?: any;
}

interface PaperPanelState {
  panelExpanded?: boolean;
}

export interface PanelItemChangedMessage {
  panelItemId?: number;
  checked?: boolean;
  item?: NestedListItem;
}

const panelCaptionMaximumLength = 70;

class PaperPanel extends PureComponent<PaperPanelProps, PaperPanelState> {
  constructor(props) {
    super(props);
    this.state = {
      panelExpanded: false
    };
  }

  handleExpandClick = () => {
    this.setState({panelExpanded: !this.state.panelExpanded});
  };

  private expandCaption = (items: number[]): string => {
    const {panelCaption, panelItems} = this.props;
    return items.reduce((caption, pi, i) => {
      if (caption.length === panelCaptionMaximumLength) {
        return caption;
      }
      const panelItem = panelItems.find(el => el.id === pi);

      const newCaption = panelItem
        ? caption + panelItem.description + (i < items.length - 1 ? ", " : "")
        : "";
      if (newCaption.length > panelCaptionMaximumLength) {
        return newCaption.substr(0, panelCaptionMaximumLength - 3) + "...";
      }
      return newCaption;
    }, panelCaption + (items.length > 0 ? ": " : ""));
  };

  isListItemSelected = (panelItemValue, item) => {
    if (!item.panelItemIds) {
      return false;
    }
    return !!item.panelItemIds.find(el => el === panelItemValue.id);
  };

  render() {
    const {
      panelItems, panelCaption, item, onChangePanelItem, classes
    } = this.props;

    const {panelExpanded} = this.state;

    return (
      <>
        <IconButton
          className={clsx(classes.expand, {
            [classes.expandOpen]: panelExpanded
          })}
          onClick={this.handleExpandClick}
        >
          <ExpandMoreIcon/>
        </IconButton>
        <Typography variant="caption" className="linkDecoration" onClick={this.handleExpandClick}>
          {panelExpanded ? panelCaption : this.expandCaption(item.panelItemIds ? item.panelItemIds : [])}
        </Typography>
        <Collapse in={panelExpanded} unmountOnExit>
          <div className={classes.panelFixedHeight}>
            <List dense disablePadding className={classes.list}>
              {panelItems.map((panelItemValue: NestedListPanelItem, id) => (
                <ListItem key={id} disableGutters className={classes.listItem}>
                  <Checkbox
                    onChange={(_, checked) => onChangePanelItem({panelItemId: panelItemValue.id, checked, item})}
                    checked={this.isListItemSelected(panelItemValue, item)}
                    className={classes.listItemCheckbox}
                  />
                  <ListItemText
                    id={id.toString()}
                    primary={panelItemValue.description}
                    className={classes.listItemText}
                  />
                </ListItem>
              ))}
            </List>
          </div>
        </Collapse>
      </>
    );
  }
}

class PaperListRenderer extends PureComponent<PaperListRendererProps, any> {
  render() {
    const {
      classes,
      items,
      onDelete,
      fade,
      inlineSecondaryText,
      panelItems,
      panelCaption,
      onChangePanelItem,
      disabled
    } = this.props;

    return (
      <div
        className={clsx(classes.root, {
          [classes.fade]: fade || disabled,
          "pb-3": items.length,
          "pb-1": !items.length
        })}
      >
        {items.map((item, index) => (
          <Paper key={index} className={classes.root__item}>
            <div className="d-flex">
              <div className={classes.primaryLine}>
                <Typography variant="body2" className="linkDecoration" onClick={() => openInternalLink(item.link)}>
                  {item.primaryText}
                </Typography>
                {inlineSecondaryText && (
                  <Typography variant="caption" className={classes.inlinePanelCaption}>
                    {item.secondaryText}
                  </Typography>
                )}
              </div>

              <div className="flex-fill"/>

              {onDelete && (
                <IconButton
                  className={clsx("lightGrayIconButton", classes.deleteButton, disabled && "invisible")}
                  children={<Delete fontSize="inherit" color="inherit"/>}
                  color="secondary"
                  onClick={() => onDelete(item, index)}
                />
              )}
            </div>

            {!inlineSecondaryText && <Typography variant="caption">{item.secondaryText}</Typography>}

            {panelItems && (
              <PaperPanel
                item={item}
                onChangePanelItem={onChangePanelItem}
                panelItems={panelItems}
                panelCaption={panelCaption}
                classes={classes}
              />
            )}
          </Paper>
        ))}
      </div>
    );
  }
}

export default withStyles(styles)(PaperListRenderer);
