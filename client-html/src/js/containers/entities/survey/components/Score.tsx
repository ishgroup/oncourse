import Rating from '@mui/lab/Rating';
import Typography from '@mui/material/Typography';
import { AppTheme } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { WrappedFieldProps } from 'redux-form';
import { withStyles } from 'tss-react/mui';

interface Props extends WrappedFieldProps {
  label: string;
  max?: number;
  score?: number;
  classes?: any;
}

const styles = (({ spacing, typography, palette }: AppTheme) => ({
  scoreNumber: {
    fontSize: "3rem",
    lineHeight: "0.9em",
    fontWeight: typography.fontWeightMedium,
    color: palette.text.secondary,
    marginRight: spacing(1),
    minWidth: spacing(4)
  }
}));

const Score = React.memo((props: Props) => {
  const {
    label, max = 5, classes, input: { value, onChange, name }, meta: { form }
  } = props;

  const onChangeHandler = useCallback((event, newValue) => {
    onChange(newValue);
  }, []);

  const ratingName = useMemo(() => `${form}-${name}`, [form, name]);

  return (
    <div className="d-flex">
      <Typography className={classes.scoreNumber}>{value || "?"}</Typography>
      <div className="flex-column justify-content-space-between">
        <Rating name={ratingName} value={value} max={max} size="small" onChange={onChangeHandler} />
        <Typography variant="body2">{label}</Typography>
      </div>
    </div>
  );
});

export default withStyles(Score, styles);
