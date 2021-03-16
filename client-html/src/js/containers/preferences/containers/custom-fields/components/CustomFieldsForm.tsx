import * as React from "react";
import ClassNames from "clsx";
import { withRouter } from "react-router";
import Grid from "@material-ui/core/Grid";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { withStyles, createStyles } from "@material-ui/core/styles";
import AddIcon from "@material-ui/icons/Add";
import Typography from "@material-ui/core/Typography";
import {
  Form, FieldArray, reduxForm, SubmissionError, arrayRemove, change, initialize
} from "redux-form";
import { CustomFieldType } from "@api/model";
import isEqual from "lodash.isequal";
import Fab from "@material-ui/core/Fab";

import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { formCommonStyles } from "../../../styles/formCommonStyles";
import CustomFieldsDeleteDialog from "./CustomFieldsDeleteDialog";
import CustomFieldsRenderer from "./CustomFieldsRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";
import { getCustomFields } from "../../../actions";
import { Fetch } from "../../../../../model/common/Fetch";
import uniqid from "../../../../../common/utils/uniqid";
import { State } from "../../../../../reducers/state";
import { setNextLocation } from "../../../../../common/actions";

const manualLink = getManualLink("generalPrefs_customFields");

const styles = () => createStyles({
  dragIcon: {
    fill: "#e0e0e0"
  },
  container: {
    width: "100%"
  }
});

const setOrder = items =>
  items.forEach((i, index) => {
    i.sortOrder = index;
  });

interface Props {
  data: any;
  classes: any;
  customFields: CustomFieldType[];
  created: Date;
  modified: Date;
  fetch: Fetch
  dispatch: any;
  handleSubmit: any;
  dirty: boolean;
  invalid: boolean;
  form: string;
  onDelete: (id: string) => void;
  onUpdate: (customFields: CustomFieldType[]) => void;
  openConfirm?: (onConfirm: any, confirmMessage?: string) => void;
  history?: any,
  nextLocation?: string,
  setNextLocation?: (nextLocation: string) => void,
}

class CustomFieldsBaseForm extends React.PureComponent<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  private onDeleteConfirm;

  constructor(props) {
    super(props);
    this.state = { fieldToDelete: null };
  }

  componentDidUpdate() {
    const { fetch } = this.props;

    if (this.isPending && fetch && fetch.success === false && this.rejectPromise) {
      this.rejectPromise(fetch.formError);
    }

    if (this.isPending && fetch && fetch.success && this.resolvePromise) {
      this.resolvePromise();
      this.isPending = false;
    }
  }

  findIndex = id => this.props.data.types.findIndex(item => item.id === id);

  getTouchedAndNew = items => {
    const initialLength = this.props.customFields.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.customFields[index]));

    return [...newItems, ...touchedItems];
  };

  onSave = value => {
    this.isPending = true;

    setOrder(value.types);

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;
      this.props.onUpdate(this.getTouchedAndNew(value.types));
    })
      .then(() => {
        const {
          nextLocation, history, setNextLocation, data
        } = this.props;

        this.props.dispatch(initialize("CustomFieldsForm", data));
        this.props.dispatch(getCustomFields());

        nextLocation && history.push(nextLocation);
        setNextLocation('');
      })
      .catch(error => {
        this.isPending = false;
        const errors: any = {
          types: []
        };

        if (error && error.id) {
          const index = this.findIndex(error.id);
          errors.types[index] = { [error.propertyName]: error.errorMessage };
          throw new SubmissionError(errors);
        }
      });
  };

  setFieldToDelete = field => {
    this.setState({ fieldToDelete: field });
  };

  onAddNew = () => {
    const {
      data: { types },
      dispatch
    } = this.props;
    const item = {} as CustomFieldType & { uniqid: string };

    item.id = null;
    item.name = null;
    item.defaultValue = null;
    item.fieldKey = null;
    item.mandatory = false;
    item.uniqid = uniqid();

    const updated = [item, ...types];

    updated.forEach((field, index) => (field.sortOrder = index));

    dispatch(change("CustomFieldsForm", "types", updated));
    const domNode = document.getElementById("types[0].name");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };

  onClickDelete = (item, index) => {
    const onConfirm = () => {
      if (item.id) {
        this.props.onDelete(item.id);
      } else {
        this.props.dispatch(arrayRemove("CustomFieldsForm", "types", index));
      }
    };

    if (!item.id) {
      onConfirm();
      return;
    }

    this.setFieldToDelete(item);
    this.onDeleteConfirm = onConfirm;
  };

  render() {
    const {
      classes, handleSubmit, data, dirty, dispatch, created, modified, invalid, form
    } = this.props;

    const { fieldToDelete } = this.state;

    return (
      <>
        <CustomFieldsDeleteDialog setFieldToDelete={this.setFieldToDelete} item={fieldToDelete} onConfirm={this.onDeleteConfirm} />
        <Form className={classes.container} onSubmit={handleSubmit(this.onSave)} noValidate autoComplete="off">
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
                <Typography variant="body1" color="inherit" noWrap className="appHeaderFontSize pl-2">
                  Custom Fields
                </Typography>

                <div className="flex-fill" />

                {data && (
                <AppBarHelpMenu
                  created={created}
                  modified={modified}
                  auditsUrl={`audit?search=~"CustomFieldType" and entityId in (${idsToString(data.types)})`}
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
            <Grid item lg={10}>
              {data && data.types && (
              <FieldArray
                name="types"
                component={CustomFieldsRenderer}
                dispatch={dispatch}
                onDelete={this.onClickDelete}
                classes={classes}
              />
            )}
            </Grid>
          </Grid>
        </Form>
      </>
);
  }
}

const mapStateToProps = (state: State) => ({
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

const CustomFieldsForm = reduxForm({
  onSubmitFail,
  form: "CustomFieldsForm"
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(theme => ({ ...formCommonStyles(theme), ...styles() }))(withRouter(CustomFieldsBaseForm) as any)));

export default CustomFieldsForm;
