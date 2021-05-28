import React, {Component} from 'react';
import DeleteIcon from '@material-ui/icons/Delete';
import {Draggable} from "react-beautiful-dnd";
import clsx from "clsx";
import {IconButton} from "@material-ui/core";
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
      index,
      listId,
      removeCard,
    } = this.props;

    const isDefault = listId === "default";

    return (
      <Draggable
        key={card.id}
        draggableId={card.id.toString()}
        index={index}>
        {(provided) => (
          <div
            ref={provided.innerRef}
            {...provided.draggableProps}
            {...provided.dragHandleProps}
          >
            <div className={clsx(classes.themeCard)}>

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
                  <DeleteIcon className={classes.removeIcon} onClick={() => removeCard(index, listId)}/>
                </IconButton>
              )}
            </div>
          </div>
        )}
      </Draggable>
    );
  }
}

export default withStyles(styles)(Card);
