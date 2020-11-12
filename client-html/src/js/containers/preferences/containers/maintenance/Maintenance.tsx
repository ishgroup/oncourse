import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../../reducers/state";
import { Categories } from "../../../../model/preferences";
import FormContainer from "../FormContainer";
import MaintenanceForm from "./components/MaintenanceForm";
import { EnumItem, EnumName } from "@api/model";
import { getEnum } from "../../actions/index";

interface Props {
  MaintenanceTimes: EnumItem[];
  maintenance: any;
  getEnum: (name: EnumName) => void;
}

class Maintenance extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("MaintenanceTimes");
  }

  render() {
    const { maintenance, MaintenanceTimes = [] } = this.props;

    return (
      <div>
        <FormContainer
          enums={{ MaintenanceTimes }}
          data={maintenance}
          category={Categories.maintenance}
          form={<MaintenanceForm />}
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  MaintenanceTimes: state.enums.MaintenanceTimes,
  maintenance: state.preferences.maintenance
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getEnum: (name: EnumName) => dispatch(getEnum(name))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Maintenance);
