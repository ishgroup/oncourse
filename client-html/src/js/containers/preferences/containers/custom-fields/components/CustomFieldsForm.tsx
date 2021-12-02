import * as React from "react";
import { withRouter } from "react-router";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
  Form, FieldArray, reduxForm, SubmissionError, arrayRemove, change, initialize
} from "redux-form";
import { CustomFieldType } from "@api/model";
import isEqual from "lodash.isequal";
import { withStyles, createStyles } from "@mui/styles";
import Grid from "@mui/material/Grid";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
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
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("generalPrefs_customFields");

const styles = theme => createStyles({
  dragIcon: {
    fill: "#e0e0e0"
  },
  container: {
    width: "100%"
  },
  expansionPanelRoot: {
    margin: "0px !important",
  },
  expansionPanelDetails: {
    padding: 0,
  },
  deleteButtonCustom: {
    top: theme.spacing(3),
  },
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

    props.dispatch(initialize("CustomFieldsForm", { "types": props.customFields }));
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
    const fistNewItemIndex = items.findIndex(item => item.id === null);

    return fistNewItemIndex === -1 ? items.filter((item, index) => !isEqual(item, this.props.customFields[index])) :
    [...items.slice(0, fistNewItemIndex).filter((item, index) => item.id != this.props.customFields[index].id) ,
      ...items.slice(fistNewItemIndex, items.length)];
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

          <AppBarContainer
            values={data}
            manualUrl={manualUrl}
            getAuditsUrl={() => `audit?search=~"CustomFieldType" and entityId in (${idsToString(data.types)})`}
            disabled={!dirty}
            invalid={invalid}
            title="Custom Fields"
            disableInteraction
            createdOn={() => created}
            modifiedOn={() => modified}
            onAddMenu={() => this.onAddNew()}
          >
            <Grid container className="mt-2">
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
          </AppBarContainer>
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
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(
  withStyles(theme => ({ ...formCommonStyles(theme), ...styles(theme) }))(withRouter(CustomFieldsBaseForm) as any)
));

export default CustomFieldsForm;
