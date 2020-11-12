import React from "react";
import { ButtonBase, withStyles } from "@material-ui/core";

import { green, amber, grey } from "@material-ui/core/colors";
import clsx from "clsx";

const style = () => ({
  button: {
    fontSize: "14px",
    borderRadius: "6px",
    padding: "2px 10px",
    width: "60px"
  },
  hide: {
    background: grey[200],
    color: grey[500]
  },
  show: {
    background: amber[300],
    color: "white"
  },
  edit: {
    background: green[500],
    color: "white"
  }
});
const buttonTypes = ["hide", "show", "edit"];

class CustomButtonEditShowHide extends React.Component<any, any> {
  constructor(props) {
    super(props);
    this.state = {
      currentType: this.props.type,
      currentClass: this.props.type
    };
  }

  nextType = type => {
    const index = buttonTypes.indexOf(type);
    if (index === buttonTypes.length - 1) {
      this.setState({
        currentType: buttonTypes[0],
        currentClass: buttonTypes[0]
      });
    } else {
      this.setState({
        currentType: buttonTypes[index + 1],
        currentClass: buttonTypes[index + 1]
      });
    }
  };

  render() {
    const { classes } = this.props;
    const { currentType, currentClass } = this.state;
    return (
      <ButtonBase
        className={clsx(classes.button, classes[currentClass])}
        children={currentType}
        onClick={() => this.nextType(currentType)}
      />
    );
  }
}

export default withStyles(style)(CustomButtonEditShowHide);
