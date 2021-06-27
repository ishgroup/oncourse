import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { DataCollectionForm, DataCollectionRule } from "@api/model";
import { getFormValues, initialize, SubmissionError } from "redux-form";
import { State } from "../../../../reducers/state";
import CollectionRulesForm from "./components/CollectionRulesForm";
import { updateDataCollectionRule, removeDataCollectionRule, createDataCollectionRule } from "../../actions";
import { setNextLocation } from "../../../../common/actions";
import { Fetch } from "../../../../model/common/Fetch";

interface Params {
  action: string;
  id: string;
}

interface Props extends RouteComponentProps <Params> {
  onUpdate: (id: string, rule: DataCollectionRule) => void;
  onDelete: (id: string) => void;
  onAddNew: (rule: DataCollectionRule) => void;
  collectionRules: DataCollectionRule[];
  collectionForms: DataCollectionForm[];
  value: DataCollectionRule;
  fetch: Fetch;
  initialize: (data) => void;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void
}

class CollectionRuleFormContainer extends React.Component<Props, any> {
  private skipValidation: boolean;

  private promisePending: boolean = false;

  private resolvePromise;

  private rejectPromise;

  UNSAFE_componentWillReceiveProps(nextProps: any) {
    if (this.rejectPromise && nextProps.fetch && nextProps.fetch.success === false) {
      this.rejectPromise(nextProps.fetch.formError);
    }
    if (this.resolvePromise && nextProps.fetch && nextProps.fetch.success) {
      this.resolvePromise();
      this.promisePending = false;
    }
  }

  getForm = (rules: DataCollectionRule[], match) => rules.find(rule => rule.id === decodeURIComponent(match.params.id));

  onSave = (value: DataCollectionRule) => {
    const {
      onUpdate, onAddNew, match, history, initialize, nextLocation, setNextLocation
    } = this.props;

    const isNew = match.params.action === "new";

    this.promisePending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      if (isNew) {
        onAddNew(value);
      } else {
        onUpdate(value.id, value);
      }
    })
      .then(() => {
        this.skipValidation = true;
        initialize(value);

        nextLocation && history.push(nextLocation);
        setNextLocation('');

        this.skipValidation = false;
      })
      .catch(error => {
        this.promisePending = false;

        const errorObj: any = {};

        if (error) {
          errorObj[error.propertyName] = error.errorMessage;
        }
        throw new SubmissionError(errorObj);
      });
  };

  render() {
    const {
 onUpdate, onDelete, onAddNew, collectionRules, collectionForms, match, value, fetch, history
} = this.props;

    const item = collectionRules && this.getForm(collectionRules, match);

    const componentForm = React.cloneElement(<CollectionRulesForm />, {
      onUpdate,
      onDelete,
      onAddNew,
      match,
      history,
      fetch,
      collectionForms,
      collectionRules,
      value,
      item,
      onSubmit: this.onSave,
    });

    return <div>{collectionRules && componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  value: getFormValues("CollectionRulesForm")(state),
  collectionForms: state.preferences.dataCollectionForms,
  collectionRules: state.preferences.dataCollectionRules,
  fetch: state.fetch,
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    onUpdate: (id: string, rule: DataCollectionRule) => dispatch(updateDataCollectionRule(id, rule)),
    onDelete: (id: string) => dispatch(removeDataCollectionRule(id)),
    onAddNew: (rule: DataCollectionRule) => dispatch(createDataCollectionRule(rule)),
    initialize: initData => dispatch(initialize("CollectionRulesForm", initData)),
    setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
  });

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withRouter(CollectionRuleFormContainer));
