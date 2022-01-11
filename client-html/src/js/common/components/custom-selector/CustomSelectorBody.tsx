import React from "react";
import Typography from "@mui/material/Typography";
import { Field } from "redux-form";
import { CustomSelectorOption } from "./CustomSelector";

interface Props {
  className?: any;
  options: CustomSelectorOption[];
  selectedIndex: number;
}

const CustomSelectorBody = ({ className, options, selectedIndex }: Props) => {
  const option = options[selectedIndex];
  return (
    <div className={className}>
      {option.type && (
        <Typography component="div">
          <Field
            name={option.fieldName}
            component={option.component}
            type={option.type}
            validate={option.validate}
            normalize={option.normalize}
            format={option.format}
            min={option.min}
            preformatDisplayValue={option.preformatDisplayValue}
          />
          {option.type && option.type !== "date" && ` ${option.body}`}
        </Typography>
      )}
    </div>
  );
};

export default CustomSelectorBody;
