import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import CollegeForm from "./components/CollegeForm";
import { State } from "../../../../reducers/state";
import { Categories } from "../../../../model/preferences";
import FormContainer from "../FormContainer";
import { getTimezones } from "../../actions/index";
import { CollegeSucureKey } from "../../../../model/preferences/College";

interface Props {
  college: any;
  timezones: string[];
  onInit: () => void;
}

export class CollegeBase extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }
  render() {
    const { college, timezones } = this.props;
    const sekKey = college && college[CollegeSucureKey.uniqueKey];

    const data = { ...college };

    delete data[CollegeSucureKey.uniqueKey];
    return (
      <div>
        <FormContainer
          data={data}
          secKey={sekKey}
          timezones={timezones}
          category={Categories.college}
          form={<CollegeForm secKey={sekKey} />}
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  college: state.preferences.college,
  timezones: state.timezones
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getTimezones())
  };
};

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(CollegeBase);
