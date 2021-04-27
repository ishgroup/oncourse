import * as React from "react";
import ClassNames from "clsx";
import Grid from "@material-ui/core/Grid";
import withStyles from "@material-ui/core/styles/withStyles";
import { withRouter } from "react-router";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import AddIcon from "@material-ui/icons/Add";
import Typography from "@material-ui/core/Typography";
import Fab from "@material-ui/core/Fab";
import {
  Form, FieldArray, reduxForm, initialize, SubmissionError, arrayInsert, arrayRemove
} from "redux-form";
import { EntityRelationType } from "@api/model";
import isEqual from "lodash.isequal";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import EntityRelationTypesRenderer from "./EntityRelationTypesRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../../reducers/state";
import { setNextLocation } from "../../../../../common/actions";
import { cardsFormStyles } from "../../../styles/formCommonStyles";

const manualLink = getManualLink("generalPrefs_sellableItemsRelationTypes");

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
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
  history?: any,
  nextLocation?: string,
  setNextLocation?: (nextLocation: string) => void,
}

class EntityRelationTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize("EntityRelationTypesForm", { types: props.entityRelationTypes }));
  }

  componentWillReceiveProps(nextProps) {
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
        const { nextLocation, history, setNextLocation } = this.props;

        this.props.dispatch(initialize("EntityRelationTypesForm", { types: this.props.entityRelationTypes }));

        nextLocation && history.push(nextLocation);
        setNextLocation('');
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

    this.props.dispatch(arrayInsert("EntityRelationTypesForm", "types", 0, item));
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
          this.props.dispatch(arrayRemove("EntityRelationTypesForm", "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize("EntityRelationTypesForm", { types: this.props.entityRelationTypes }));
          }
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm(onConfirm, "This item will be removed from types list", "DELETE");
  };

  render() {
    const {
      classes, handleSubmit, data, dirty, created, modified, invalid, discountsMap, form
    } = this.props;

    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)}>
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
                onClick={() => this.onAddNew()}
              >
                <AddIcon />
              </Fab>
              <Typography className="appHeaderFontSize pl-2" variant="body1" color="inherit" noWrap>
                Sellable items relation types
              </Typography>

              <div className="flex-fill" />

              {data && (
                <AppBarHelpMenu
                  created={created}
                  modified={modified}
                  auditsUrl={`audit?search=~"EntityRelationType" and entityId in (${idsToString(data.types)})`}
                  manualUrl={manualLink}
                />
              )}

              <FormSubmitButton
                disabled={!dirty}
                invalid={invalid}
              />
            </Grid>
          </Grid>
        </CustomAppBar>

        <Grid container className={classes.marginTop}>
          <Grid item sm={12} lg={10}>
            <Grid container>
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
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

const EntityRelationTypesForm = reduxForm({
  onSubmitFail,
  form: "EntityRelationTypesForm"
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(cardsFormStyles)(withRouter(EntityRelationTypesBaseForm) as any)));

export default EntityRelationTypesForm;
