import * as React from "react";
import ClassNames from "clsx";
import { withRouter } from "react-router";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import Grid from "@material-ui/core/Grid";
import withStyles from "@material-ui/core/styles/withStyles";
import AddIcon from "@material-ui/icons/Add";
import Typography from "@material-ui/core/Typography";
import Fab from "@material-ui/core/Fab";
import {
  Form, FieldArray, reduxForm, initialize, SubmissionError, arrayInsert, arrayRemove
} from "redux-form";
import { ContactRelationType } from "@api/model";
import isEqual from "lodash.isequal";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import ContactRelationTypesRenderer from "./ContactRelationTypesRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";
import { State } from "../../../../../reducers/state";
import { setNextLocation } from "../../../../../common/actions";
import { cardsFormStyles } from "../../../styles/formCommonStyles";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";

const manualLink = getManualLink("generalPrefs_contactRelationTypes");

interface Props {
  data: any;
  classes: any;
  contactRelationTypes: ContactRelationType[];
  created: Date;
  modified: Date;
  dispatch: any;
  handleSubmit: any;
  dirty: boolean;
  invalid: boolean;
  form: string;
  onDelete: (id: string) => void;
  onUpdate: (contactRelationTypes: ContactRelationType[]) => void;
  openConfirm?: ShowConfirmCaller;
  history?: any,
  nextLocation?: string,
  setNextLocation?: (nextLocation: string) => void,
}

class ContactRelationTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize("ContactRelationTypesForm", { types: props.contactRelationTypes }));
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
    const initialLength = this.props.contactRelationTypes.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.contactRelationTypes[index]));

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

        this.props.dispatch(initialize("ContactRelationTypesForm", { types: this.props.contactRelationTypes }));

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
    const item = {} as ContactRelationType;
    item.id = null;
    item.relationName = "";
    item.reverseRelationName = "";
    item.portalAccess = false;
    item.systemType = false;

    this.props.dispatch(arrayInsert("ContactRelationTypesForm", "types", 0, item));
    const domNode = document.getElementById("types[0].relationName");
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
          this.props.dispatch(arrayRemove("ContactRelationTypesForm", "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize("ContactRelationTypesForm", { types: this.props.contactRelationTypes }));
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
                Contact Relation Types
              </Typography>

              <div className="flex-fill" />

              {data && (
                <AppBarHelpMenu
                  created={created}
                  modified={modified}
                  auditsUrl={`audit?search=~"ContactRelationType" and entityId in (${idsToString(data.types)})`}
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
                  component={ContactRelationTypesRenderer}
                  onDelete={this.onClickDelete}
                  classes={classes}
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

const ContactRelationTypesForm = reduxForm({
  onSubmitFail,
  form: "ContactRelationTypesForm"
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(cardsFormStyles)(withRouter(ContactRelationTypesBaseForm)) as any));

export default ContactRelationTypesForm;
