import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {Checkbox, FormControlLabel, Grid, Paper} from "@material-ui/core";
import {getSkillsOnCourseSettings, setSkillsOnCourseSettings} from "./actions";
import {State} from "../../../../reducers/state";
import {SkillsOnCourseState} from "./reducers/State";
import CustomButton from "../../../../common/components/CustomButton";
import clsx from "clsx";
import {stubFunction} from "../../../../common/utils/Components";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  skillsOnCourse: SkillsOnCourseState;
  fetching: boolean;
}

export class Skills extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      hideStudentDetails: props.skillsOnCourse.hideStudentDetails || false,
      enableOutcomeMarking: props.skillsOnCourse.enableOutcomeMarking || false,
      tutorFeedbackEmail: props.skillsOnCourse.tutorFeedbackEmail || "",
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentWillReceiveProps(props: Props) {
    if (props.skillsOnCourse.refreshSettings) {
      this.setState({
        hideStudentDetails: props.skillsOnCourse.hideStudentDetails,
        enableOutcomeMarking: props.skillsOnCourse.enableOutcomeMarking,
        tutorFeedbackEmail: props.skillsOnCourse.tutorFeedbackEmail,
      });
    }
  }

  onChange(event, key) {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    this.setState({
      [key]: value,
    });
  }

  onSave() {
    const {onSave} = this.props;
    onSave(this.state);
  }

  render() {
    const {hideStudentDetails, enableOutcomeMarking, tutorFeedbackEmail} = this.state;
    const {fetching} = this.props;

    return (
      <Paper className={clsx((fetching && "fetching"), "p-3")}>
        <form>
          <Grid className="flex-column">
            <FormControlLabel
              control={
                <Checkbox
                  checked={hideStudentDetails}
                  onChange={e => {this.onChange(e, 'hideStudentDetails');}}
                  name="hideStudentDetails"
                  color="primary"
                />
              }
              label="Hide student contact details from tutor."
            />

            <FormControlLabel
              control={
                <Checkbox
                  checked={enableOutcomeMarking}
                  onChange={e => {this.onChange(e, 'enableOutcomeMarking');}}
                  name="enableOutcomeMarking"
                  color="primary"
                />
              }
              label="Enable outcome marking in tutor portal."
            />
          </Grid>

          <Grid className="mt-1 mb-1">
            <Grid container>
              <Grid item xs={3}>
                <EditInPlaceField
                  label="Email tutor feedback to"
                  name="tutorFeedbackEmail"
                  id="tutorFeedbackEmail"
                  meta={{}}
                  input={{
                    onChange: e => this.onChange(e, 'tutorFeedbackEmail'),
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: tutorFeedbackEmail,
                  }}
                />
              </Grid>
            </Grid>
          </Grid>

          <CustomButton
            styleType="submit"
            onClick={() => this.onSave()}
            styles="mt-2"
          >
            Save
          </CustomButton>
        </form>
      </Paper>
    );
  }
}

const mapStateToProps = (state: State) => ({
  skillsOnCourse: state.settings.skillsOnCourseSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getSkillsOnCourseSettings()),
    onSave: settings => dispatch(setSkillsOnCourseSettings(settings)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Skills as any);
