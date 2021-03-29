/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { createStyles, withStyles } from "@material-ui/core";
import { getFormSyncErrors, getFormValues, reduxForm } from "redux-form";
import Typography from "@material-ui/core/Typography";
import { connect } from "react-redux";
import { State } from "../../../../../reducers/state";
import FormSubmitButton from "../../../form/FormSubmitButton";
import { pushGTMEvent } from "../../../google-tag-manager/actions";
import { EditViewContainerProps } from "../../../../../model/common/ListView";

const styles = theme =>
  createStyles({
    root: {
      display: "flex",
      flexDirection: "column",
      width: "100%",
      userSelect: "text",
    },
    actionButtonsGroup: {
      position: "absolute",
      bottom: theme.spacing(2),
      right: theme.spacing(2),
      display: "flex",
      justifyContent: "flex-end",
      zIndex: 1000
    }
  });

class EditView extends React.PureComponent<EditViewContainerProps, any> {
  componentDidUpdate(prevProps) {
    const { pending, dispatch, rootEntity } = this.props;

    if (window.performance.getEntriesByName("EditViewStart").length && prevProps.pending && !pending) {
      window.performance.mark("EditViewEnd");
      window.performance.measure("EditView", "EditViewStart", "EditViewEnd");
      dispatch(
        pushGTMEvent("timing", `${rootEntity}EditView`, window.performance.getEntriesByName("EditView")[0].duration)
      );
      window.performance.clearMarks("EditViewStart");
      window.performance.clearMarks("EditViewEnd");
      window.performance.clearMeasures("EditView");
    }
  }

  render() {
    const {
      classes,
      EditViewContent,
      handleSubmit,
      dirty,
      invalid,
      hasSelected,
      creatingNew,
      values,
      updateDeleteCondition,
      dispatch,
      rootEntity,
      form,
      showConfirm,
      openNestedEditView,
      manualLink,
      submitSucceeded,
      syncErrors,
      toogleFullScreenEditView,
      asyncValidating,
      disabledSubmitCondition
    } = this.props;

    return (
      <form className={classes.root} onSubmit={handleSubmit} autoComplete="off" noValidate>
        {!hasSelected && (
          <div className="noRecordsMessage">
            <Typography variant="h6" color="inherit" align="center">
              Nothing selected
            </Typography>
          </div>
        )}

        {hasSelected && (
          <>
            <div className="flex-fill">
              <EditViewContent
                asyncValidating={asyncValidating}
                syncErrors={syncErrors}
                submitSucceeded={submitSucceeded}
                manualLink={manualLink}
                invalid={invalid}
                form={form}
                rootEntity={rootEntity}
                isNew={creatingNew}
                values={values}
                updateDeleteCondition={updateDeleteCondition}
                dirty={dirty}
                dispatch={dispatch}
                showConfirm={showConfirm}
                openNestedEditView={openNestedEditView}
                toogleFullScreenEditView={toogleFullScreenEditView}
              />
            </div>
            <div className={classes.actionButtonsGroup}>
              <FormSubmitButton
                disabled={(!creatingNew && !dirty) || Boolean(asyncValidating) || disabledSubmitCondition}
                invalid={invalid}
                fab
              />
            </div>
          </>
        )}
      </form>
    );
  }
}

const mapStateToProps = (state: State, props) => ({
  values: getFormValues(props.form)(state),
  syncErrors: getFormSyncErrors(props.form)(state),
  pending: state.fetch.pending
});

export default reduxForm<any, EditViewContainerProps>({ destroyOnUnmount: false })(
  connect(mapStateToProps, null)(withStyles(styles)(EditView))
);
