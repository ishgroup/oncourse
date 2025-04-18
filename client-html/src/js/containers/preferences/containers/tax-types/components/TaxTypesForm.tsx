import { Tax } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import { idsToString, ShowConfirmCaller } from 'ish-ui';
import isEqual from 'lodash.isequal';
import * as React from 'react';
import { withRouter } from 'react-router';
import { arrayInsert, arrayRemove, FieldArray, Form, initialize, reduxForm, SubmissionError } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { formCommonStyles } from '../../../styles/formCommonStyles';
import TaxTypesRenderer from './TaxTypesRenderer';

const manualUrl = getManualLink("setting-your-general-preferences#tax-types");

interface Props {
  data: any;
  classes: any;
  created: Date;
  modified: Date;
  taxTypes: Tax[];
  dispatch: any;
  handleSubmit: any;
  onDelete: (id: string) => void;
  onUpdate: (taxTypes: Tax[]) => void;
  assetAccounts: any;
  liabilityAccounts: any;
  dirty: boolean;
  form: string;
  invalid: boolean;
  openConfirm: ShowConfirmCaller;
  history: any;
  nextLocation: string;
}

class TaxTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize("TaxTypesForm", { types: props.taxTypes }));
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

  componentDidUpdate() {
    const {
      dirty, nextLocation, history
    } = this.props;

    if (nextLocation && !dirty) {
      history.push(nextLocation);
    }
  }

  findIndex = id => this.props.data.types.findIndex(item => item.id === id);

  getTouchedAndNew = items => {
    const initialLength = this.props.taxTypes.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.taxTypes[index]));

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
        this.props.dispatch(initialize("TaxTypesForm", { types: this.props.taxTypes }));
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
    const item = {} as Tax;
    item.id = null;
    item.code = "";
    item.editable = true;
    item.systemType = false;
    item.gst = false;
    item.rate = "" as any;
    item.payableAccountId = null;
    item.receivableAccountId = null;
    item.description = "";

    this.props.dispatch(arrayInsert("TaxTypesForm", "types", 0, item));
    const domNode = document.getElementById("types[0].code");
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
          this.props.dispatch(arrayRemove("TaxTypesForm", "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize("TaxTypesForm", { types: this.props.taxTypes }));
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
      classes, handleSubmit, data, assetAccounts, liabilityAccounts, dirty, created, modified, invalid, form
    } = this.props;

    return (
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={() => `audit?search=~"Tax" and entityId in (${idsToString(data.types)})`}
          disabled={!dirty}
          invalid={invalid}
          title={$t('tax_types')}
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
                    assetAccounts={assetAccounts}
                    liabilityAccounts={liabilityAccounts}
                    component={TaxTypesRenderer}
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

const TaxTypesForm = reduxForm({
  onSubmitFail,
  form: "TaxTypesForm"
})(withStyles(withRouter(TaxTypesBaseForm), formCommonStyles));

export default TaxTypesForm;
