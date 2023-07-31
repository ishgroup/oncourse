import * as React from "react";
import { withRouter } from "react-router";
import { connect } from "react-redux";
import {
  Form, FieldArray, reduxForm, SubmissionError, arrayRemove, change, initialize
} from "redux-form";
import { CustomFieldType } from "@api/model";
import isEqual from "lodash.isequal";
import { withStyles, createStyles } from "@mui/styles";
import Grid from "@mui/material/Grid";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import { formCommonStyles } from "../../../styles/formCommonStyles";
import CustomFieldsDeleteDialog from "./CustomFieldsDeleteDialog";
import CustomFieldsRenderer from "./CustomFieldsRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "ish-ui";
import { getCustomFields } from "../../../actions";
import { Fetch } from "../../../../../model/common/Fetch";
import uniqid from "../../../../../common/utils/uniqid";
import { State } from "../../../../../reducers/state";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("generalPrefs_customFields");

export const CUSTOM_FIELDS_FORM: string = "CustomFieldsForm";

const styles = theme => createStyles({
  dragIcon: {
    fill: "#e0e0e0"
  },
  container: {
    width: "100%"
  },
  expansionPanelRoot: {
    margin: "0px !important",
    width: "100%",
  },
  expansionPanelDetails: {
    padding: 0,
    marginTop: `-${theme.spacing(9.75)}`,
  },
  expandIcon: {
    marginTop: -5,
    "& > button": {
      padding: 5,
    }
  },
  deleteButtonCustom: {
    padding: theme.spacing(1),
    marginTop: -5,
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
  history?: any,
  nextLocation?: string
}

class CustomFieldsBaseForm extends React.PureComponent<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  private onDeleteConfirm;

  constructor(props) {
    super(props);
    this.state = { fieldToDelete: null };

    props.dispatch(initialize(CUSTOM_FIELDS_FORM, { "types": props.customFields }));
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
          nextLocation, history, data
        } = this.props;

        this.props.dispatch(initialize(CUSTOM_FIELDS_FORM, data));
        this.props.dispatch(getCustomFields());

        nextLocation && history.push(nextLocation);
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

    dispatch(change(CUSTOM_FIELDS_FORM, "types", updated));
    setTimeout(() => {
      const domNode = document.getElementById("types[0].name");
      if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
    }, 200);
  };

  onClickDelete = (item, index) => {
    const onConfirm = () => {
      if (item.id) {
        this.props.onDelete(item.id);
      } else {
        this.props.dispatch(arrayRemove(CUSTOM_FIELDS_FORM, "types", index));
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
        <Form className={classes.container} onSubmit={handleSubmit(this.onSave)} noValidate autoComplete="off" role={CUSTOM_FIELDS_FORM}>
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

const CustomFieldsForm = reduxForm({
  onSubmitFail,
  form: CUSTOM_FIELDS_FORM
})(connect<any, any, any>(mapStateToProps, null)(
  withStyles(theme => ({ ...formCommonStyles(theme), ...styles(theme) }))(withRouter(CustomFieldsBaseForm) as any)
));

export default CustomFieldsForm;