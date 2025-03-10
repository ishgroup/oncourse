import { MergeLine } from '@api/model';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React, { useCallback, useMemo } from 'react';
import { WrappedFieldProps } from 'redux-form';
import RadioLabel from './RadioLabel';

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
              {$t('select_one')}
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