/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import clsx from "clsx";
import * as React from "react";
import Grid from "@material-ui/core/Grid";
import AddIcon from "@material-ui/icons/Add";
import Typography from "@material-ui/core/Typography";
import { withRouter } from "react-router";
import {
  Form,
  arrayInsert,
  arrayRemove,
  FieldArray,
  getFormValues,
  initialize,
  reduxForm,
  SubmissionError
} from "redux-form";
import { connect } from "react-redux";
import { FundingSource } from "@api/model";
import isEqual from "lodash.isequal";
import Fab from "@material-ui/core/Fab";
import { Dispatch } from "redux";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { Fetch } from "../../../../../model/common/Fetch";
import FundingContractItem from "./FundingContractItem";
import { State } from "../../../../../reducers/state";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import * as Model from "../../../../../model/preferences/Licences";
import getTimestamps from "../../../../../common/utils/timestamps/getTimestamps";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";
import { ApiMethods } from "../../../../../model/common/apiHandlers";
import { setNextLocation } from "../../../../../common/actions";

interface Props {
  values: any;
  fundingContracts: FundingSource[];
  onSave: (items: FundingSource[], method?: ApiMethods) => void;
  onDelete: (id: number) => void;
  dirty: boolean;
  invalid: boolean;
  dispatch: any;
  handleSubmit: any;
  initialized: boolean;
  hasLicence: boolean;
  form: string;
  timestamps: Date[];
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
  fetch?: Fetch;
  history?: any;
  nextLocation?: string;
  setNextLocation?: (nextLocation: string) => void;
}

const Initial: FundingSource = {
  id: null,
  active: false,
  flavour: undefined,
  name: null
};

class FundingContractsForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);
    if (props.fundingContracts) {
      props.dispatch(initialize("FundingContractsForm", { fundingContracts: props.fundingContracts }));
    }
  }

  UNSAFE_componentWillReceiveProps(nextProps: Props) {
    // Initializing form with values
    if (nextProps.fundingContracts && !this.props.initialized) {
      this.props.dispatch(initialize("FundingContractsForm", { fundingContracts: nextProps.fundingContracts }));
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
    return this.props.values.fundingContracts.findIndex(item => item.id === id);
  }

  onAddNew = () => {
    this.props.dispatch(arrayInsert("FundingContractsForm", "fundingContracts", 0, Initial));
    const domNode = document.getElementById("fundingContracts[0].name");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };

  getTouchedAndNew = items => {
    if (!this.props.fundingContracts) {
      return items;
    }
    const initialLength = this.props.fundingContracts.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.fundingContracts[index]));

    return [...newItems, ...touchedItems];
  };

  onSave = value => {
    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      this.props.onSave(this.getTouchedAndNew(value.fundingContracts), this.props.hasLicence ? "POST" : "PATCH");
    })
      .then(() => {
        const { nextLocation, history, setNextLocation, dispatch } = this.props;
        dispatch(initialize("FundingContractsForm", { fundingContracts: this.props.fundingContracts }));

        nextLocation && history.push(nextLocation);
        setNextLocation('');
      })
      .catch(error => {
        this.isPending = false;

        const errors: any = {
          fundingContracts: []
        };

        if (error) {
          const index = this.findIndex(error.id);
          errors.fundingContracts[index] = { [error.propertyName]: error.errorMessage };
        }

        throw new SubmissionError(errors);
      });
  };

  onClickDelete = (item, index) => {
    const { onDelete, dispatch, openConfirm } = this.props;

    const onConfirm = () => {
      this.isPending = true;

      return new Promise((resolve, reject) => {
        this.resolvePromise = resolve;
        this.rejectPromise = reject;

        if (item.id) {
          onDelete(item.id);
        } else {
          dispatch(arrayRemove("FundingContractsForm", "fundingContracts", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            dispatch(initialize("FundingContractsForm", { fundingContracts: this.props.fundingContracts }));
          }
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm(onConfirm, "This item will be removed from funding contracts list", "DELETE");
  };

  render() {
    const {
     values, dirty, invalid, handleSubmit, hasLicence, timestamps, form
    } = this.props;
console.log('invalid', invalid)
    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className="centeredFlex relative">
              {hasLicence && (
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
              )}

              <Typography
                color="inherit"
                noWrap
                className={clsx("appHeaderFontSize",
                  hasLicence ? "pl-2" : undefined)}
              >
                Funding Contracts
              </Typography>

              <div className="flex-fill" />

              {values && (
                <AppBarHelpMenu
                  created={timestamps && timestamps[0]}
                  modified={timestamps && timestamps[1]}
                  auditsUrl={`audit?search=~"FundingSource" and entityId in (${idsToString(values.fundingContracts)})`}
                  manualUrl={`https://www.ish.com.au/s/onCourse/doc/${process.env.RELEASE_VERSION}/manual/generalPrefs.html#generalPrefs-fundingContracts`}
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
          <Grid item sm={12} lg={10} className="mt-1">
            {values && (
              <FieldArray
                name="fundingContracts"
                component={FundingContractItem}
                onDelete={this.onClickDelete}
                hasLicence={hasLicence}
              />
            )}
          </Grid>
        </Grid>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("FundingContractsForm")(state),
  fundingContracts: state.preferences.fundingContracts,
  timestamps: state.preferences.fundingContracts && getTimestamps(state.preferences.fundingContracts),
  fetch: state.fetch,
  hasLicence:
    state.preferences.licences && state.preferences.licences[Model.LicenseFundingContract.uniqueKey] === "true",
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export default reduxForm({ onSubmitFail, form: "FundingContractsForm" })(
  connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(FundingContractsForm))
) as React.ComponentClass<any>;
