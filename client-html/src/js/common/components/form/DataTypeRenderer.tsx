/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FormControlLabel, Tooltip } from "@mui/material";
import {
  CheckboxField,
  EditInPlaceDateTimeField,
  EditInPlaceField,
  EditInPlaceFileField,
  EditInPlaceMoneyField,
  EditInPlaceSearchSelect,
  FormSwitch
} from "ish-ui";
import React from "react";
import { COMMON_PLACEHOLDER } from "../../../constants/Forms";
import { useAppSelector } from "../../utils/hooks";
import EditInPlaceRemoteDataSearchSelect from "./formFields/EditInPlaceRemoteDataSearchSelect";

interface Props {
  type: string;
}

const DataTypeRenderer = React.memo<Props & any>(props => {
  const { type, ...rest } = props;

  const currencySymbol = useAppSelector(state => state.currency?.shortCurrencySymbol);
  const processActionId = useAppSelector(state => state.fieldProcessing[rest.name]);

  switch (type) {
    default:
    case "Text": {
      return <EditInPlaceField {...rest} />;
    }

    case "Date": {
      return <EditInPlaceDateTimeField type="date" {...rest} processActionId={processActionId}/>;
    }

    case "Date time": {
      return <EditInPlaceDateTimeField type="datetime" {...rest} processActionId={processActionId}/>;
    }

    case "Portal subdomain": {
      return <EditInPlaceRemoteDataSearchSelect {...rest}/>;  
    }
    
    case "Message template":
    case "Select": {
      return <EditInPlaceSearchSelect {...rest} />;
    }

    case "Number": {
      return <EditInPlaceField {...rest} type="number"/>;
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
          control={<FormSwitch {...rest} label={null}/>}
          label={props.label}
          labelPlacement="start"
        />
      );
    }

    case "Money": {
      return <EditInPlaceMoneyField {...rest} currencySymbol={currencySymbol}/>;
    }

    case "File": {
      return <EditInPlaceFileField {...rest} />;
    }
  }
});

export default props => <DataTypeRenderer {...props} placeholder={COMMON_PLACEHOLDER}/>;
