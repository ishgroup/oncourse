import React, {Component} from 'react';
import update from 'react-addons-update';
import Card from "./Card";
import {DropTarget} from 'react-dnd';
import {TextField} from "@material-ui/core";
import clsx from "clsx";
import {withStyles} from "@material-ui/core/styles";

const styles = theme => ({
  themeSource: {
    height: "200px",
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
    height: "632px",
  },
  activeBlock: {
    background: theme.palette.primary.main,
  },
  notActiveBlock: {
    background: "#fff",
  }
});

interface Props {
  classes: any;
  list: any[];
  showFilter?: boolean;
}

class Source extends Component<any, any> {
  constructor(props) {
    super(props);
    this.state = {
      cards: props.list,
      filter: '',
    };
  }

  componentDidUpdate(prevProps: Readonly<any>, prevState: Readonly<any>, snapshot?: any) {
    if (prevProps.list !== this.props.list) {
      this.setState({ cards: this.props.list });
    }
  }

  pushCard(card) {
    const {id} = this.props;
    this.setState(update(this.state, {
      cards: {
        $push: [card],
      },
    }));

    this.props.onUpdate && this.props.onUpdate(id, this.state.cards);
  }

  removeCard(index) {
    const {id} = this.props;

    this.setState(update(this.state, {
      cards: {
        $splice: [
          [index, 1],
        ],
      },
    }), () => {
      this.props.onUpdate && this.props.onUpdate(id, this.state.cards);
    });
  }

  moveCard(dragIndex, hoverIndex) {
    const {id} = this.props;
    const {cards} = this.state;
    const dragCard = cards[dragIndex];

    this.setState(update(this.state, {
      cards: {
        $splice: [
          [dragIndex, 1],
          [hoverIndex, 0, dragCard],
        ],
      },
    }));

    this.props.onUpdate && this.props.onUpdate(id, this.state.cards);
  }

  onChangeFilter(e) {
    this.setState({
      filter: e.target.value,
    });
  }

  render() {
    const {cards, filter} = this.state;
    const {canDrop, classes, isOver, connectDropTarget, placeholder, className, showFilter, noUpperCase} = this.props;
    const isActive = canDrop && isOver;

    return connectDropTarget(
      <div className="relative">
        <div className={classes.placeholder}>
          {placeholder}
        </div>
        <div
          className={clsx(classes.themeSource, className === "blocks" && classes.blocks,
            isActive && classes.activeBlock || classes.notActiveBlock, !noUpperCase && classes.upperCaseAfter)}
          // data-placeholder={placeholder}
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

          {cards.filter(card => card.title.toLowerCase().indexOf(filter.toLowerCase()) !== -1).map((card, i) => (
            card &&
              <Card
                key={card.id}
                index={i}
                listId={this.props.id}
                card={card}
                removeCard={this.removeCard.bind(this)}
                moveCard={this.moveCard.bind(this)}
              />
            ),
          )}
        </div>
      </div>,
    );
  }
}

const cardTarget = {
  drop(props, monitor, component) {
    const {id} = props;
    const sourceObj = monitor.getItem();
    if (id !== sourceObj.listId) component.pushCard(sourceObj.card);
    return {
      listId: id,
    };
  },
};

export default DropTarget("CARD", cardTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
  canDrop: monitor.canDrop(),
}))(withStyles(styles as any)(Source));
