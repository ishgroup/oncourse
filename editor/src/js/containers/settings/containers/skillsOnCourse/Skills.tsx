import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import classnames from "classnames";
import {Checkbox, FormControlLabel, Grid, Paper, TextField} from "@material-ui/core";
import {getSkillsOnCourseSettings, setSkillsOnCourseSettings} from "./actions";
import {State} from "../../../../reducers/state";
import {SkillsOnCourseState} from "./reducers/State";
import CustomButton from "../../../../common/components/CustomButton";

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
      hideStudentDetails: props.skillsOnCourse.hideStudentDetails,
      enableOutcomeMarking: props.skillsOnCourse.enableOutcomeMarking,
      tutorFeedbackEmail: props.skillsOnCourse.tutorFeedbackEmail,
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
      <Paper className={classnames({fetching})}>
        <form>
          <Grid className="flex-column">
            <FormControlLabel
              control={
                <Checkbox
                  checked={hideStudentDetails}
                  onChange={e => {this.onChange(e, 'hideStudentDetails')}}
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
                  onChange={e => {this.onChange(e, 'enableOutcomeMarking')}}
                  name="enableOutcomeMarking"
                  color="primary"
                />
              }
              label="Enable outcome marking in tutor portal."
            />
          </Grid>

          <Grid className="mt-1 mb-1">
            <label htmlFor="tutorFeedbackEmail">Email tutor feedback to</label>
            <Grid container>
              <Grid item xs={3}>
                <TextField
                  type="text"
                  name="tutorFeedbackEmail"
                  id="tutorFeedbackEmail"
                  value={tutorFeedbackEmail}
                  onChange={e => this.onChange(e, 'tutorFeedbackEmail')}
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
