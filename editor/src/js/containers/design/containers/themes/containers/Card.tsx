import React, {Component} from 'react';
import {DragSource, DropTarget} from 'react-dnd';
import {findDOMNode} from 'react-dom';
import DeleteIcon from '@material-ui/icons/Delete';
import clsx from "clsx";
import {IconButton} from "@material-ui/core";
import flow from 'lodash/flow';
import {withStyles} from "@material-ui/core/styles";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import {addContentMarker} from "../../../../content/utils";

const styles = theme => ({
  themeCard: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    border: "1px dashed",
    borderColor: theme.palette.primary.main,
    padding: "0.5rem 1rem",
    margin: ".5rem",
    background: "white",
    cursor: "move",
    opacity: 1,
    "&:hover": {
      "& $removeIcon": {
        display: "block",
      }
    }
  },
  dragging: {
    opacity: 0,
    maxWidth: "200px",
  },
  removeIcon: {
    fontSize: "16px",
    display: "none",
    color: "rgba(0, 0, 0, 0.2);",
    "&:hover": {
      cursor: "pointer",
    }
  },
});

class Card extends Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      title: props.card.title,
    };
  }

  onChange(event) {
    this.setState({
      title: event.target.value,
    });
  }

  onSave(block) {
    const { saveBlock } = this.props;

    saveBlock(block.id, {
      title: this.state.title,
      content: addContentMarker(block.content, block.contentMode),
    });
  }

  render() {
    const {
      card,
      classes,
      connectDragSource,
      connectDropTarget,
      index,
      isDragging,
      listId,
      removeCard,
    } = this.props;

    const isDefault = listId === "default";

    return connectDragSource(connectDropTarget(
      <div className={clsx(classes.themeCard, isDragging && classes.dragging)}>
        <EditInPlaceField
          // label="Block title"
          name="blockTitle"
          id="blockTitle"
          meta={{}}
          input={{
            onChange: e => this.onChange(e),
            onFocus: stubFunction,
            onBlur: () => this.onSave(card),
            value: this.state.title,
          }}
        />

        {!isDefault && (
          <IconButton size="small">
            <DeleteIcon className={classes.removeIcon} onClick={() => removeCard(index)}/>
          </IconButton>
        )}
      </div>,
    ));
  }
}

const cardSource = {
  beginDrag(props) {
    return {
      index: props.index,
      listId: props.listId,
      card: props.card,
    };
  },

  endDrag(props, monitor) {
    const item = monitor.getItem();
    const dropResult = monitor.getDropResult();

    if ( dropResult && dropResult.listId !== item.listId ) {
      props.removeCard(item.index);
    }
  },
};

const cardTarget = {
  hover(props, monitor, component) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;
    const sourceListId = monitor.getItem().listId;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }

    // Determine rectangle on screen
    const node = findDOMNode(component) as Element;
    const hoverBoundingRect = node.getBoundingClientRect();

    // Get vertical middle
    const hoverMiddleY = (hoverBoundingRect.bottom - hoverBoundingRect.top) / 2;

    // Determine mouse position
    const clientOffset = monitor.getClientOffset();

    // Get pixels to the top
    const hoverClientY = clientOffset.y - hoverBoundingRect.top;

    // Only perform the move when the mouse has crossed half of the items height
    // When dragging downwards, only move when the cursor is below 50%
    // When dragging upwards, only move when the cursor is above 50%

    // Dragging downwards
    if (dragIndex < hoverIndex && hoverClientY < hoverMiddleY) {
      return;
    }

    // Dragging upwards
    if (dragIndex > hoverIndex && hoverClientY > hoverMiddleY) {
      return;
    }

    // Time to actually perform the action
    if ( props.listId === sourceListId ) {
      props.moveCard(dragIndex, hoverIndex);

      // Note: we're mutating the monitor item here!
      // Generally it's better to avoid mutations,
      // but it's good here for the sake of performance
      // to avoid expensive index searches.
      monitor.getItem().index = hoverIndex;
    }
  },
};

export default flow<any, any, any>(
  DropTarget("CARD", cardTarget, connect => ({
    connectDropTarget: connect.dropTarget(),
  })),
  DragSource("CARD", cardSource, (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging(),
  })),
)(withStyles(styles)(Card));
