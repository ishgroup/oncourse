import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import SettingsForm from "./SettingsForm";
import { State } from "../../../../reducers/state";
import { Categories } from "../../../../model/preferences";
import FormContainer from "../../../preferences/containers/FormContainer";
import { EnumItem, EnumName } from "@api/model";
import { getEnum } from "../../../preferences/actions";

interface Props {
  security: any;
  TwoFactorAuthStatus: EnumItem[];
  getEnum: (name: EnumName) => void;
}

class Settings extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("TwoFactorAuthStatus");
  }

  render() {
    const { security, TwoFactorAuthStatus = [] } = this.props;

    return (
      <div>
        <FormContainer
          data={security}
          category={Categories.security}
          enums={{ TwoFactorAuthStatus }}
          form={<SettingsForm />}
          skipOnInit
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  security: state.preferences.security,
  TwoFactorAuthStatus: state.enums.TwoFactorAuthStatus
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getEnum: (name: EnumName) => dispatch(getEnum(name))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Settings);
