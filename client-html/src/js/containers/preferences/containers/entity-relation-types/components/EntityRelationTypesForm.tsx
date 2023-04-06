import * as React from "react";
import Grid from "@mui/material/Grid";
import withStyles from "@mui/styles/withStyles";
import { withRouter } from "react-router";
import { connect } from "react-redux";
import {
  Form, FieldArray, reduxForm, initialize, SubmissionError, arrayInsert, arrayRemove
} from "redux-form";
import { EntityRelationType } from "@api/model";
import isEqual from "lodash.isequal";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import EntityRelationTypesRenderer from "./EntityRelationTypesRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../../reducers/state";
import { cardsFormStyles } from "../../../styles/formCommonStyles";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("generalPrefs_sellableItemsRelationTypes");

export const ENTITY_RELATION_TYPES_FORM: string = "EntityRelationTypesForm";

interface Props {
  data: any;
  classes: any;
  entityRelationTypes: EntityRelationType[];
  discountsMap: any;
  created: Date;
  modified: Date;
  dispatch: any;
  handleSubmit: any;
  dirty: boolean;
  invalid: boolean;
  form: string;
  onDelete: (id: string) => void;
  onUpdate: (entityRelationTypes: EntityRelationType[]) => void;
  openConfirm?: ShowConfirmCaller;
  history?: any,
  nextLocation?: string
}

class EntityRelationTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize(ENTITY_RELATION_TYPES_FORM, { types: props.entityRelationTypes }));
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
    const initialLength = this.props.entityRelationTypes.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.entityRelationTypes[index]));

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

        this.props.dispatch(initialize(ENTITY_RELATION_TYPES_FORM, { types: this.props.entityRelationTypes }));

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
    const item = {} as EntityRelationType;
    item.id = null;
    item.name = "";
    item.toName = "";
    item.fromName = "";
    item.description = "";
    item.shoppingCart = null;
    item.discountId = null;
    item.isShownOnWeb = true;
    item.considerHistory = false;

    this.props.dispatch(arrayInsert(ENTITY_RELATION_TYPES_FORM, "types", 0, item));
    const domNode = document.getElementById("types[0].toName");
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
          this.props.dispatch(arrayRemove(ENTITY_RELATION_TYPES_FORM, "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize(ENTITY_RELATION_TYPES_FORM, { types: this.props.entityRelationTypes }));
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
      classes, handleSubmit, data, dirty, created, modified, invalid, discountsMap, form
    } = this.props;

    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)} role={ENTITY_RELATION_TYPES_FORM}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={() => `audit?search=~"EntityRelationType" and entityId in (${idsToString(data.types)})`}
          disabled={!dirty}
          invalid={invalid}
          title="Sellable items relation types"
          disableInteraction
          createdOn={() => created}
          modifiedOn={() => modified}
          onAddMenu={() => this.onAddNew()}
        >
          <Grid container className="mt-2">
            <Grid item sm={12} lg={10}>
              <Grid container columnSpacing={3}>
                {data && (
                  <FieldArray
                    name="types"
                    component={EntityRelationTypesRenderer}
                    onDelete={this.onClickDelete}
                    classes={classes}
                    discounts={discountsMap}
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

const EntityRelationTypesForm = reduxForm({
  onSubmitFail,
  form: ENTITY_RELATION_TYPES_FORM
})(connect<any, any, any>(mapStateToProps, null)(
  withStyles(cardsFormStyles)(withRouter(EntityRelationTypesBaseForm) as any)
));

export default EntityRelationTypesForm;
