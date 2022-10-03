import React, { useCallback, useMemo } from "react";
import RadioLabel from "./RadioLabel";
import Typography from "@mui/material/Typography";
import { MergeLine } from "@api/model";
import { WrappedFieldProps } from "redux-form";

export interface RadioLabelGroupProps extends WrappedFieldProps {
  line: MergeLine;
  showDifference?: boolean;
  index: number;
}

export type Selected = "a" | "b";

const RadioLabelGroup = React.memo(
  (props: RadioLabelGroupProps) => {
    const { line, showDifference, input: { onChange, value }, index } = props;

    const onSelect = useCallback(
      (variant: Selected) => {
        onChange(variant.toUpperCase());
      },
      [index]
    );

    const isDifferent = useMemo(() => line.a !== line.b, [line.a, line.b]);

    const lineContent = useMemo(() => line[value?.toLowerCase()] || "None", [index, value, line]);

    return showDifference && !isDifferent ? null : (
      <div>
        <RadioLabel line={line} selected={value?.toLowerCase()} setSelected={onSelect} isDifferent={isDifferent} />
        {isDifferent ? (
          value ? (
            <Typography variant="body2" className={lineContent === "None" ? "placeholderContent" : undefined}>
              {lineContent}
            </Typography>
          ) : (
            <Typography variant="body2" color="error">
              Select one
            </Typography>
          )
        ) : (
          <Typography variant="body2">{line["a"]}</Typography>
        )}
      </div>
    );
  }
);

export default RadioLabelGroup;