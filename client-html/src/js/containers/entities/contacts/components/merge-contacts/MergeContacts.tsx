/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { MergeData, MergeLine, MergeRequest } from "@api/model";
import { FormControlLabel, Grid } from "@mui/material";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Typography from "@mui/material/Typography";
import { createStyles, withStyles } from "@mui/styles";
import clsx from "clsx";
import { AppTheme, StyledCheckbox, Switch } from "ish-ui";
import React, { Dispatch, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import { Field, getFormValues, initialize, reduxForm } from "redux-form";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { State } from "../../../../../reducers/state";
import { getMergeContacts, postMergeContacts } from "../../actions";
import { getContactFullName } from "../../utils";
import InfoCard from "./components/InfoCard";
import RadioLabelGroup from "./components/RadioLabelGroup";

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
    const [showDifference, setShowDifference] = useState(true);
    const [agreeWithLowScore, setAgreeWithLowScore] = useState(false);

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
      if (values.mergeData.mergeLines.length) {
        values.mergeData.mergeLines.forEach((l, i) => {
          if (l.a === l.b) {
            initiallySameIndices.add(i);
          }
        });
      }
    }, [values.mergeData.mergeLines]);

    const onSubmit = values => {
      postMergeContacts(values.mergeRequest);
      if (isModal) {
        mergeCloseOnSuccess();
      }
    };

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

    const isMergeDisabled = values.mergeData.mergeLines.some(l => values.mergeRequest.data[l.key] === undefined)
        || (score <= 70 && !agreeWithLowScore);

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
        a: getContactFullName({
          firstName: firstNameData.a,
          lastName: lastNameData.a
        }),
        b: getContactFullName({
          firstName: firstNameData.b,
          lastName: lastNameData.b
        })
      };
    }, [values.mergeData]);

    return (
      <>
        <Dialog open={successDialogOpen} maxWidth="md" scroll="body">
          <div style={{ padding: "30px 20px 40px" }}>
            <DialogTitle>Merge successful</DialogTitle>
            <DialogContent className="overflow-hidden">Contacts were merged successfully! Close the window.</DialogContent>
          </div>
        </Dialog>

        <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
          <AppBarContainer
            hideHelpMenu
            closeButtonText="Close"
            submitButtonText="Merge"
            onCloseClick={isModal ? closeWithoutMerge : null}
            disabled={isMergeDisabled}
            title={`Merge ${contactNames.a} with ${contactNames.b}`}
          >

            <Grid container wrap="nowrap">
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
                  />
                ))}
              </Grid>
              <Grid item xs={12} md="auto" className={clsx("d-grid align-items-start align-content-start", classes.rightColumn)}>
                <InfoCard
                  values={values}
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
          </AppBarContainer>
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