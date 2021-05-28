import React, {Component} from 'react';
import update from 'react-addons-update';
import {TextField} from "@material-ui/core";
import {Droppable} from "react-beautiful-dnd";
import clsx from "clsx";
import {withStyles} from "@material-ui/core/styles";
import Card from "./Card";

const styles = theme => ({
  themeSource: {
    height: "100%",
    // border: "1px solid gray",
    // border: "1px solid rgba(0, 0, 0, 0.12)",
    background: "transparent",
    overflowY: "auto",
    overflowX: "hidden",
    position: "static",
    transition: "all .3s",
  },
  placeholder: {
    position: "absolute",
    width: "100%",
    textAlign: "center",
    top: "50%",
    left: 0,
    marginTop: "-17px",
    fontSize: "34px",
    color: "gray",
    opacity: .3,
    pointerEvents: "none",
  },
  upperCaseAfter: {
    "&:after": {
      textTransform: "uppercase",
    },
  },
  blocks: {
    height: "100%",
  },
  activeBlock: {
    background: theme.palette.primary.main,
  },
  notActiveBlock: {
    background: "#fff",
  },
  sourceWrapper: {
    overflowY: "auto",
  }
});

interface Props {
  classes: any;
  id: string;
  cards: any[];
  showFilter?: boolean;
  saveBlock: (blockId, settings) => any;
  removeBlock: (index, sourceId) => any;
}

class Source extends Component<any, any> {
  constructor(props) {
    super(props);
    this.state = {
      cards: props.cards,
      filter: '',
    };
  }

  componentDidUpdate(prevProps: Readonly<any>, prevState: Readonly<any>, snapshot?: any) {
    if (prevProps.cards !== this.props.cards) {
      this.setState({ cards: this.props.cards });
    }
  }

  onChangeFilter(e) {
    this.setState({
      filter: e.target.value,
    });
  }

  render() {
    const {cards, filter} = this.state;
    const {classes, id, placeholder, className, showFilter, noUpperCase, removeBlock, saveBlock} = this.props;

    return (
      <Droppable droppableId={id}>
        {(provided, snapshot) => (
          <div ref={provided.innerRef} className={"h-100"}>
            <div className={"relative h-100"}>
              <div className={classes.placeholder}>
                {placeholder}
              </div>
              <div
                className={clsx(classes.themeSource, className === "blocks" && classes.blocks,
                  snapshot.isDraggingOver && classes.activeBlock || classes.notActiveBlock, !noUpperCase && classes.upperCaseAfter)}
              >
                {cards && cards.length > 0 && showFilter &&
                  <TextField
                    type="text"
                    name="filter"
                    placeholder="Filter"
                    id="filter"
                    className="w-100 pl-1 pr-1"
                    value={filter}
                    onChange={e => this.onChangeFilter(e)}
                  />
                }

                {cards && cards.filter(card => card.title.toLowerCase().indexOf(filter.toLowerCase()) !== -1).map((card, i) => (
                  card &&
                    <Card
                      key={card.id}
                      index={i}
                      listId={this.props.id}
                      card={card}
                      saveBlock={saveBlock}
                      removeCard={(index, sourceId) => removeBlock(index, sourceId)}
                    />
                  ),
                )}
              </div>
            </div>
          </div>
        )}
      </Droppable>
    );
  }
}


export default withStyles(styles as any)(Source);
