/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import * as React from 'react';
import { connect } from 'react-redux';
import { getFormSyncErrors, getFormValues, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { TAB_LIST_SCROLL_TARGET_ID } from '../../../../../constants/Config';
import { EditViewContainerProps } from '../../../../../model/common/ListView';
import { State } from '../../../../../reducers/state';
import FormSubmitButton from '../../../form/FormSubmitButton';
import { pushGTMEvent } from '../../../google-tag-manager/actions';

export const editViewFormRole: string = "editView-form";

const styles = theme =>
  ({
    root: {
      display: "flex",
      flexDirection: "column",
      width: "100%",
      userSelect: "text",
      position: "relative",
      overflow: "hidden"
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
      manualLink,
      submitSucceeded,
      syncErrors,
      toogleFullScreenEditView,
      asyncValidating,
      disabledSubmitCondition
    } = this.props;

    const noTabList = document.getElementById(TAB_LIST_SCROLL_TARGET_ID) === null;

    return (
      <form
        className={clsx(classes.root, noTabList && "fullHeightWithoutAppBar")}
        onSubmit={handleSubmit}
        autoComplete="off"
        noValidate
        role={editViewFormRole}
      >
        {!hasSelected && (
          <div className="noRecordsMessage">
            <Typography variant="h6" color="inherit" align="center">
              Nothing selected
            </Typography>
          </div>
        )}

        {hasSelected && (
          <>
            <div className={clsx("flex-fill", noTabList && "overflow-y-auto")}>
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
  connect(mapStateToProps, null)(withStyles(EditView, styles))
);
