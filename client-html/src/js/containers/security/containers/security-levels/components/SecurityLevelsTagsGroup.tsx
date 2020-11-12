import React from "react";
import {
 FormGroup, FormControlLabel, Typography, withStyles 
} from "@material-ui/core";
import clsx from "clsx";

import CustomButtonEditShowHide from "./CustomButtonEditShowHide";

const style = () => ({
  headingMarginBottom: {
    marginBottom: "15px"
  },
  descriptionMarginBottom: {
    marginBottom: "15px"
  },
  buttonLabel: {
    marginLeft: "15px"
  },
  customTriggerEditShowHide: {
    marginBottom: "10px"
  }
});

class SecurityLevelsTagsGroup extends React.Component<any, any> {
  render() {
    const {
 classes, className, heading, description, childItems 
} = this.props;
    return (
      <div className={className}>
        <Typography className={clsx("heading", classes.headingMarginBottom)}>{heading}</Typography>
        <Typography className={classes.descriptionMarginBottom}>{description}</Typography>
        <FormGroup>
          {childItems.map((item, index) => (
            <FormControlLabel
              classes={{
                root: "m-0",
                label: classes.buttonLabel
              }}
              label={item.name}
              control={<CustomButtonEditShowHide type={item.value} />}
              labelPlacement="end"
              className={classes.customTriggerEditShowHide}
              key={index}
            />
          ))}
        </FormGroup>
      </div>
    );
  }
}

export default withStyles(style)(SecurityLevelsTagsGroup);
