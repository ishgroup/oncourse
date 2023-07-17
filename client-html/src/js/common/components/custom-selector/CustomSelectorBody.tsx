import React from "react";
import Typography from "@mui/material/Typography";
import { CustomSelectorOption } from "./CustomSelector";
import FormField from "../form/formFields/FormField";

interface Props {
  className?: any;
  options: CustomSelectorOption[];
  selectedIndex: number;
}

const CustomSelectorBody = ({ className, options, selectedIndex }: Props) => {
  const option = options[selectedIndex];
  return (
    <div className={className}>
      {option.formFileldProps && (
        <Typography component="div">
          <FormField
            {...option.formFileldProps}
            {...option.formFileldProps.type !== "code" ? { inline: true } : {}}
          />
          {" "}
          {option.body}
        </Typography>
      )}
    </div>
  );
};

export default CustomSelectorBody;