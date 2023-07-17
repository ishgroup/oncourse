/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
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
import Grid from "@mui/material/Grid";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import { Fetch } from "../../../../../model/common/Fetch";
import FundingContractItem from "./FundingContractItem";
import { State } from "../../../../../reducers/state";
import getTimestamps from "../../../../../common/utils/timestamps/getTimestamps";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";
import { ApiMethods } from "../../../../../model/common/apiHandlers";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { getManualLink } from "../../../../../common/utils/getManualLink";

const manualUrl = getManualLink("generalPrefs-fundingContractsPrefs");

export const FUNDING_CONTRACTS_FORM: string = "FundingContractsForm";

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
  form: string;
  timestamps: Date[];
  openConfirm?: ShowConfirmCaller;
  fetch?: Fetch;
  history?: any;
  nextLocation?: string;
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
      props.dispatch(initialize(FUNDING_CONTRACTS_FORM, { fundingContracts: props.fundingContracts }));
    }
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps: Props) {
    // Initializing form with values
    if (nextProps.fundingContracts && !this.props.initialized) {
      this.props.dispatch(initialize(FUNDING_CONTRACTS_FORM, { fundingContracts: nextProps.fundingContracts }));
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
    this.props.dispatch(arrayInsert(FUNDING_CONTRACTS_FORM, "fundingContracts", 0, Initial));
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

      this.props.onSave(this.getTouchedAndNew(value.fundingContracts), "POST");
    })
      .then(() => {
        const {
          nextLocation, history, dispatch
        } = this.props;
        dispatch(initialize(FUNDING_CONTRACTS_FORM, { fundingContracts: this.props.fundingContracts }));

        nextLocation && history.push(nextLocation);

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
          dispatch(arrayRemove(FUNDING_CONTRACTS_FORM, "fundingContracts", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            dispatch(initialize(FUNDING_CONTRACTS_FORM, { fundingContracts: this.props.fundingContracts }));
          }
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm({ onConfirm, confirmMessage: "This item will be removed from funding contracts list", confirmButtonText: "DELETE" });
  };

  render() {
    const {
      values, dirty, invalid, handleSubmit, timestamps, form
    } = this.props;

    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)} role={FUNDING_CONTRACTS_FORM}>
        <RouteChangeConfirm form={form} when={dirty} />
        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl={() => `audit?search=~"FundingSource" and entityId in (${idsToString(values.fundingContracts)})`}
          disabled={!dirty}
          invalid={invalid}
          title="Funding Contracts"
          disableInteraction
          createdOn={() => timestamps && timestamps[0]}
          modifiedOn={() => timestamps && timestamps[1]}
          onAddMenu={() => this.onAddNew()}
        >
          <Grid container className="mt-2">
            <Grid item sm={12} lg={10}>
              {values && (
                <FieldArray
                  name="fundingContracts"
                  component={FundingContractItem}
                  onDelete={this.onClickDelete}
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
  values: getFormValues(FUNDING_CONTRACTS_FORM)(state),
  fundingContracts: state.preferences.fundingContracts,
  timestamps: state.preferences.fundingContracts && getTimestamps(state.preferences.fundingContracts),
  fetch: state.fetch,
});


export default reduxForm({
  onSubmitFail,
  form: FUNDING_CONTRACTS_FORM
})(connect<any, any, any>(mapStateToProps, null)(withRouter(FundingContractsForm))) as React.ComponentClass<any>;
