import React from "react";
import clsx from "clsx";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";
import Checkbox from "@material-ui/core/Checkbox";
import MenuItem from "@material-ui/core/MenuItem";
import { MenuTag } from "../../../../model/tags";

interface Props {
  classes: any;
  setActiveTag: (tag: MenuTag) => void;
  handleAdd: (tag: MenuTag) => void;
  tag: MenuTag;
}

class AddTagMenuItem extends React.PureComponent<Props, any> {
  openSubmenu = () => {
    const { setActiveTag, tag } = this.props;
    setActiveTag(tag);
  };

  toggleChecked = () => {
    const { tag, handleAdd } = this.props;
    handleAdd(tag);
  };

  render() {
    const {
      classes, tag
    } = this.props;

    const hasChildren = Boolean(tag.children.length);

    return (
      <MenuItem
        button
        classes={{ root: classes.listItem }}
        onClick={hasChildren ? this.openSubmenu : this.toggleChecked}
      >
        <span className="mr-2 flex-fill">{tag.tagBody.name}</span>
        {hasChildren ? (
          <KeyboardArrowRight className={clsx("d-flex textSecondaryColor", classes.proceedIcon)} />
        ) : (
          <Checkbox classes={{ root: "smallIconButton" }} checked={tag.active} />
        )}
      </MenuItem>
    );
  }
}

export default AddTagMenuItem;
