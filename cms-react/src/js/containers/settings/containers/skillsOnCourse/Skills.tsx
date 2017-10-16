import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {getSkillsOnCourseSettings, setSkillsOnCourseSettings} from "./actions";
import {Checkbox} from "../../../../common/components/Checkbox";
import {SkillsOnCourseSettings} from "../../../../model";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  skillsOnCourse: SkillsOnCourseSettings;
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

    return (
      <div>

        <Form>
          <FormGroup>
            <Checkbox
              label="Hide student contact details from tutor."
              name="hideStudentDetails"
              checked={hideStudentDetails}
              onChange={e => {this.onChange(e, 'hideStudentDetails');}}
            />
          </FormGroup>

          <FormGroup>
            <Checkbox
              label="Enable outcome marking in tutor portal."
              name="enableOutcomeMarking"
              checked={enableOutcomeMarking}
              onChange={e => {this.onChange(e, 'enableOutcomeMarking');}}
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="tutorFeedbackEmail">Email tutor feedback to</Label>
            <Row>
              <Col sm={3}>
                <Input
                  type="text"
                  name="tutorFeedbackEmail"
                  id="tutorFeedbackEmail"
                  value={tutorFeedbackEmail}
                  onChange={e => this.onChange(e, 'tutorFeedbackEmail')}
                />
              </Col>
            </Row>
          </FormGroup>

          <Button color="primary" onClick={() => this.onSave()}>Save</Button>
        </Form>

      </div>
    );
  }
}

const mapStateToProps = state => ({
  skillsOnCourse: state.settings.skillsOnCourseSettings,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getSkillsOnCourseSettings()),
    onSave: settings => dispatch(setSkillsOnCourseSettings(settings)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Skills);
