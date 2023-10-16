/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Tree from "@atlaskit/tree";
import { Props } from "@atlaskit/tree/dist/types/components/Tree/Tree-types";
import { createStyles } from "@mui/material";
import withStyles from "@mui/styles/withStyles";
import { AppTheme } from "ish-ui";
import isEmpty from "lodash.isempty";
import * as React from "react";
import * as ReactBeautifulDnD from "react-beautiful-dnd-next";

const TAG_ITEMS_SPACING = 8;

const getDraggedDom = draggableId => document.querySelector(`[data-draggable-id='${draggableId}']`) as any;

const styles = (theme: AppTheme) => createStyles({
  placeholder: {
    border: `2px dashed ${theme.palette.action.focus}`,
    borderRadius: `${theme.shape.borderRadius}px`,
    position: "absolute",
    boxSizing: "border-box",
    zIndex: 0
  }
})

class PatchTree extends Tree {
  setPlaceholderProps = placeholderProps => {
    this.setState({
      placeholderProps
    } as any);
  }

  onDragStartCustom = event => {
    const draggedDOM = getDraggedDom(event.draggableId);

    if (!draggedDOM) {
      return;
    }

    const { clientHeight, clientWidth } = draggedDOM;

    const sourceIndex = event.source.index;

    const paddingLeft = parseFloat(
      window.getComputedStyle(draggedDOM).paddingLeft
    );

    const prevNode = draggedDOM.parentNode.children[sourceIndex - 1];

    const clientY = prevNode ? prevNode.offsetTop + prevNode.clientHeight + TAG_ITEMS_SPACING : TAG_ITEMS_SPACING;

    this.setPlaceholderProps({
      clientHeight: clientHeight - TAG_ITEMS_SPACING,
      clientWidth: clientWidth - paddingLeft,
      clientY,
      clientX: paddingLeft
    });
  };

  onDragUpdateCustom = event => {
    if (!event.destination) {
      return;
    }

    const draggedDOM = getDraggedDom(event.draggableId);

    if (!draggedDOM) {
      return;
    }

    const destinationIndex = event.destination.index;
    const sourceIndex = event.source.index;

    const childrenArray = [...draggedDOM.parentNode.children];

    const targetDOM = childrenArray[destinationIndex > sourceIndex ? destinationIndex + 1 : destinationIndex];

    const { clientWidth, offsetTop } = targetDOM;

    const clientY = sourceIndex === destinationIndex
      ? sourceIndex > 0
        ? (childrenArray[sourceIndex - 1].offsetTop + childrenArray[sourceIndex - 1].clientHeight + TAG_ITEMS_SPACING)
        : TAG_ITEMS_SPACING
      : offsetTop + TAG_ITEMS_SPACING;

    let paddingLeft = parseFloat(
      window.getComputedStyle(targetDOM).paddingLeft
    );

    if (destinationIndex > 0 && destinationIndex !== sourceIndex) {
      const prevPadding = parseFloat(
        window.getComputedStyle(childrenArray[destinationIndex - 1]).paddingLeft
      );
      if (prevPadding > paddingLeft) {
        paddingLeft = prevPadding;
      }
    }

    this.setPlaceholderProps({
      clientHeight: draggedDOM.clientHeight - TAG_ITEMS_SPACING,
      clientWidth: clientWidth - paddingLeft,
      clientY,
      clientX: paddingLeft
    });
  };
  
  render() {
    const { isNestingEnabled, classes } = this.props as any;
    const { placeholderProps } = this.state as any;
    const renderedItems = this.renderItems();

    return (
      <ReactBeautifulDnD.DragDropContext
        onDragStart={e => {
          this.onDragStartCustom(e);
          this.onDragStart(e);
        }}
        onDragEnd={this.onDragEnd}
        onDragUpdate={e => {
          this.onDragUpdateCustom(e);
          this.onDragUpdate(e);
        }}
      >
        <ReactBeautifulDnD.Droppable
          droppableId="tree"
          isCombineEnabled={isNestingEnabled}
          ignoreContainerClipping
        >
          {(dropProvided, snapshot) => {
            const finalProvided = this.patchDroppableProvided(dropProvided);

            return (
              <div
                ref={finalProvided.innerRef}
                style={{ pointerEvents: "auto", position: "relative" }}
                onTouchMove={this.onPointerMove}
                onMouseMove={this.onPointerMove}
                {...finalProvided.droppableProps}
              >
                {renderedItems}
                {dropProvided.placeholder}
                {!isEmpty(placeholderProps) && snapshot.isDraggingOver && (
                  <div
                    className={classes.placeholder}
                    style={{
                      top: placeholderProps.clientY,
                      left: placeholderProps.clientX,
                      height: placeholderProps.clientHeight,
                      width: placeholderProps.clientWidth
                    }}
                  />
                )}
              </div>
            );
          }}
        </ReactBeautifulDnD.Droppable>
      </ReactBeautifulDnD.DragDropContext>
    );
  }
}

export default withStyles(styles)(PatchTree) as React.ClassicComponentClass<Partial<Props>>;