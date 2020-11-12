import React from "react";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import MenuItem from "@material-ui/core/MenuItem";
import List from "@material-ui/core/List";
import AddTagMenuItem from "./AddTagMenuItem";
import { MenuTag } from "../../../../model/tags";

const styles = theme => ({
  listItem: {
    padding: theme.spacing(0, 0.5, 0, 2)
  },
  proceedIcon: {
    marginTop: "2px"
  },
  backContainer: {
    marginLeft: "-6px"
  }
});

interface Props {
  classes?: any;
  tags: MenuTag[];
  handleAdd: (tag: MenuTag) => void;
}

interface State {
  activeTag: MenuTag;
}

class AddTagMenu extends React.Component<Props, State> {
  state = {
    activeTag: null
  };

  setActiveTag = activeTag => {
    this.setState({
      activeTag
    });
  };

  handleBack = () => {
    this.setState(prev => ({
      activeTag: prev.activeTag.parent
    }));
  }

  render() {
    const {
     classes, tags, handleAdd
    } = this.props;

    const { activeTag } = this.state;

    return (
      <List
        className={classes.rootMenu}
      >
        {activeTag && (
          <MenuItem button onClick={this.handleBack} className={classes.listItem}>
            <span className={clsx("d-flex textSecondaryColor", classes.backContainer)}>
              <KeyboardArrowLeft color="inherit" />
              Back
            </span>
          </MenuItem>
        )}
        {(activeTag ? activeTag.children : tags).map(t => (
          <AddTagMenuItem
            key={t.tagBody.id}
            tag={t}
            classes={classes}
            setActiveTag={this.setActiveTag}
            handleAdd={handleAdd}
          />
        ))}
      </List>
    );
  }
}

export default withStyles(styles)(AddTagMenu);
