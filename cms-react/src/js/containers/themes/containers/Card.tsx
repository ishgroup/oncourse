import React, {Component} from 'react';
import {DragSource, DropTarget} from 'react-dnd';
import {findDOMNode} from 'react-dom';
import classnames from 'classnames';
import flow from 'lodash/flow';

class Card extends Component<any, any> {

  render() {
    const {card, isDragging, connectDragSource, connectDropTarget} = this.props;

    return connectDragSource(connectDropTarget(
      <div className={classnames('theme__card', {dragging: isDragging})}>
        {card.title}
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
    const hoverBoundingRect = findDOMNode(component).getBoundingClientRect();

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

export default flow<any>(
  DropTarget("CARD", cardTarget, connect => ({
    connectDropTarget: connect.dropTarget(),
  })),
  DragSource("CARD", cardSource, (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging(),
  })),
)(Card);
