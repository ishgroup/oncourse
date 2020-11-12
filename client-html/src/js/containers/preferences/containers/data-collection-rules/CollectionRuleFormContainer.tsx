import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { RouteComponentProps, withRouter } from "react-router-dom";
import { DataCollectionForm, DataCollectionRule } from "@api/model";
import { State } from "../../../../reducers/state";
import CollectionRulesForm from "./components/CollectionRulesForm";
import { updateDataCollectionRule, removeDataCollectionRule, createDataCollectionRule } from "../../actions";
import { getFormValues } from "redux-form";
import { Fetch } from "../../../../model/common/Fetch";

interface Props extends RouteComponentProps {
  onUpdate: (id: string, rule: DataCollectionRule) => void;
  onDelete: (id: string) => void;
  onAddNew: (rule: DataCollectionRule) => void;
  collectionRules: DataCollectionRule[];
  collectionForms: DataCollectionForm[];
  value: DataCollectionRule;
  fetch: Fetch;
}

class CollectionRuleFormContainer extends React.Component<Props, any> {
  getForm = (rules: DataCollectionRule[], match) => {
    return rules.find(rule => rule.id === decodeURIComponent(match.params.id));
  };

  render() {
    const { onUpdate, onDelete, onAddNew, collectionRules, collectionForms, match, value, fetch, history } = this.props;

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
      item
    });

    return <div>{collectionRules && componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  value: getFormValues("CollectionRulesForm")(state),
  collectionForms: state.preferences.dataCollectionForms,
  collectionRules: state.preferences.dataCollectionRules,
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onUpdate: (id: string, rule: DataCollectionRule) => dispatch(updateDataCollectionRule(id, rule)),
    onDelete: (id: string) => dispatch(removeDataCollectionRule(id)),
    onAddNew: (rule: DataCollectionRule) => dispatch(createDataCollectionRule(rule))
  };
};

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withRouter(CollectionRuleFormContainer));
