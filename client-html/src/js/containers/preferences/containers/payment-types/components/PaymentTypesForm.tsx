import { PaymentMethod } from "@api/model";
import Grid from "@mui/material/Grid";
import { withStyles } from "@mui/styles";
import { idsToString, ShowConfirmCaller } from "ish-ui";
import isEqual from "lodash.isequal";
import * as React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router";
import { arrayInsert, arrayRemove, FieldArray, Form, initialize, reduxForm, SubmissionError } from "redux-form";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import { State } from "../../../../../reducers/state";
import { formCommonStyles } from "../../../styles/formCommonStyles";
import PaymentTypesRenderer from "./PaymentTypesRenderer";

const manualUrl = getManualLink("generalPrefs_paymentTypes");

export const PAYMENT_TYPES_FORM: string = "PaymentTypesForm";

interface Props {
  created: Date;
  modified: Date;
  data: any;
  classes: any;
  paymentTypes: PaymentMethod[];
  dispatch: any;
  handleSubmit: any;
  dirty: boolean;
  invalid: boolean;
  onDelete: (id: string) => void;
  onUpdate: (paymentTypes: PaymentMethod[]) => void;
  assetAccounts: any;
  touched: any;
  form: string;
  reset: () => void;
  openConfirm?: ShowConfirmCaller;
  history?: any;
  nextLocation?: string;
}

class PaymentTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize(PAYMENT_TYPES_FORM, { types: props.paymentTypes }));
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
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

  findIndex = id => this.props.data.types.findIndex(item => item.id === id);

  getTouchedAndNew = items => {
    const initialLength = this.props.paymentTypes.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.paymentTypes[index]));

    return [...newItems, ...touchedItems];
  };

  onSave = value => {
    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      this.props.onUpdate(this.getTouchedAndNew(value.types));
    })
      .then(() => {
        const { nextLocation, history } = this.props;

        this.props.dispatch(initialize(PAYMENT_TYPES_FORM, { types: this.props.paymentTypes }));

        nextLocation && history.push(nextLocation);
      })
      .catch(error => {
        this.isPending = false;

        const errors: any = {
          types: []
        };

        if (error) {
          const index = this.findIndex(error.id);
          errors.types[index] = { [error.propertyName]: error.errorMessage };
        }

        throw new SubmissionError(errors);
      });
  };

  onAddNew = () => {
    const item = {} as PaymentMethod;
    item.id = null;
    item.name = "";
    item.systemType = false;
    item.active = false;
    item.reconcilable = false;
    item.bankedAuto = false;
    item.accountId = null;
    item.undepositAccountId = null;

    this.props.dispatch(arrayInsert(PAYMENT_TYPES_FORM, "types", 0, item));
    const domNode = document.getElementById("types[0].name");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
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
          this.props.dispatch(arrayRemove(PAYMENT_TYPES_FORM, "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize(PAYMENT_TYPES_FORM, { types: this.props.paymentTypes }));
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
     classes, handleSubmit, data, assetAccounts, dirty, created, modified, invalid, form
    } = this.props;

    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)} role={PAYMENT_TYPES_FORM}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={() => `audit?search=~"PaymentMethod" and entityId in (${idsToString(data.types)})`}
          disabled={!dirty}
          invalid={invalid}
          title="Payment Types"
          disableInteraction
          createdOn={() => created}
          modifiedOn={() => modified}
          onAddMenu={() => this.onAddNew()}
        >
          <Grid container className="mt-2">
            <Grid item sm={12} lg={10}>
              <Grid container>
                {data && (
                  <FieldArray
                    name="types"
                    assetAccounts={assetAccounts}
                    component={PaymentTypesRenderer}
                    onDelete={this.onClickDelete}
                    classes={classes}
                  />
                )}
              </Grid>
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  nextLocation: state.nextLocation
});


const PaymentTypesForm = reduxForm({
  onSubmitFail,
  form: PAYMENT_TYPES_FORM
})(connect<any, any, any>(mapStateToProps, null)(withStyles(formCommonStyles)(withRouter(PaymentTypesBaseForm)) as any));

export default PaymentTypesForm;
