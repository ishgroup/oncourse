import React, { useCallback, useMemo } from "react";
import RadioLabel from "./RadioLabel";
import Typography from "@material-ui/core/Typography";
import { MergeLine } from "@api/model";
import { WrappedFieldProps } from "redux-form";

export interface RadioLabelGroupProps extends WrappedFieldProps {
  line: MergeLine;
  showDifference?: boolean;
  selected?: Selected[];
  setSelected?: any;
  index: number;
}

export type Selected = "a" | "b";

const RadioLabelGroup = React.memo(
  ({ line, showDifference, input: { onChange }, setSelected, selected, index }: RadioLabelGroupProps) => {
    const onSelect = useCallback(
      (variant: Selected) => {
        onChange(variant.toUpperCase());
        setSelected(prev => {
          const updated = [...prev];
          updated[index] = variant;
          return updated;
        });
      },
      [index]
    );

    const isDifferent = useMemo(() => line.a !== line.b, [line.a, line.b]);

    const lineContent = useMemo(() => line[selected[index]] || "None", [selected, index]);

    return showDifference && !isDifferent ? null : (
      <div>
        <RadioLabel line={line} selected={selected[index]} setSelected={onSelect} isDifferent={isDifferent} />
        {isDifferent ? (
          selected[index] ? (
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
