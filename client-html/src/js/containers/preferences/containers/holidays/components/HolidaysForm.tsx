/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import ClassNames from "clsx";
import Grid from "@material-ui/core/Grid";
import { withStyles, createStyles } from "@material-ui/core/styles";
import AddIcon from "@material-ui/icons/Add";
import Typography from "@material-ui/core/Typography";
import Fab from "@material-ui/core/Fab";
import isEqual from "lodash.isequal";
import {
  FieldArray,
  getFormValues,
  reduxForm,
  arrayInsert,
  arrayRemove,
  initialize,
  SubmissionError
} from "redux-form";
import { connect } from "react-redux";
import { Holiday, RepeatEndEnum, RepeatEnum } from "@api/model";
import { addHours } from "date-fns";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../../reducers/state";
import AvailabilityRenderer from "../../../../../common/components/form/availabilityComponent/AvailabilityRenderer";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import getTimestamps from "../../../../../common/utils/timestamps/getTimestamps";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";

const styles = theme =>
  createStyles({
    container: {
      margin: theme.spacing(-3)
    }
  });

const manualUrl = getManualLink("generalPrefs_holidays");

interface Props {
  values: any;
  holidays: Holiday[];
  onSave: (items: Holiday[]) => void;
  onDelete: (id: number) => void;
  timestamps: Date[];
  classes: any;
  initialized: boolean;
  invalid: boolean;
  dirty: boolean;
  dispatch: any;
  form: string;
  getFormState: any;
  handleSubmit: any;
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
}

class HolidaysBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    if (props.holidays) {
      props.dispatch(initialize("HolidaysForm", { holidays: props.holidays }));
    }
  }

  componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (nextProps.holidays && !this.props.initialized) {
      this.props.dispatch(initialize("HolidaysForm", { holidays: nextProps.holidays }));
    }
    if (!this.isPending) {
      return;
    }
    if (nextProps.fetch && nextProps.fetch.success === false) {
      this.rejectPromise(nextProps.fetch.formError);
    }
    if (nextProps.fetch && nextProps.fetch.success) {
      this.resolvePromise();
      this.isPending = false;
    }
  }

  findIndex(id) {
    return this.props.values.holidays.findIndex(item => item.id === id);
  }

  onAddNew = () => {
    const item = {} as Holiday;
    item.id = null;
    item.description = undefined;
    item.startDateTime = new Date().toISOString();
    item.endDateTime = addHours(new Date(), 1).toISOString();
    item.repeatEnd = RepeatEndEnum.never;
    item.repeat = RepeatEnum.none;
    item.repeatEndAfter = 0;
    item.repeatOn = undefined;

    this.props.dispatch(arrayInsert("HolidaysForm", "holidays", 0, item));
    const domNode = document.getElementById("holidays[0].description");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };

  getTouchedAndNew = items => {
    if (!this.props.holidays) {
      return items;
    }
    const initialLength = this.props.holidays.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.holidays[index]));

    return [...newItems, ...touchedItems];
  };

  onSave = value => {
    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      this.props.onSave(this.getTouchedAndNew(value.holidays));
    })
      .then(() => {
        this.props.dispatch(initialize("HolidaysForm", { holidays: this.props.holidays }));
      })
      .catch(error => {
        this.isPending = false;

        const errors: any = {
          holidays: []
        };

        if (error) {
          const index = this.findIndex(error.id);
          errors.holidays[index] = { [error.propertyName]: error.errorMessage };
        }

        throw new SubmissionError(errors);
      });
  };

  onClickDelete = (item, index) => {
    const { onDelete, openConfirm } = this.props;

    const onConfirm = () => {
      this.isPending = true;

      return new Promise((resolve, reject) => {
        this.resolvePromise = resolve;
        this.rejectPromise = reject;

        if (item.id) {
          onDelete(item.id);
        } else {
          this.props.dispatch(arrayRemove("HolidaysForm", "holidays", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize("HolidaysForm", { holidays: this.props.holidays }));
          }
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm(onConfirm, "This item will be removed from holidays list", "DELETE");
  };

  render() {
    const {
      classes, handleSubmit, values, dirty, invalid, timestamps, dispatch, form
    } = this.props;
    const created = timestamps && timestamps[0];
    const modified = timestamps && timestamps[1];

    return (
      <form className="mt-2" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className={ClassNames("centeredFlex", "relative")}>
              <Fab
                type="button"
                size="small"
                color="primary"
                classes={{
                  sizeSmall: "appBarFab"
                }}
                onClick={this.onAddNew}
              >
                <AddIcon />
              </Fab>
              <Typography color="inherit" noWrap className="appHeaderFontSize pl-2">
                Holidays
              </Typography>

              <div className="flex-fill" />

              {values && (
                <AppBarHelpMenu
                  created={created}
                  modified={modified}
                  auditsUrl={`audit?search=~"UnavailableRule" and entityId in (${idsToString(values.holidays)})`}
                  manualUrl={manualUrl}
                />
              )}

              <FormSubmitButton
                disabled={!dirty}
                invalid={invalid}
              />
            </Grid>
          </Grid>
        </CustomAppBar>

        <Grid container>
          <Grid item sm={12} lg={10} className={classes.container}>
            {values && (
              <FieldArray
                name="holidays"
                component={AvailabilityRenderer}
                onDelete={this.onClickDelete}
                dispatch={dispatch}
                rerenderOnEveryChange
              />
            )}
          </Grid>
        </Grid>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  timestamps: state.preferences.holidays && getTimestamps(state.preferences.holidays),
  holidays: state.preferences.holidays,
  values: getFormValues("HolidaysForm")(state),
  fetch: state.fetch
});

const HolidaysForm = reduxForm({
  onSubmitFail,
  form: "HolidaysForm"
})(connect<any, any, any>(mapStateToProps, null)(withStyles(styles)(HolidaysBaseForm)));

export default HolidaysForm;
