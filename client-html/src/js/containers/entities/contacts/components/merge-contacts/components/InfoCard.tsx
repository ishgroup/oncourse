import React, { useCallback, useMemo } from "react";
import Typography from "@material-ui/core/Typography";
import Paper from "@material-ui/core/Paper";
import Button from "@material-ui/core/Button";
import Check from "@material-ui/icons/Check";
import { createStyles, withStyles } from "@material-ui/core/styles";
import { Theme } from "@material-ui/core";
import { change } from "redux-form";
import { MergeLine } from "@api/model";
import { MergeContactsFormValues } from "../MergeContacts";
import { Selected } from "./RadioLabelGroup";
import Marker from "./Marker";

interface Props {
  classes?: any;
  values: MergeContactsFormValues;
  selected: Selected[];
  setSelected: any;
  dispatch: any;
  contactNames: any;
  initiallySameIndices?: Set<number>;
}

interface InfoLineProps {
  label?: string;
  data: string;
}

const styles = createStyles(({ spacing }: Theme) => ({
  root: {
    padding: spacing(2),
    "& > div": {
      display: "grid",
      gridTemplateColumns: "1fr 1fr"
    }
  },
  pill: {
    padding: spacing(0, 0.5),
    marginLeft: spacing(1)
  },
  button: {
    fontSize: spacing(1.5),
    padding: spacing(0, 1),
    marginLeft: spacing(2)
  },
  buttonIcon: {
    margin: spacing(0, -0.5, 0.25, 0.5)
  }
}));

const InfoLine = ({ label, data }: InfoLineProps) => (
  <Typography variant="body2" component="div">
    {label && (
      <Typography variant="body2" color="textSecondary" display="inline">
        {label}
        {" "}
      </Typography>
    )}
    {data}
  </Typography>
);

const onGlobalSelectionChange = (mergeLines: MergeLine[], letter: Selected, dispatch, setSelected) => {
  const newSelected = [];
  const newData = {};

  mergeLines.forEach(l => {
    newSelected.push(letter);
    newData[l.key] = letter.toUpperCase();
  });

  setSelected(newSelected);
  dispatch(change("MergeContactsForm", "mergeRequest.data", newData));
};

const InfoCard = React.memo<Props>(
  ({
 classes, selected, contactNames, setSelected, initiallySameIndices, values: { mergeData }, dispatch
}) => {
    const selectedA = useMemo(() => selected.filter((s, i) => !initiallySameIndices.has(i)).every(s => s === "a"), [
      selected,
      initiallySameIndices
    ]);

    const selectedB = useMemo(() => selected.filter((s, i) => !initiallySameIndices.has(i)).every(s => s === "b"), [
      selected,
      initiallySameIndices
    ]);

    const onClickA = useCallback(() => {
      if (!selectedA) {
        onGlobalSelectionChange(mergeData.mergeLines, "a", dispatch, setSelected);
      }
    }, [mergeData, selectedA]);

    const onClickB = useCallback(() => {
      if (!selectedB) {
        onGlobalSelectionChange(mergeData.mergeLines, "b", dispatch, setSelected);
      }
    }, [mergeData, selectedB]);

    return mergeData ? (
      <Paper className={classes.root}>
        <div>
          <div className="centeredFlex">
            <Typography className="heading" display="inline">
              {contactNames.a}
            </Typography>

            <Marker letter="A" className="ml-0-5" />

            <Button
              variant="contained"
              size="small"
              color="primary"
              onClick={onClickA}
              className={classes.button}
              classes={{
                endIcon: classes.buttonIcon
              }}
              disabled={selectedA}
              endIcon={selectedA ? <Check color="inherit" /> : undefined}
            >
              Select all
            </Button>
          </div>

          <div className="centeredFlex">
            <Typography className="heading" display="inline">
              {contactNames.b}
            </Typography>

            <Marker letter="B" className="ml-0-5" />

            <Button
              variant="contained"
              size="small"
              color="primary"
              onClick={onClickB}
              className={classes.button}
              classes={{
                endIcon: classes.buttonIcon
              }}
              disabled={selectedB}
              endIcon={selectedB ? <Check color="inherit" /> : undefined}
            >
              Select all
            </Button>
          </div>
        </div>

        {mergeData.infoLines.map((l, i) => (
          <div key={i}>
            <InfoLine data={l.a} label={l.label} />
            <InfoLine data={l.b} label={l.label} />
          </div>
        ))}
      </Paper>
    ) : null;
  }
);

export default withStyles(styles)(InfoCard);
