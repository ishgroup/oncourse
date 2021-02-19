/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FormControlLabel } from "@material-ui/core";
import React, {
 Dispatch, useCallback, useEffect, useMemo, useState
} from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import {
 Field, getFormValues, initialize, reduxForm
} from "redux-form";
import { createStyles, withStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import Button from "@material-ui/core/Button";
import { MergeData, MergeLine, MergeRequest } from "@api/model";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import { State } from "../../../../../reducers/state";
import LoadingIndicator from "../../../../../common/components/layout/LoadingIndicator";
import { Switch } from "../../../../../common/components/form/form-fields/Switch";
import { getMergeContacts, postMergeContacts } from "../../actions";
import { getContactName } from "../../utils";
import RadioLabelGroup, { Selected } from "./components/RadioLabelGroup";
import InfoCard from "./components/InfoCard";
import { AppTheme } from "../../../../../model/common/Theme";
import { LSGetItem } from "../../../../../common/utils/storage";
import { APPLICATION_THEME_STORAGE_NAME } from "../../../../../constants/Config";

export interface MergeContactsFormValues {
  mergeData?: MergeData;
  mergeRequest?: MergeRequest;
}

interface Props {
  classes?: any;
  getMergeContacts?: (contactA: string, contactB: string) => void;
  postMergeContacts?: (request: MergeRequest) => void;
  handleSubmit?: any;
  values?: MergeContactsFormValues;
  dispatch?: any;
  successDialogOpen?: boolean;
  isModal?: boolean;
  mergeCloseOnSuccess?: () => void;
  closeWithoutMerge?: () => void;
}

const FORM: string = "MergeContactsForm";

const initialValues = { mergeData: { mergeLines: [], infoLines: [] }, mergeRequest: {} };

const styles = createStyles(({ spacing }: AppTheme) => ({
  switcherGroup: {
    gridAutoFlow: "column",
    gridGap: spacing(1)
  },
  contactsFields: {
    gridGap: spacing(3)
  },
  rightColumn: {
    gridGap: spacing(2)
  },
  score: {
    fontWeight: 600,
    fontSize: "24px"
  }
}));

const calculateMergeScore = (mergeLines: MergeLine[]) => {
  let matched = 0;

  for (const line of mergeLines) {
    if (line.a === line.b) {
      matched++;
    }
  }

  return Math.round(matched / (mergeLines.length / 100));
};

const initiallySameIndices: Set<number> = new Set();

const MergeContacts = React.memo<Props>(
  ({
    classes,
    getMergeContacts,
    postMergeContacts,
    values,
    handleSubmit,
    dispatch,
    successDialogOpen,
    isModal,
    mergeCloseOnSuccess,
    closeWithoutMerge
  }) => {
    const [showDifference, setShowDifference] = useState(false);
    const [agreeWithLowScore, setAgreeWithLowScore] = useState(false);

    const [selected, setSelected] = useState<Selected[]>([]);

    useEffect(() => {
      if (window.location.search) {
        const search = new URLSearchParams(window.location.search);

        if (search.has("contactA") && search.has("contactB")) {
          getMergeContacts(search.get("contactA"), search.get("contactB"));
        }

        document.title = "Merge Contacts";
      }
    }, [window.location.search]);

    useEffect(() => {
      if (!selected.length && values.mergeData.mergeLines.length) {
        setSelected(
          values.mergeData.mergeLines.map((l, i) => {
            if (l.a === l.b) {
              initiallySameIndices.add(i);
              return "a" as Selected;
            }
            return null;
          })
        );
      }
    }, [selected, values.mergeData.mergeLines]);

    const onSubmit = useCallback(
      values => {
        postMergeContacts(values.mergeRequest);
        if (isModal) {
          mergeCloseOnSuccess();
        }
      },
      [selected]
    );

    const score = useMemo(() =>
      (values.mergeData.mergeLines.length ? calculateMergeScore(values.mergeData.mergeLines) : 0),
     [values.mergeData.mergeLines]);

    const matchScoreLabel = useMemo(
      () => {
        switch (true) {
          default:
          case score <= 50:
            return (
              <Typography className={classes.score} color="error">
                Poor match
              </Typography>
            );
          case score > 50 && score <= 70:
            return (
              <Typography className={classes.score} color="error">
                Unlikely match
              </Typography>
            );
          case score > 70 && score <= 85:
            return (
              <Typography className={clsx(classes.score, "primaryColor")}>
                Probable match
              </Typography>
            );
          case score > 85:
            return (
              <Typography className={clsx(classes.score, "successColor")}>
                Likely match
              </Typography>
            );
        }
      },
      [score]
    );

    const isMergeDisabled = useMemo(() => selected.some(s => s === null) || (score <= 70 && !agreeWithLowScore),
      [selected, score, agreeWithLowScore]);

    const contactNames = useMemo(() => {
      const firstNameData = values.mergeData.mergeLines.find(m => m.key === "Contact.firstName");
      const lastNameData = values.mergeData.mergeLines.find(m => m.key === "Contact.lastName");

      if (!firstNameData || !lastNameData) {
        return {
          a: "",
          b: ""
        };
      }

      return {
        a: getContactName({
          firstName: firstNameData.a,
          lastName: lastNameData.a
        }),
        b: getContactName({
          firstName: firstNameData.b,
          lastName: lastNameData.b
        })
      };
    }, [values.mergeData]);

    console.log(score);

    return (
      <>
        <Dialog open={successDialogOpen} maxWidth="md" scroll="body">
          <div style={{ padding: "30px 20px 40px" }}>
            <DialogTitle>Merge successful</DialogTitle>
            <DialogContent className="overflow-hidden">Contacts were merged successfully! Close the window.</DialogContent>
          </div>
          {/* <DialogActions className="p-3"> */}
          {/*  <Button color="primary" onClick={() => closeMergeContactsPage()}> */}
          {/*    Close Window */}
          {/*  </Button> */}
          {/* </DialogActions> */}
        </Dialog>
        <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
          <AppBar className={clsx(LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasHeader")}>
            <Toolbar>
              <Typography className="appHeaderFontSize">
                Merge
                {' '}
                {contactNames.a}
                {' '}
                with
                {' '}
                {contactNames.b}
              </Typography>

              <div className="flex-fill" />

              {isModal ? (
                <Button onClick={closeWithoutMerge} className="whiteAppBarButton">
                  Close
                </Button>
              ) : null}

              <Button
                type="submit"
                classes={{
                  root: "whiteAppBarButton",
                  disabled: "whiteAppBarButtonDisabled"
                }}
                disabled={isMergeDisabled}
              >
                Merge
              </Button>
            </Toolbar>
          </AppBar>

          <LoadingIndicator appBarOffset />

          <Grid container className="p-3 h-100 appBarContainer defaultBackgroundColor" wrap="nowrap">
            <Grid item xs={12} md={6} className={clsx("d-grid align-content-start", classes.contactsFields)}>
              <Typography variant="body2" className={clsx("d-grid align-items-center justify-content-start", classes.switcherGroup)}>
                Only show differences
                <Switch checked={showDifference} onChange={() => setShowDifference(prevValue => !prevValue)} />
              </Typography>

              {values.mergeData.mergeLines.map((l, i) => (
                <Field
                  key={i}
                  line={l}
                  index={i}
                  name={`mergeRequest.data["${l.key}"]`}
                  component={RadioLabelGroup}
                  showDifference={showDifference}
                  selected={selected}
                  setSelected={setSelected}
                />
              ))}
            </Grid>
            <Grid item xs={12} md="auto" className={clsx("d-grid align-items-start align-content-start", classes.rightColumn)}>
              <InfoCard
                values={values}
                selected={selected}
                setSelected={setSelected}
                initiallySameIndices={initiallySameIndices}
                dispatch={dispatch}
                contactNames={contactNames}
              />
              <Typography variant="body2">
                Merging two contacts cannot be undone. Choose which values to retain; the other data will be discarded.
              </Typography>

              {matchScoreLabel}

              {score <= 70 && (
              <div className="mt1">
                <Typography variant="body2">
                  These records are not a likely match
                </Typography>
                <FormControlLabel
                  classes={{
                    root: "checkbox mt1"
                  }}
                  control={(
                    <StyledCheckbox
                      color="primary"
                      checked={agreeWithLowScore}
                      onChange={(e, v) => setAgreeWithLowScore(v)}
                    />
                  )}
                  label="I am sure I want to merge these records"
                />
              </div>
)}

            </Grid>
          </Grid>
        </form>
      </>
    );
  }
);

const mapStateToProps = (state: State) => ({
  values: getFormValues(FORM)(state),
  successDialogOpen: state.contacts.mergeContactsSuccessOpen
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getMergeContacts: (contactA: string, contactB: string) => dispatch(getMergeContacts(contactA, contactB)),
  postMergeContacts: (request: MergeRequest) => dispatch(postMergeContacts(request)),
  closeWithoutMerge: () => dispatch(initialize(FORM, initialValues))
});

export default reduxForm({
  form: FORM,
  initialValues
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(MergeContacts))) as any;
