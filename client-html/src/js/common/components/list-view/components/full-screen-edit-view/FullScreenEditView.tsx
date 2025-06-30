/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import Slide from '@mui/material/Slide';
import { TransitionProps } from '@mui/material/transitions';
import $t from '@t';
import clsx from 'clsx';
import { AppBarHelpMenu } from 'ish-ui';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { getFormSyncErrors, getFormValues, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { APPLICATION_THEME_STORAGE_NAME, TAB_LIST_SCROLL_TARGET_ID } from '../../../../../constants/Config';
import { EditViewContainerProps } from '../../../../../model/common/ListView';
import { State } from '../../../../../reducers/state';
import { getSingleEntityDisplayName } from '../../../../utils/getEntityDisplayName';
import { LSGetItem } from '../../../../utils/storage';
import FormSubmitButton from '../../../form/FormSubmitButton';
import { pushGTMEvent } from '../../../google-tag-manager/actions';
import LoadingIndicator from '../../../progress/LoadingIndicator';
import FullScreenStickyHeader from './FullScreenStickyHeader';

const styles = (theme, p, classes) => ({
  header: {
    height: "64px",
    display: "flex",
    justifyContent: "space-between",
    flexDirection: "row",
    alignItems: "center",
    padding: theme.spacing(0, 3),
    background: theme.appBar.header.background,
    color: theme.appBar.header.color,
    [`& .${classes.submitButtonAlternate}`]: {
      background: `${theme.appBar.headerAlternate.color}`,
      color: `${theme.appBar.headerAlternate.background}`,
    },
    [`& .${classes.closeButtonAlternate}`]: {
      color: `${theme.appBar.headerAlternate.color}`
    }
  },
  root: {
    marginTop: theme.spacing(8),
    height: `calc(100vh - ${theme.spacing(8)})`,
    overflow: 'hidden'
  },
  fullEditViewBackground: {
    background: theme.appBar.header.background,
  },
  headerAlternate: {
    background: `${theme.appBar.headerAlternate.background}`,
    color: `${theme.appBar.headerAlternate.color}`,
    [`& .${classes.actionsWrapper} svg`]: {
      color: `${theme.appBar.headerAlternate.color}`,
    }
  },
  actionsWrapper: {
    display: "inline-block"
  },
  submitButtonAlternate: {
  },
  closeButtonAlternate: {},
  titleWrapper: {}
});

const Transition = React.forwardRef<unknown, TransitionProps>((props, ref) => (
  <Slide direction="up" ref={ref} {...props as any} />
));

class FullScreenEditViewBase extends React.PureComponent<EditViewContainerProps, any> {
  state = {
    hasScrolling: false
  };

  componentDidUpdate(prevProps) {
    const {
      pending, dispatch, rootEntity
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
  }

  updateTitle = (title: string) => {
    const { fullScreenEditView, customTableModel, rootEntity,  } = this.props;

    if (fullScreenEditView && title) {
      document.title = `${getSingleEntityDisplayName(customTableModel || rootEntity)} (${title})`;
    }
  };

  resetScroll = () => {
    this.setState({
      hasScrolling: false
    });
  };

  onCloseClick = () => {
    const {
      toogleFullScreenEditView, dirty, creatingNew, showConfirm, reset
    } = this.props;

    if (dirty || creatingNew) {
      showConfirm({
        onConfirm: () => {
          reset();
          toogleFullScreenEditView(false);
          this.resetScroll();
        }
      });
    } else {
      toogleFullScreenEditView(false);
      this.resetScroll();
    }
  };

  onScroll = e => {
    if (e.target.scrollTop > 0 && !this.state.hasScrolling) {
      this.setState({
        hasScrolling: true
      });
    }
    if (e.target.scrollTop <= 0 && this.state.hasScrolling) {
      this.setState({
        hasScrolling: false
      });
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
      dispatch,
      rootEntity,
      isNested,
      nameCondition,
      showConfirm,
      manualLink,
      submitSucceeded,
      syncErrors,
      toogleFullScreenEditView,
      form,
      asyncValidating,
      disabledSubmitCondition,
      hideTitle,
    } = this.props;

    const noTabList = document.getElementById(TAB_LIST_SCROLL_TARGET_ID) === null;

    const { hasScrolling } = this.state;

    const title = values && (nameCondition ? nameCondition(values) : values.name);

    this.updateTitle(title);

    const isDarkTheme = LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "dark";

    return (
      <Dialog
        fullScreen
        open={fullScreenEditView}
        TransitionComponent={Transition}
        classes={{
          paper: classes.fullEditViewBackground
        }}
        disableEnforceFocus
      >
        <LoadingIndicator position="fixed" />
        <form onSubmit={handleSubmit} autoComplete="off" noValidate>
          <AppBar
            elevation={0}
            className={clsx(
              classes.header,
              { [classes.headerAlternate]: hasScrolling }
            )}
          >
            <div className={clsx("flex-fill", classes.titleWrapper)}>
              {!hideTitle && (<FullScreenStickyHeader title={title} customStuck={hasScrolling} twoColumn disableInteraction />)}
            </div>
            <div>
              <div className={classes.actionsWrapper}>
                {manualLink && (
                  <AppBarHelpMenu
                    created={values ? new Date(values.createdOn) : null}
                    modified={values ? new Date(values.modifiedOn) : null}
                    auditsUrl={rootEntity !== "Audit" && `audit?search=~"${rootEntity}" and entityId in (${values ? values.id : 0})`}
                    manualUrl={manualLink}
                  />
                )}
              </div>
              <Button
                onClick={this.onCloseClick}
                className={clsx("closeAppBarButton", hasScrolling && classes.closeButtonAlternate)}
              >
                {$t('close')}
              </Button>
              <FormSubmitButton
                disabled={Boolean(asyncValidating) || (!creatingNew && !dirty) || disabledSubmitCondition}
                invalid={invalid}
                fab
                className={isDarkTheme && classes.submitButtonAlternate}
              />
            </div>
          </AppBar>
          <div
            className={clsx(classes.root, noTabList && "overflow-y-auto", !hideTitle && noTabList && "pt-1")}
            onScroll={noTabList ? this.onScroll : undefined}
          >
            <EditViewContent
              twoColumn
              onScroll={this.onScroll}
              asyncValidating={asyncValidating}
              syncErrors={syncErrors}
              submitSucceeded={submitSucceeded}
              invalid={invalid}
              onCloseClick={this.onCloseClick}
              manualLink={manualLink}
              rootEntity={rootEntity}
              isNested={isNested}
              form={form}
              isNew={creatingNew}
              values={values}
              updateDeleteCondition={updateDeleteCondition}
              dispatch={dispatch}
              dirty={dirty}
              showConfirm={showConfirm}
              toogleFullScreenEditView={toogleFullScreenEditView}
            />
          </div>
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

export default reduxForm<any, EditViewContainerProps>({
  destroyOnUnmount: false
})(
  connect(mapStateToProps, null)(withStyles(withRouter(FullScreenEditViewBase as any), styles))
);