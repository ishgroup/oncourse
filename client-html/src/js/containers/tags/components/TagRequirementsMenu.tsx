import React from "react";
import { connect } from "react-redux";
import {
  Typography, Menu, MenuItem
} from "@mui/material";
import { TagRequirement, TagRequirementType } from "@api/model";
import clsx from "clsx";
import GetTagRequirementDisplayName from "../utils/GetTagRequirementDisplayName";
import { State } from "../../../reducers/state";
import AddButton from "../../../common/components/icons/AddButton";

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

class TagRequirementsMenu extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      anchorEl: null
    };
  }

  handleAddFieldClick = e => {
    this.setState({ anchorEl: e.target });
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
    const filtered = items.filter(i => {
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

    filtered.sort((a, b) => (GetTagRequirementDisplayName(a.type) > GetTagRequirementDisplayName(b.type) ? 1 : -1));

    return filtered;
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

    onChange([...items, item]);
  };

  render() {
    const { anchorEl, filteredItems } = this.state;
    const {
      label,
      input: { name },
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
          >
            {filteredItems.map((i, n) => (
              <MenuItem key={n} onClick={() => this.addRequirement(i)}>
                {GetTagRequirementDisplayName(i.type)}
              </MenuItem>
            ))}
          </Menu>
        )}

        <div id={name}>
          <div className="centeredFlex">
            <Typography
              className={clsx("heading", {
                "errorColor": invalid
              })}
            >
              {label}
            </Typography>

            <AddButton
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

          {typeof error === "string" && (
            <Typography className="shakingError" variant="caption" color="error" component="div">
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

export default connect(mapStateToProps)(TagRequirementsMenu);
