/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { FormControlLabel } from "@material-ui/core";
import { FormSwitch } from "./form-fields/Switch";
import EditInPlaceField from "./form-fields/EditInPlaceField";
import EditInPlaceDateTimeField from "./form-fields/EditInPlaceDateTimeField";
import { CheckboxField } from "./form-fields/CheckboxField";
import EditInPlaceFileField from "./form-fields/EditInPlaceFileField";
import EditInPlaceMoneyField from "./form-fields/EditInPlaceMoneyField";

interface Props {
  type: string;
}

const DataTypeRenderer = React.memo<Props & any>(props => {
  const { type, ...rest } = props;

  switch (type) {
    default:
    case "Text": {
      return <EditInPlaceField {...rest} />;
    }

    case "Date": {
      return <EditInPlaceDateTimeField type="date" {...rest} />;
    }

    case "Date time": {
      return <EditInPlaceDateTimeField type="datetime" {...rest} />;
    }

    case "Message template":
    case "Select": {
      return <EditInPlaceField {...rest} select />;
    }

    case "Number": {
      return <EditInPlaceField {...rest} type="number" />;
    }

    case "Checkbox": {
      return (
        <FormControlLabel
          classes={{
            root: "checkbox",
            label: props.classes && props.classes.label
          }}
          control={<CheckboxField {...rest} />}
          label={props.label}
        />
      );
    }

    case "Switch": {
      return (
        <FormControlLabel
          className="switchWrapper"
          control={<FormSwitch {...rest} label={null} />}
          label={props.label}
          labelPlacement="start"
        />
      );
    }

    case "Money": {
      return <EditInPlaceMoneyField {...rest} />;
    }

    case "File": {
      return <EditInPlaceFileField {...rest} />;
    }
  }
});

export default DataTypeRenderer;
