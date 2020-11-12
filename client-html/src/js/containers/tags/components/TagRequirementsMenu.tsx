import React, { ComponentClass } from "react";
import {
  withStyles, Typography, Menu, MenuItem
} from "@material-ui/core";
import { TagRequirement, TagRequirementType } from "@api/model";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import clsx from "clsx";
import GetTagRequirementDisplayName from "../utils/GetTagRequirementDisplayName";

const requirements = Object.keys(TagRequirementType).map(
  (i: TagRequirementType) =>
    ({
      id: null,
      type: i,
      mandatory: false,
      limitToOneTag: false,
      system: false
    } as TagRequirement)
);

const styles = theme => ({
  menu: {
    marginLeft: "52px"
  }
});

class TagRequirementsMenu extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      anchorEl: null
    };
  }

  handleAddFieldClick = e => {
    this.setState({ anchorEl: e.currentTarget });
  };

  handleAddFieldClose = () => {
    this.setState({ anchorEl: null });
  };

  setItemsProps(items) {
    const withId = items.map(r => {
      const match = this.props.items.find(i => i.type === r.type);
      if (match) {
        r.id = match.id;
        r.system = match.system;
        return r;
      }
      return r;
    });

    this.setState({
      itemsBase: withId,
      filteredItems: this.filterItems(withId)
    });
  }

  filterItems(items) {
    return items.filter(i => {
      const isAdded = Boolean(this.props.items.find(r => i.type === r.type));

      let isBinded;

      if (i.type === "Students" || i.type === "Tutors") {
        isBinded = Boolean(this.props.items.find(r => r.type === "Contacts"));
      }

      if (i.type === "Contacts") {
        isBinded = Boolean(this.props.items.find(r => r.type === "Students" || r.type === "Tutors"));
      }

      if (i.type === "Courses") {
        isBinded = Boolean(this.props.items.find(r => r.type === "Classes"));
      }

      if (i.type === "Classes") {
        isBinded = Boolean(this.props.items.find(r => r.type === "Courses"));
      }

      return isAdded ? false : !isBinded;
    });
  }

  componentDidUpdate({ rootID, items }) {
    if (items !== this.props.items) {
      const filtered = this.filterItems(this.state.itemsBase);

      this.setState({
        filteredItems: filtered
      });

      if (!filtered.length) {
        this.handleAddFieldClose();
      }
    }
    if (rootID !== this.props.rootID) {
      this.setItemsProps(JSON.parse(JSON.stringify(requirements)));
    }
  }

  componentDidMount() {
    this.setItemsProps(JSON.parse(JSON.stringify(requirements)));
  }

  addRequirement = item => {
    const {
      input: { onChange },
      items
    } = this.props;

    onChange([item, ...items]);
  };

  render() {
    const { anchorEl, filteredItems } = this.state;
    const {
      classes,
      label,
      meta: { invalid, error },
      disabled,
      system
    } = this.props;

    return (
      <>
        {filteredItems && Boolean(filteredItems.length) && (
          <Menu
            id="field-types-menu"
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={this.handleAddFieldClose}
            className={classes.menu}
          >
            {filteredItems.map((i, n) => (
              <MenuItem key={n} onClick={() => this.addRequirement(i)}>
                {GetTagRequirementDisplayName(i.type)}
              </MenuItem>
            ))}
          </Menu>
        )}

        <div>
          <div className="centeredFlex">
            <Typography
              className={clsx("heading", {
                "errorColor": invalid
              })}
            >
              {label}
            </Typography>

            <IconButton
              aria-owns={anchorEl ? "field-types-menu" : null}
              aria-haspopup="true"
              onClick={this.handleAddFieldClick}
              disabled={disabled || !(filteredItems && filteredItems.length)}
              classes={{
                disabled: "disabled",
                root: system ? "invisible" : undefined
              }}
            >
              <AddCircle className="addButtonColor" width={20} />
            </IconButton>
          </div>

          {error && (
            <Typography variant="caption" color="error">
              {error}
            </Typography>
          )}
        </div>
      </>
    );
  }
}

export default withStyles(styles)(TagRequirementsMenu) as ComponentClass<any>;
