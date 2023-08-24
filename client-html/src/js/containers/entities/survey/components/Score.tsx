import Rating from "@mui/lab/Rating";
import { Theme } from "@mui/material";
import Typography from "@mui/material/Typography";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import React, { useCallback, useMemo } from "react";
import { WrappedFieldProps } from "redux-form";

interface Props extends WrappedFieldProps {
  label: string;
  max?: number;
  score?: number;
  classes?: any;
}

const styles = createStyles(({ spacing, typography, palette }: Theme) => ({
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

export default withStyles(styles)(Score);
