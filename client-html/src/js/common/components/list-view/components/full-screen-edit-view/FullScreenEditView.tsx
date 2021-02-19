/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import { withRouter } from "react-router-dom";
import Dialog from "@material-ui/core/Dialog";
import AppBar from "@material-ui/core/AppBar";
import { createStyles, withStyles } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import { getFormSyncErrors, getFormValues, reduxForm } from "redux-form";
import { connect } from "react-redux";
import Slide from "@material-ui/core/Slide";
import Typography from "@material-ui/core/Typography";
import { TransitionProps } from "@material-ui/core/transitions";
import { State } from "../../../../../reducers/state";
import FormSubmitButton from "../../../form/FormSubmitButton";
import LoadingIndicator from "../../../layout/LoadingIndicator";
import { pushGTMEvent } from "../../../google-tag-manager/actions";
import { EditViewContainerProps } from "../../../../../model/common/ListView";
import AppBarHelpMenu from "../../../form/AppBarHelpMenu";
import { getSingleEntityDisplayName } from "../../../../utils/getEntityDisplayName";
import { LSGetItem } from "../../../../utils/storage";
import { APPLICATION_THEME_STORAGE_NAME } from "../../../../../constants/Config";

const styles = theme => createStyles({
    header: {
      height: "64px",
      display: "flex",
      justifyContent: "space-between",
      flexDirection: "row",
      alignItems: "center",
      padding: theme.spacing(0, 3)
    },
    root: {
      marginTop: theme.spacing(8),
      height: `calc(100vh - ${theme.spacing(8)}px)`
    },
    fullEditViewBackground: {
      background: theme.palette.background.default
    }
  });

const Transition = React.forwardRef<unknown, TransitionProps>((props, ref) => (
  <Slide direction="up" ref={ref} {...props as any} />
));

class FullScreenEditViewBase extends React.PureComponent<EditViewContainerProps, any> {
  componentDidUpdate(prevProps) {
    const {
      pending, dispatch, rootEntity, isNested
    } = this.props;

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

    if (
      isNested
      && window.performance.getEntriesByName("NestedEditViewStart").length
      && prevProps.pending
      && !pending
    ) {
      window.performance.mark("NestedEditViewEnd");
      window.performance.measure("NestedEditView", "NestedEditViewStart", "NestedEditViewEnd");
      dispatch(
        pushGTMEvent(
          "timing",
          `${rootEntity}EditView`,
          window.performance.getEntriesByName("NestedEditView")[0].duration
        )
      );
      window.performance.clearMarks("NestedEditViewStart");
      window.performance.clearMarks("NestedEditViewEnd");
      window.performance.clearMeasures("NestedEditViewView");
    }
  }

  updateTitle = (title: string) => {
    const { fullScreenEditView, rootEntity } = this.props;

    if (fullScreenEditView && title) {
      document.title = `${getSingleEntityDisplayName(rootEntity)} (${title})`;
    }
  };

  onCloseClick = () => {
    const {
      toogleFullScreenEditView, dirty, creatingNew, showConfirm, reset
    } = this.props;

    if (dirty || creatingNew) {
      showConfirm(() => {
        reset();
        toogleFullScreenEditView();
      });
    } else {
      toogleFullScreenEditView();
    }
  };

  render() {
    const {
      fullScreenEditView,
      classes,
      handleSubmit,
      EditViewContent,
      dirty,
      invalid,
      creatingNew,
      values,
      updateDeleteCondition,
      hasSelected,
      dispatch,
      rootEntity,
      isNested,
      nestedIndex,
      nameCondition,
      showConfirm,
      openNestedEditView,
      hideFullScreenAppBar,
      manualLink,
      submitSucceeded,
      syncErrors,
      threeColumn,
      alwaysFullScreenCreateView,
      toogleFullScreenEditView,
      form,
      asyncValidating,
      disabledSubmitCondition
    } = this.props;

    const title = values && (nameCondition ? nameCondition(values) : values.name);

    this.updateTitle(title);

    return (
      <Dialog
        fullScreen
        open={Boolean(
          hasSelected && (fullScreenEditView || ((!threeColumn || alwaysFullScreenCreateView) && creatingNew))
        )}
        TransitionComponent={Transition}
        classes={{
          paper: classes.fullEditViewBackground
        }}
        disableEnforceFocus
      >
        <form onSubmit={handleSubmit} autoComplete="off" noValidate>
          {!hideFullScreenAppBar && (
            <AppBar
              elevation={0}
              className={clsx(classes.header, LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasHeader")}
            >
              <div className="flex-fill">
                <Typography className="appHeaderFontSize" color="inherit">
                  {title}
                </Typography>
              </div>
              <div>
                {manualLink && (
                  <AppBarHelpMenu
                    created={values ? new Date(values.createdOn) : null}
                    modified={values ? new Date(values.modifiedOn) : null}
                    auditsUrl={`audit?search=~"${rootEntity}" and entityId in (${values ? values.id : 0})`}
                    manualUrl={manualLink}
                  />
                )}
                <Button onClick={this.onCloseClick} className="closeAppBarButton">
                  Close
                </Button>
                <FormSubmitButton
                  disabled={(!creatingNew && !dirty) || Boolean(asyncValidating) || disabledSubmitCondition}
                  invalid={invalid}
                />
              </div>
            </AppBar>
          )}
          <Grid container className={hideFullScreenAppBar ? undefined : classes.root}>
            <Grid item xs={12}>
              <LoadingIndicator appBarOffset position="fixed" />

              <EditViewContent
                twoColumn
                asyncValidating={asyncValidating}
                syncErrors={syncErrors}
                submitSucceeded={submitSucceeded}
                invalid={invalid}
                onCloseClick={this.onCloseClick}
                manualLink={manualLink}
                rootEntity={rootEntity}
                isNested={isNested}
                nestedIndex={nestedIndex}
                form={form}
                isNew={creatingNew}
                values={values}
                updateDeleteCondition={updateDeleteCondition}
                dispatch={dispatch}
                dirty={dirty}
                showConfirm={showConfirm}
                openNestedEditView={openNestedEditView}
                toogleFullScreenEditView={toogleFullScreenEditView}
              />
            </Grid>
          </Grid>
        </form>
      </Dialog>
    );
  }
}

const mapStateToProps = (state: State, props) => ({
  values: getFormValues(props.form)(state),
  syncErrors: getFormSyncErrors(props.form)(state),
  pending: state.fetch.pending
});

export default reduxForm<any, EditViewContainerProps>({})(
  connect(mapStateToProps, null)(withStyles(styles)(withRouter(FullScreenEditViewBase as any)))
);
