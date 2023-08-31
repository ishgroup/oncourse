/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Holiday, RepeatEndEnum, RepeatEnum } from "@api/model";
import Grid from "@mui/material/Grid";
import { addHours } from "date-fns";
import { idsToString, ShowConfirmCaller } from "ish-ui";
import isEqual from "lodash.isequal";
import * as React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router";
import {
  arrayInsert,
  arrayRemove,
  FieldArray,
  Form,
  getFormValues,
  initialize,
  reduxForm,
  SubmissionError
} from "redux-form";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import AvailabilityRenderer from "../../../../../common/components/form/availabilityComponent/AvailabilityRenderer";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import getTimestamps from "../../../../../common/utils/timestamps/getTimestamps";
import { State } from "../../../../../reducers/state";

const manualUrl = getManualLink("generalPrefs_holidays");

export const HOLIDAYS_FORM: string = "HolidaysForm";

interface Props {
  values: any;
  holidays: Holiday[];
  onSave: (items: Holiday[]) => void;
  onDelete: (id: number) => void;
  initialized: boolean;
  invalid: boolean;
  dirty: boolean;
  dispatch: any;
  form: string;
  getFormState: any;
  handleSubmit: any;
  history: any;
  openConfirm?: ShowConfirmCaller;
  nextLocation?: string,
}

class HolidaysBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    if (props.holidays) {
      props.dispatch(initialize(HOLIDAYS_FORM, { holidays: props.holidays }));
    }
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (nextProps.holidays && !this.props.initialized) {
      this.props.dispatch(initialize(HOLIDAYS_FORM, { holidays: nextProps.holidays }));
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

    this.props.dispatch(arrayInsert(HOLIDAYS_FORM, "holidays", 0, item));
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
        const { history, nextLocation } = this.props;
        this.props.dispatch(initialize(HOLIDAYS_FORM, { holidays: this.props.holidays }));

        nextLocation && history.push(nextLocation);
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
          this.props.dispatch(arrayRemove(HOLIDAYS_FORM, "holidays", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize(HOLIDAYS_FORM, { holidays: this.props.holidays }));
          }
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm({ onConfirm, confirmMessage: "This item will be removed from holidays list", confirmButtonText: "DELETE" });
  };

  render() {
    const {
      handleSubmit, values, dirty, invalid, holidays, dispatch, form
    } = this.props;

    const timestamps = holidays && getTimestamps(holidays);
    const created = timestamps && timestamps[0];
    const modified = timestamps && timestamps[1];

    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)} role={HOLIDAYS_FORM}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl={() => `audit?search=~"UnavailableRule" and entityId in (${idsToString(values.holidays)})`}
          disabled={!dirty}
          invalid={invalid}
          title="Holidays"
          disableInteraction
          createdOn={() => created}
          modifiedOn={() => modified}
          onAddMenu={() => this.onAddNew()}
        >
          <Grid container className="mt-2">
            <Grid item sm={12} lg={10}>
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
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  holidays: state.preferences.holidays,
  values: getFormValues(HOLIDAYS_FORM)(state),
  fetch: state.fetch,
  nextLocation: state.nextLocation
});

const HolidaysForm = reduxForm({
  onSubmitFail,
  form: HOLIDAYS_FORM
})(connect<any, any, any>(mapStateToProps, null)(
  withRouter(HolidaysBaseForm)
));

export default HolidaysForm;
