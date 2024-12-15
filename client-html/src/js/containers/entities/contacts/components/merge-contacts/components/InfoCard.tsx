import { MergeLine } from '@api/model';
import Check from '@mui/icons-material/Check';
import { Button, Typography } from '@mui/material';
import Paper from '@mui/material/Paper';
import { AppTheme } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { change } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { MergeContactsFormValues } from '../MergeContacts';
import Marker from './Marker';
import { Selected } from './RadioLabelGroup';

interface Props {
  classes?: any;
  values: MergeContactsFormValues;
  dispatch: any;
  contactNames: any;
  initiallySameIndices?: Set<number>;
}

interface InfoLineProps {
  label?: string;
  data: string;
}

const styles = (({ spacing }: AppTheme) => ({
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

const onGlobalSelectionChange = (mergeLines: MergeLine[], letter: Selected, dispatch) => {
  const newSelected = [];
  const newData = {};

  mergeLines.forEach(l => {
    newSelected.push(letter);
    newData[l.key] = letter.toUpperCase();
  });

  dispatch(change("MergeContactsForm", "mergeRequest.data", newData));
};

const InfoCard = React.memo<Props>(
  ({
 classes, contactNames, initiallySameIndices, values: { mergeData, mergeRequest }, dispatch
}) => {
    const selectedA = useMemo(() => mergeData?.mergeLines
      .filter((l, i) => !initiallySameIndices.has(i))
      .every(l => mergeRequest.data.hasOwnProperty(l.key) && mergeRequest.data[l.key] === "A"),
    [
      mergeData,
      mergeRequest,
      initiallySameIndices
    ]);

    const selectedB = useMemo(() => mergeData?.mergeLines
      .filter((l, i) => !initiallySameIndices.has(i))
      .every(l => mergeRequest.data.hasOwnProperty(l.key) && mergeRequest.data[l.key] === "B"),
    [
      mergeData,
      mergeRequest,
      initiallySameIndices
    ]);

    const onClickA = useCallback(() => {
      if (!selectedA) {
        onGlobalSelectionChange(mergeData.mergeLines, "a", dispatch);
      }
    }, [mergeData, selectedA]);

    const onClickB = useCallback(() => {
      if (!selectedB) {
        onGlobalSelectionChange(mergeData.mergeLines, "b", dispatch);
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

export default withStyles(InfoCard, styles);