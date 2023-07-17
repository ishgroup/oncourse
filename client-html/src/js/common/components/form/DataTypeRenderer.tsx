/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { FormControlLabel, Tooltip } from "@mui/material";
import { FormSwitch } from "../../ish-ui/formFields/Switch";
import EditInPlaceField from "../../ish-ui/formFields/EditInPlaceField";
import EditInPlaceDateTimeField from "../../ish-ui/formFields/EditInPlaceDateTimeField";
import { CheckboxField } from "../../ish-ui/formFields/CheckboxField";
import EditInPlaceFileField from "../../ish-ui/formFields/EditInPlaceFileField";
import EditInPlaceMoneyField from "../../ish-ui/formFields/EditInPlaceMoneyField";
import EditInPlaceSearchSelect from "../../ish-ui/formFields/EditInPlaceSearchSelect";

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
      return <EditInPlaceSearchSelect {...rest} />;
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
