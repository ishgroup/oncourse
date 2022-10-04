/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { FormControlLabel, Tooltip } from "@mui/material";
import { FormSwitch } from "./formFields/Switch";
import EditInPlaceField from "./formFields/EditInPlaceField";
import EditInPlaceDateTimeField from "./formFields/EditInPlaceDateTimeField";
import { CheckboxField } from "./formFields/CheckboxField";
import EditInPlaceFileField from "./formFields/EditInPlaceFileField";
import EditInPlaceMoneyField from "./formFields/EditInPlaceMoneyField";

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
        <Tooltip title={props.label}>
          <FormControlLabel
            classes={{
              root: "checkbox"
            }}
            control={<CheckboxField {...rest} />}
            label={props.label}
          />
        </Tooltip>
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
