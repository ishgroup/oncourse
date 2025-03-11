/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataCollectionForm, DataCollectionRule, DataCollectionType } from '@api/model';
import DeleteForever from '@mui/icons-material/DeleteForever';
import Grid from '@mui/material/Grid';
import $t from '@t';
import { sortDefaultSelectItems } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { getFormSyncErrors, initialize, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import AppBarActions from '../../../../../common/components/appBar/AppBarActions';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../reducers/state';

const manualUrl = getManualLink("data-collection-forms-and-rules");

export const DATA_COLLECTION_RULES_FORM: string = "CollectionRulesForm";

const styles = () =>
  ({
    selectField: {
      paddingRight: "60px"
    }
  });

interface Props {
  item: DataCollectionRule;
  value: DataCollectionRule;
  onUpdate: (id: string, rule: DataCollectionRule) => void;
  onDelete: (id: string) => void;
  onAddNew: (rule: DataCollectionRule) => void;
  classes: any;
  dispatch: any;
  handleSubmit: any;
  match: any;
  history: any;
  syncErrors: any;
  dirty: boolean;
  invalid: boolean;
  form: string;
  collectionForms: DataCollectionForm[];
  collectionRules: DataCollectionRule[];
  onSubmit: (value) => void;
}

class CollectionRulesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private unlisten;

  private promisePending: boolean = false;

  private skipValidation: boolean;

  constructor(props) {
    super(props);

    if (props.item) {
      props.dispatch(initialize(DATA_COLLECTION_RULES_FORM, props.item));
    } else {
      props.dispatch(initialize(DATA_COLLECTION_RULES_FORM, { id: null }));
    }
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (!this.promisePending && nextProps.item && (!this.props.item || this.props.item.id !== nextProps.item.id)) {
      this.props.dispatch(initialize(DATA_COLLECTION_RULES_FORM, nextProps.item));
      return;
    }
    if (this.rejectPromise && nextProps.fetch && nextProps.fetch.success === false) {
      this.rejectPromise(nextProps.fetch.formError);
    }
    if (this.resolvePromise && nextProps.fetch && nextProps.fetch.success) {
      this.resolvePromise();
      this.promisePending = false;
    }
  }

  componentDidMount() {
    this.unlisten = this.props.history.listen(location => {
      this.onHistoryChange(location);
    });
  }

  componentWillUnmount() {
    this.unlisten();
  }

  onHistoryChange = location => {
    const locationParts = location.pathname.split("/");
    if (locationParts[2] === "collectionRules" && locationParts[3] === "new") {
      this.props.dispatch(initialize(DATA_COLLECTION_RULES_FORM, { id: null }));
    }
  };

  getItems = (type: DataCollectionType) => {
    const { collectionForms } = this.props;

    const items = collectionForms
      && collectionForms
        .filter(filtered => filtered.type === type)
        .map(form => ({
          value: form.name,
          label: form.name
        }));

    if (items) {
      items.sort(sortDefaultSelectItems);
    }

    return items;
  };

  validateUniqueNames = (value, values) => {
    const { collectionRules, match } = this.props;

    if (!collectionRules || this.skipValidation) {
      return undefined;
    }

    if (value.includes("%")) {
      return "Special symbols not allowed";
    }

    const matching = collectionRules.filter(item => item.name.trim() === value.trim());

    if (match.params.action === "edit") {
      const filteredMatch = matching.filter(item => item.id !== values.id);
      return filteredMatch.length > 0 ? "Form name must be unique" : undefined;
    }

    return matching.length > 0 ? "Form name must be unique" : undefined;
  };

  redirectOnDelete = id => {
    const { history, collectionRules, dispatch } = this.props;

    if (collectionRules[0].id === id) {
      if (collectionRules.length > 1) {
        history.push(`/preferences/collectionRules/edit/${collectionRules[1].id}`);
        setTimeout(() => dispatch(initialize(DATA_COLLECTION_RULES_FORM, collectionRules[1])), 100);

        return;
      }
      history.push("/preferences");
      return;
    }
    history.push(`/preferences/collectionRules/edit/${collectionRules[0].id}`);
    setTimeout(() => dispatch(initialize(DATA_COLLECTION_RULES_FORM, collectionRules[0])), 100);
  };

  onRuleDelete = id => {
    const { onDelete } = this.props;

    this.promisePending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      onDelete(id);
    })
      .then(() => {
        this.redirectOnDelete(id);
      })
      .catch(() => {
        this.promisePending = false;
      });
  };

  render() {
    const {
      classes, handleSubmit, match, value, dirty, form, onSubmit, invalid, syncErrors
    } = this.props;
    const isNew = match.params.action === "new";

    return (
      <form className="container" autoComplete="off" onSubmit={handleSubmit(onSubmit)} role={DATA_COLLECTION_RULES_FORM}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={value}
          manualUrl={manualUrl}
          getAuditsUrl={id => `audit?search=~"FieldConfigurationScheme" and entityId == ${id}`}
          disabled={!dirty}
          invalid={invalid}
          title={(isNew && (!value || !value.name || value.name.trim().length === 0))
            ? "New"
            : value?.name?.trim()}
          hideHelpMenu={isNew}
          opened={isNew || Object.keys(syncErrors).includes("name")}
          createdOn={v => new Date(v.created)}
          modifiedOn={v => new Date(v.modified)}
          fields={(
            <Grid item xs={12}>
              <FormField
                type="text"
                name="name"
                label={$t('name')}
                validate={this.validateUniqueNames}
                required
              />
            </Grid>
          )}
          actions={!isNew && (
            <AppBarActions
              actions={[
                {
                  action: () => {
                    this.onRuleDelete(value.id);
                  },
                  icon: <DeleteForever />,
                  tooltip: "Delete form",
                  confirmText: "Rule will be deleted permanently",
                  confirmButtonText: "DELETE"
                }
              ]}
            />
          )}
        >
          <Grid container>
            <Grid item xs={12} md={10}>
              <Grid container columnSpacing={3} rowSpacing={2}>
                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="enrolmentFormName"
                    label={$t('enrolment')}
                    items={this.getItems("Enrolment") || []}
                    className={classes.selectField}
                    required
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="surveyForms"
                    label={$t('student_feedback')}
                    items={this.getItems("Survey") || []}
                    className={classes.selectField}
                    allowEmpty
                    multiple
                    />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="applicationFormName"
                    label={$t('application')}
                    items={this.getItems("Application") || []}
                    className={classes.selectField}
                    required
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="payerFormName"
                    label={$t('payer')}
                    allowEmpty
                    items={this.getItems("Payer") || []}
                    className={classes.selectField}
                    />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="waitingListFormName"
                    label={$t('waiting_list')}
                    items={this.getItems("WaitingList") || []}
                    className={classes.selectField}
                    required
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="parentFormName"
                    label={$t('parent')}
                    allowEmpty
                    items={this.getItems("Parent") || []}
                    className={classes.selectField}
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="productFormName"
                    label={$t('product')}
                    allowEmpty
                    items={this.getItems("Product") || []}
                    className={classes.selectField}
                    required
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="voucherFormName"
                    label={$t('voucher')}
                    allowEmpty
                    items={this.getItems("Voucher") || []}
                    className={classes.selectField}
                    required
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormField
                    type="select"
                    name="membershipFormName"
                    label={$t('membership')}
                    allowEmpty
                    items={this.getItems("Membership") || []}
                    className={classes.selectField}
                    required
                  />
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </AppBarContainer>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  syncErrors: getFormSyncErrors(DATA_COLLECTION_RULES_FORM)(state)
});

const CollectionRulesForm = reduxForm({
  form: DATA_COLLECTION_RULES_FORM,
  onSubmitFail
})(withStyles(
  withRouter(connect<any, any, any>(mapStateToProps)(CollectionRulesBaseForm)),
  styles
) as any);

export default CollectionRulesForm;
