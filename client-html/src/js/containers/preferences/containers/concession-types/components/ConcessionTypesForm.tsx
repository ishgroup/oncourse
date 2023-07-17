import * as React from "react";
import { connect } from "react-redux";
import {
  Form, FieldArray, reduxForm, initialize, SubmissionError, arrayInsert, arrayRemove
} from "redux-form";
import { withRouter } from "react-router";
import isEqual from "lodash.isequal";
import { ConcessionType } from "@api/model";
import Grid from "@mui/material/Grid";
import withStyles from "@mui/styles/withStyles";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import ConcessionTypesRenderer from "./ConcessionTypesRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../../reducers/state";
import { cardsFormStyles } from "../../../styles/formCommonStyles";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("generalPrefs_concessionTypes");

export const CONCESSION_TYPES_FORM: string = "ConcessionTypesForm";

interface Props {
  data: any;
  classes: any;
  concessionTypes: ConcessionType[];
  created: Date;
  modified: Date;
  dispatch: any;
  handleSubmit: any;
  dirty: boolean;
  invalid: boolean;
  onDelete: (id: string) => void;
  form: string;
  onUpdate: (concessionTypes: ConcessionType[]) => void;
  openConfirm?: ShowConfirmCaller;
  history?: any,
  nextLocation?: string
}

class ConcessionTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise: any;

  private rejectPromise: any;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize(CONCESSION_TYPES_FORM, { types: props.concessionTypes }));
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
    const initialLength = this.props.concessionTypes.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.concessionTypes[index]));

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

        this.props.dispatch(initialize(CONCESSION_TYPES_FORM, { types: this.props.concessionTypes }));

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
    const item = {} as ConcessionType;
    item.id = null;
    item.name = "";
    item.requireExpary = false;
    item.requireNumber = false;
    item.allowOnWeb = false;

    this.props.dispatch(arrayInsert(CONCESSION_TYPES_FORM, "types", 0, item));
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
          this.props.dispatch(arrayRemove(CONCESSION_TYPES_FORM, "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize(CONCESSION_TYPES_FORM, { types: this.props.concessionTypes }));
          }
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm({ onConfirm, confirmMessage: "This item will be removed from types list", confirmButtonText: "DELETE" });
  };

  render() {
    const {
      classes, handleSubmit, data, dirty, created, modified, invalid, form
    } = this.props;

    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)} role={CONCESSION_TYPES_FORM}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={() => `audit?search=~"ConcessionType" and entityId in (${idsToString(data.types)}})`}
          disabled={!dirty}
          invalid={invalid}
          title="Concession Types"
          disableInteraction
          createdOn={() => created}
          modifiedOn={() => modified}
          onAddMenu={() => this.onAddNew()}
        >
          <Grid container className={classes.marginTop}>
            <Grid item sm={12} lg={10}>
              <Grid container columnSpacing={3}>
                {data && (
                  <FieldArray
                    name="types"
                    component={ConcessionTypesRenderer}
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
  nextLocation: state.nextLocation,
});

const ConcessionTypesForm = reduxForm({
  onSubmitFail,
  form: CONCESSION_TYPES_FORM
})(connect<any, any, any>(mapStateToProps, null)(withStyles(cardsFormStyles)(withRouter(ConcessionTypesBaseForm)) as any));

export default ConcessionTypesForm;
