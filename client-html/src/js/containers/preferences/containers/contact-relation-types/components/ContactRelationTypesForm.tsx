import { ContactRelationType } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import { idsToString, ShowConfirmCaller } from 'ish-ui';
import { isEqual } from 'es-toolkit/compat';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { arrayInsert, arrayRemove, FieldArray, Form, initialize, reduxForm, SubmissionError } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../reducers/state';
import { cardsFormStyles } from '../../../styles/formCommonStyles';
import ContactRelationTypesRenderer from './ContactRelationTypesRenderer';

const manualUrl = getManualLink("setting-your-general-preferences#contact-relation-types");

export const CONTACT_RELATION_TYPES_FORM: string = "ContactRelationTypesForm";

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
  nextLocation?: string
}

class ContactRelationTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize(CONTACT_RELATION_TYPES_FORM, { types: props.contactRelationTypes }));
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
        const { nextLocation, history } = this.props;

        this.props.dispatch(initialize(CONTACT_RELATION_TYPES_FORM, { types: this.props.contactRelationTypes }));

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
    const item = {} as ContactRelationType;
    item.id = null;
    item.relationName = "";
    item.reverseRelationName = "";
    item.portalAccess = false;
    item.systemType = false;

    this.props.dispatch(arrayInsert(CONTACT_RELATION_TYPES_FORM, "types", 0, item));
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
          this.props.dispatch(arrayRemove(CONTACT_RELATION_TYPES_FORM, "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize(CONTACT_RELATION_TYPES_FORM, { types: this.props.contactRelationTypes }));
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
      <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)} role={CONTACT_RELATION_TYPES_FORM}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={() => `audit?search=~"ContactRelationType" and entityId in (${idsToString(data.types)})`}
          disabled={!dirty}
          invalid={invalid}
          title={$t('contact_relation_types')}
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
                    component={ContactRelationTypesRenderer}
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
  nextLocation: state.nextLocation
});

const ContactRelationTypesForm = reduxForm({
  onSubmitFail,
  form: CONTACT_RELATION_TYPES_FORM
})(connect<any, any, any>(mapStateToProps)(
  withStyles(withRouter(ContactRelationTypesBaseForm), cardsFormStyles)
));

export default ContactRelationTypesForm;
