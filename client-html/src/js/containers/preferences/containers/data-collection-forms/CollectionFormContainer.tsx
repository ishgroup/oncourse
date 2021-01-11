/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DeliveryScheduleType } from "@api/model";
import Divider from "@material-ui/core/Divider";
import Grid from "@material-ui/core/Grid";
import { createStyles, withStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import DeleteForever from "@material-ui/icons/DeleteForever";
import FileCopy from "@material-ui/icons/FileCopy";
import clsx from "clsx";
import * as React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { Dispatch } from "redux";
import {
  change, FieldArray, getFormValues, initialize, reduxForm, SubmissionError
} from "redux-form";
import RouteChangeConfirm from "../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarActions from "../../../../common/components/form/AppBarActions";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import FormField from "../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import { mapSelectItems, sortDefaultSelectItems } from "../../../../common/utils/common";
import { getManualLink } from "../../../../common/utils/getManualLink";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
import { State } from "../../../../reducers/state";
import { createDataCollectionForm, deleteDataCollectionForm, updateDataCollectionForm } from "../../actions";
import renderCollectionFormFields from "./components/CollectionFormFieldsRenderer";
import CollectionFormFieldTypesMenu from "./components/CollectionFormFieldTypesMenu";

const manualLink = getManualLink("dataCollection");

const deliveryScheduleTypes = Object.keys(DeliveryScheduleType).map(mapSelectItems);

deliveryScheduleTypes.sort(sortDefaultSelectItems);

const styles = theme => createStyles({
  mainContainer: {
    margin: theme.spacing(-3),
    height: `calc(100% + ${theme.spacing(6)}px)`
  },
  headerControlsContainer: {
    margin: `-12px 0 0 ${theme.spacing(1)}px`
  },
  heading: {
    marginTop: "50px",
    "&:first-child": {
      marginTop: "0"
    }
  },
  selectField: {
    minWidth: "155px"
  },
  boxShadowHover: {
    "&:hover": {
      boxShadow: theme.shadows[2]
    }
  },
  boxShadow: {
    boxShadow: theme.shadows[2],
    background: theme.palette.background.default
  },
  HeaderTextField: {
    marginLeft: "20px"
  }
});

const setParents = targets => {
  let parent = null;
  targets.forEach((item: any) => {
    if (item.baseType === "field") {
      item.parent = parent;
    }
    if (item.baseType === "heading") {
      parent = item.name;
    }
  });

  return targets;
};

class CollectionFormContainer extends React.Component<any, any> {
  private resolvePromise;

  private rejectPromise;

  private unlisten;

  private isPending: boolean;

  private skipValidation: boolean;

  private formRef: HTMLFormElement;

  constructor(props) {
    super(props);
    this.state = {
      disableConfirm: false
    };

    if (props.match.params.action === "edit" && props.collectionForms) {
      const currentForm = this.getCollectionForm(props);
      const items = this.parseFormData(currentForm);
      const state = {
        items,
        form: currentForm
      };
      this.props.dispatch(initialize("DataCollectionForm", state));
    }

    if (props.match.params.action === "new") {
      const state = {
        items: [],
        form: {
          id: null,
          type: this.props.match.params.type
        }
      };
      this.props.dispatch(initialize("DataCollectionForm", state));
    }
  }

  getFormRef = node => {
    this.formRef = node;
  };

  componentDidMount() {
    this.unlisten = this.props.history.listen(location => {
      this.onHistoryChange(location);
    });
  }

  componentDidUpdate(prevProps) {
    if (this.props.collectionForms) {
      const isExactForm = this.props.match.params.id === prevProps.match.params.id;
      const isEditing = this.props.match.params.action === "edit";
      const currentForm = this.getCollectionForm(this.props);

      if ((isEditing && !this.props.collectionForms) || (isEditing && (!isExactForm || !prevProps.collectionForms))) {
        if (!currentForm) {
          return;
        }
        const items = this.parseFormData(currentForm);
        const state = {
          items,
          form: currentForm
        };
        this.props.dispatch(initialize("DataCollectionForm", state));
      }
    }

    if (!this.isPending) {
      return;
    }
    if (this.props.fetch && this.props.fetch.success === false) {
      this.rejectPromise(this.props.fetch.formError);
    }
    if (this.props.fetch && this.props.fetch.success) {
      this.resolvePromise();
      this.isPending = false;
    }
  }

  componentWillUnmount() {
    this.unlisten();
  }

  getCollectionForm(source) {
    return source.collectionForms.find(item => item.id === decodeURIComponent(source.match.params.id));
  }

  parseFormData(form) {
    const fieldsArr = [];
    form.fields.forEach(field => {
      fieldsArr.push({
        baseType: "field",
        parent: null,
        ...field
      });
    });
    form.headings.forEach(item => {
      fieldsArr.push({
        baseType: "heading",
        name: item.name,
        description: item.description
      });
      item.fields.forEach(field => {
        fieldsArr.push({
          baseType: "field",
          parent: item.name,
          ...field
        });
      });
    });
    return fieldsArr;
  }

  formatFormData(data) {
    const { form, items } = JSON.parse(JSON.stringify(data));
    form.fields = items
      .filter(item => item.baseType !== "heading" && !item.parent)
      .map(item => {
        delete item.baseType;
        delete item.parent;
        return item;
      });
    form.headings = items.filter(item => item.baseType === "heading");
    form.headings.forEach(heading => {
      delete heading.baseType;
      heading.fields = items
        .filter(item => item.parent === heading.name)
        .map(item => {
          delete item.baseType;
          delete item.parent;
          return item;
        });
    });

    return form;
  }

  onHistoryChange = location => {
    const locationParts = location.pathname.split("/");
    if (locationParts[2] === "collectionForms" && locationParts[3] === "new") {
      const state = {
        form: {
          id: null,
          type: locationParts[4]
        },
        items: []
      };
      this.props.dispatch(initialize("DataCollectionForm", state));
    }
  };

  addField = type => {
    const { items } = this.props.values;
    const field = {
      type,
      baseType: "field",
      parent: null,
      label: type.label,
      helpText: "",
      mandatory: false
    };

    delete type.formattedLabel;

    this.props.dispatch(change("DataCollectionForm", "items", [field, ...items]));

    this.formRef.scrollTo({
      top: 0,
      left: 0,
      behavior: "smooth"
    });
  };

  addHeading = () => {
    const { items } = this.props.values;
    const heading = {
      baseType: "heading",
      name: "",
      description: ""
    };

    this.props.dispatch(change("DataCollectionForm", "items", [heading, ...items]));

    this.formRef.scrollTo({
      top: 0,
      left: 0,
      behavior: "smooth"
    });
  };

  deleteField = index => {
    const { items } = this.props.values;
    const updated = [...items];
    updated.splice(index, 1);

    this.props.dispatch(change("DataCollectionForm", "items", updated));
  };

  onSave = value => {
    this.isPending = true;

    setParents(value.items);
    const { onCreate, onUpdate, history } = this.props;

    const formatted = this.formatFormData(value);

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      if (this.props.match.params.action === "edit") {
        onUpdate(formatted.id, formatted);
      }

      if (this.props.match.params.action === "new") {
        onCreate(formatted);
      }
    })
      .then(() => {
        const updated = this.props.collectionForms.find(item => item.name === value.form.name);
        const items = this.parseFormData(updated);
        const updatedData = {
          items,
          form: updated
        };
        this.skipValidation = true;
        this.props.dispatch(initialize("DataCollectionForm", updatedData));
        history.push(`/preferences/collectionForms/edit/${formatted.type}/${encodeURIComponent(updated.id)}`);
        this.skipValidation = false;
      })
      .catch(error => {
        this.isPending = false;

        const errors: any = {
          form: {}
        };
        if (error) {
          errors.form[error.propertyName] = error.errorMessage;
        }
        throw new SubmissionError(errors);
      });
  };

  onDelete = id => {
    this.isPending = true;

    this.setState({
      disableConfirm: true
    });

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      this.props.onDelete(id);
    })
      .then(() => {
        this.redirectOnDelete(id);
        this.setState({
          disableConfirm: false
        });
      })
      .catch(() => {
        this.isPending = false;
        this.setState({
          disableConfirm: false
        });
      });
  };

  redirectOnDelete = id => {
    const { history, collectionForms } = this.props;

    if (collectionForms[0].id === id) {
      collectionForms.length > 1
        ? history.push(
            `/preferences/collectionForms/edit/${collectionForms[1].type}/${encodeURIComponent(collectionForms[1].id)}`
          )
        : history.push("/preferences");
      return;
    }

    history.push(
      `/preferences/collectionForms/edit/${collectionForms[0].type}/${encodeURIComponent(collectionForms[0].id)}`
    );
  };

  validateUniqueNames = (value, values) => {
    const { collectionForms } = this.props;

    if (!collectionForms || this.skipValidation) {
      return undefined;
    }

    if (value.includes("%")) {
      return "Special symbols not allowed";
    }

    const match = collectionForms.filter(item => item.name === value.trim());

    if (this.props.match.params.action === "edit") {
      const filteredMatch = match.filter(item => item.id !== values.form.id);
      return filteredMatch.length > 0 ? "Form name must be unique" : undefined;
    }

    return match.length > 0 ? "Form name must be unique" : undefined;
  };

  duplicateForm = (history, form, items) => {
    const clone = JSON.parse(JSON.stringify(form));
    clone.name = "";
    clone.id = "";

    this.props.dispatch(
      initialize("DataCollectionForm", {
        items,
        form: clone
      })
    );

    setTimeout(() => {
      this.unlisten();
      history.push(`/preferences/collectionForms/new/${form.type}/`);
      this.unlisten = this.props.history.listen(location => {
        this.onHistoryChange(location);
      });
    }, 100);
  };

  render() {
    const {
      classes, dispatch, values, handleSubmit, match, dirty, history, valid, form
    } = this.props;

    const { disableConfirm } = this.state;
    const isNew = match.params.action === "new";

    const type = this.props.match.params.type;
    const id = !isNew && values && values.form.id;
    const created = values && values.form.created;
    const modified = values && values.form.modified;

    return (
      <div className={clsx(classes.mainContainer, "overflow-hidden")}>
        <div className="h-100 overflow-y-auto" ref={this.getFormRef}>
          <form className="container p-3" onSubmit={handleSubmit(this.onSave)}>
            {!disableConfirm && dirty && <RouteChangeConfirm form={form} when={dirty} />}
            <CustomAppBar>
              <Grid
                container
                className="ml-1"
                classes={{
                  container: classes.fitSmallWidth
                }}
              >
                <Grid item xs={12} className="centeredFlex">
                  {values && (
                    <CollectionFormFieldTypesMenu
                      items={values.items}
                      formType={type}
                      addField={this.addField}
                      addHeading={this.addHeading}
                      className="ml-0"
                    />
                  )}

                  <FormField
                    type="headerText"
                    name="form.name"
                    placeholder="Name"
                    margin="none"
                    className={classes.HeaderTextField}
                    listSpacing={false}
                    validate={[validateSingleMandatoryField, this.validateUniqueNames]}
                  />

                  <div className="flex-fill" />

                  {values && !isNew && (
                    <AppBarActions
                      actions={[
                        {
                          action: () => {
                            this.onDelete(id);
                          },
                          icon: <DeleteForever />,

                          confirmText: "Form will be deleted permanently",
                          tooltip: "Delete form",
                          confirmButtonText: "DELETE"
                        },
                        {
                          action: () => {
                            this.duplicateForm(history, values.form, values.items);
                          },
                          icon: <FileCopy />,
                          confirm: false,
                          tooltip: "Copy form"
                        }
                      ]}
                    />
                  )}

                  {!isNew && values && (
                    <AppBarHelpMenu
                      created={created ? new Date(created) : null}
                      modified={modified ? new Date(modified) : null}
                      auditsUrl={`audit?search=~"${type}FieldConfiguration" and entityId == ${values.form.id}`}
                      manualUrl={manualLink}
                    />
                  )}

                  <FormSubmitButton
                    disabled={!dirty}
                    invalid={!valid}
                  />
                </Grid>
              </Grid>
            </CustomAppBar>

            <Grid container>
              <Grid item sm={12} lg={10} xl={6}>
                <Grid container>
                  <Grid item xs={12} className={clsx("centeredFlex", classes.headerControlsContainer)}>
                    <div className="pt-2 pb-2">
                      <Typography variant="caption">Type</Typography>

                      <Typography variant="subtitle1">{type === "Survey" ? "Student Feedback" : type}</Typography>
                    </div>

                    <div className="flex-fill" />

                    {values && type === "Survey" && (
                      <FormField
                        type="select"
                        name="form.deliverySchedule"
                        label="Delivery Schedule"
                        autoWidth
                        items={deliveryScheduleTypes}
                        className={clsx("pt-2", classes.selectField)}
                        required
                      />
                    )}
                  </Grid>

                  <Grid item xs={12} className="mb-1">
                    <Divider />
                  </Grid>

                  <Grid item xs={12} className="mb-3">
                    {values && values.items && (
                      <FieldArray
                        name="items"
                        component={renderCollectionFormFields}
                        deleteField={this.deleteField}
                        dispatch={dispatch}
                        classes={classes}
                      />
                    )}
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </form>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  collectionForms: state.preferences.dataCollectionForms,
  values: getFormValues("DataCollectionForm")(state),
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onUpdate: (id, form) => dispatch(updateDataCollectionForm(id, form)),
  onCreate: form => dispatch(createDataCollectionForm(form)),
  onDelete: id => dispatch(deleteDataCollectionForm(id))
});

const DataCollectionForm = reduxForm({
  onSubmitFail: (errors, dispatch, submitError, props) =>
    onSubmitFail(errors, dispatch, submitError, props, { behavior: "smooth", block: "end" }),
  form: "DataCollectionForm"
})(
  connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(withRouter(CollectionFormContainer)))
);

export default DataCollectionForm;
