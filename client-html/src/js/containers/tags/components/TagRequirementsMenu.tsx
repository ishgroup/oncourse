import React, { ComponentClass } from "react";
import { connect } from "react-redux";
import {
  Typography, Menu, MenuItem
} from "@mui/material";
import { withStyles } from "@mui/styles";
import { TagRequirement, TagRequirementType } from "@api/model";
import clsx from "clsx";
import GetTagRequirementDisplayName from "../utils/GetTagRequirementDisplayName";
import { State } from "../../../reducers/state";
import AddIcon from "../../../common/components/icons/AddIcon";

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
      allTags,
      input: { onChange },
      items,
      rootID
    } = this.props;

    const currentTagRequirements = allTags.find(t => t.id === rootID)?.requirements;
    if (currentTagRequirements && currentTagRequirements.length) {
      const foundedTagRequirement = currentTagRequirements.find(r => r.type === item.type);

      if (foundedTagRequirement) {
        item.id = foundedTagRequirement.id;
      } else {
        item.id = null;
      }
    }

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

            <AddIcon
              aria-owns={anchorEl ? "field-types-menu" : null}
              aria-haspopup="true"
              onClick={this.handleAddFieldClick}
              disabled={disabled || !(filteredItems && filteredItems.length)}
              classes={{
                disabled: "disabled",
                root: system ? "invisible" : undefined
              }}
            />
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

const mapStateToProps = (state: State) => ({
  allTags: state.tags.allTags,
});

export default connect(mapStateToProps, null)(withStyles(styles)(TagRequirementsMenu) as ComponentClass<any>);
