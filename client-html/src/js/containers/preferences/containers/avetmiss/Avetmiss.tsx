import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../../reducers/state";
import { Categories } from "../../../../model/preferences";
import FormContainer from "../FormContainer";
import AvetmissForm from "./components/AvetmissForm";
import { EnumItem, EnumName } from "@api/model";
import { getEnum } from "../../actions";

interface Props {
  avetmiss: any;
  ExportJurisdiction: EnumItem[];
  TrainingOrg_Types: EnumItem[];
  AddressStates: EnumItem[];
  getEnum: (name: EnumName) => void;
}

class Avetmiss extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("ExportJurisdiction");
    this.props.getEnum("TrainingOrg_Types");
    this.props.getEnum("AddressStates");
  }

  render() {
    const { avetmiss, ExportJurisdiction = [], TrainingOrg_Types = [], AddressStates = [] } = this.props;

    return (
      <div>
        <FormContainer
          data={avetmiss}
          enums={{
            ExportJurisdiction,
            TrainingOrg_Types,
            AddressStates
          }}
          category={Categories.avetmiss}
          form={<AvetmissForm />}
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  avetmiss: state.preferences.avetmiss,
  ExportJurisdiction: state.enums.ExportJurisdiction,
  TrainingOrg_Types: state.enums.TrainingOrg_Types,
  AddressStates: state.enums.AddressStates
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getEnum: (name: EnumName) => dispatch(getEnum(name))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Avetmiss);
