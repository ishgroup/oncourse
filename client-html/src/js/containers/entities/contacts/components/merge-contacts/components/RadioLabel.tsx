import { MergeLine } from "@api/model";
import { Theme } from "@mui/material";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import React, { useCallback } from "react";
import { Selected } from "./RadioLabelGroup";
import RadioLabelPill from "./RadioPill";

interface RadioLabelProps {
  line: MergeLine;
  selected?: Selected;
  setSelected?: any;
  classes?: any;
  isDifferent?: boolean;
}

const styles = createStyles(({ spacing }: Theme) => ({
  root: {
    gridGap: spacing(1),
    gridAutoFlow: "column"
  }
}));

const RadioLabel = React.memo(({
 line, classes, selected, setSelected, isDifferent 
}: RadioLabelProps) => {
  const setSelectedA = useCallback(() => setSelected("a"), []);

  const setSelectedB = useCallback(() => setSelected("b"), []);

  return (
    <Typography variant="caption" color="textSecondary" className={`d-grid align-items-center justify-content-start ${classes.root}`}>
      {line.label}

      {isDifferent && (
        <>
          <RadioLabelPill name={line.a || "None"} letter="a" selected={selected === "a"} onClick={setSelectedA} />

          <RadioLabelPill name={line.b || "None"} letter="b" selected={selected === "b"} onClick={setSelectedB} />
        </>
      )}
    </Typography>
  );
});

export default withStyles(styles)(RadioLabel);
