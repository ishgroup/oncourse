import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import ClassDefaultsForm from "./components/ClassDefaultsForm";
import { State } from "../../../../reducers/state";
import { Categories } from "../../../../model/preferences";
import FormContainer from "../FormContainer";
import { getEnum } from "../../actions";
import { EnumItem, EnumName } from "@api/model";

interface Props {
  classDefaults: any;
  DeliveryMode: EnumItem[];
  ClassFundingSource: EnumItem[];
  getEnum: (name: EnumName) => void;
}

class ClassDefaults extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getEnum("DeliveryMode");
    this.props.getEnum("ClassFundingSource");
  }

  render() {
    const { classDefaults, DeliveryMode = [], ClassFundingSource = [] } = this.props;

    return (
      <div>
        <FormContainer
          data={classDefaults}
          enums={{ DeliveryMode, ClassFundingSource }}
          category={Categories.classDefaults}
          form={<ClassDefaultsForm />}
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  classDefaults: state.preferences.classDefaults,
  DeliveryMode: state.enums.DeliveryMode,
  ClassFundingSource: state.enums.ClassFundingSource
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getEnum: (name: EnumName) => dispatch(getEnum(name))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ClassDefaults);
