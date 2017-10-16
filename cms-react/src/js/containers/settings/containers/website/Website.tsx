import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {getWebsiteSettings, setWebsiteSettings} from "./actions";
import {WebsiteSettings} from "../../../../model";
import {Checkbox} from "../../../../common/components/Checkbox";
import {ClassCondition, ClassEnrolmentCondition} from "../../../../model/ClassAge";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  website: WebsiteSettings;
}

export class Website extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      enableSocialMedia: props.enableSocialMedia,
      addThisId: props.addThisId,
      enableForCourse: props.enableForCourse,
      enableForWebpage: props.enableForWebpage,
      classAge: {
        hideClassDays: props.classAge && props.classAge.hideClassDays,
        hideClassCondition: props.classAge && props.classAge.hideClassCondition,
        stopWebEnrolmentDays: props.classAge && props.classAge.stopWebEnrolmentDays,
        stopWebEnrolmentCondition: props.classAge && props.classAge.stopWebEnrolmentCondition,
      },
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  onChange(event, key, sub?) {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;

    if (sub) {
      this.setState({
        [sub]: {
          ...this.state[sub],
          [key]: value,
        },
      });

      return;
    }

    this.setState({
      [key]: value,
    });

  }

  onSave() {
    const {onSave} = this.props;
    onSave(this.state);
  }

  render() {
    const {enableSocialMedia, addThisId, enableForCourse, enableForWebpage, classAge} = this.state;

    return (
      <div>
        <Form>
          <FormGroup>
            <Checkbox
              label="Hide student contact details from tutor."
              name="enableSocialMedia"
              checked={enableSocialMedia}
              onChange={e => {this.onChange(e, 'enableSocialMedia');}}
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="addThisId">AddThis profile id</Label>
            <Row>
              <Col sm={3}>
                <Input
                  type="text"
                  name="addThisId"
                  id="addThisId"
                  value={addThisId}
                  onChange={e => this.onChange(e, 'addThisId')}
                />
                <FormText><a target="_blank" href="http://www.addthis.com/">Click here</a> to to create one.</FormText>
              </Col>
            </Row>
          </FormGroup>

          <h6>Enable for these pages:</h6>
          <FormGroup>
            <Checkbox
              label="Course"
              name="enableForCourse"
              checked={enableForCourse}
              onChange={e => {this.onChange(e, 'enableForCourse');}}
            />
          </FormGroup>

          <FormGroup>
            <Checkbox
              label="Web page"
              name="enableForWebpage"
              checked={enableForWebpage}
              onChange={e => {this.onChange(e, 'enableForWebpage');}}
            />
          </FormGroup>

          <FormGroup>
            <h6>Hide class on website:</h6>
            <div className="form-inline">
              <Input
                type="number"
                name="hideClassDays"
                className="xs"
                value={classAge.hideClassDays}
                onChange={e => {this.onChange(e, 'hideClassDays', 'classAge');}}
              />
              <Label>days</Label>
              <Input
                type="select"
                name="hideClassCondition"
                onChange={e => {this.onChange(e, 'hideClassCondition', 'classAge');}}
              >
                <option value={ClassCondition.afterClassStarts}>after class starts</option>
                <option value={ClassCondition.beforeClassStarts}>before class starts</option>
                <option value={ClassCondition.afterClassEnds}>after class ends</option>
                <option value={ClassCondition.beforeClassEnds}>before class ends</option>
              </Input>
            </div>
          </FormGroup>

          <FormGroup>
            <h6>Stop web enrolments:</h6>
            <div className="form-inline">
              <Input
                type="number"
                name="stopWebEnrolmentDays"
                className="xs"
                value={classAge.stopWebEnrolmentDays}
                onChange={e => {this.onChange(e, 'stopWebEnrolmentDays', 'classAge');}}
              />
              <Label>days</Label>
                <Input
                  type="select"
                  name="stopWebEnrolmentCondition"
                  onChange={e => {this.onChange(e, 'stopWebEnrolmentCondition', 'classAge');}}
                >
                <option value={ClassEnrolmentCondition.afterClassStarts}>after class starts</option>
                <option value={ClassEnrolmentCondition.beforeClassStarts}>before class starts</option>
                <option value={ClassEnrolmentCondition.beforeClassEnds}>before class ends</option>
              </Input>
            </div>
          </FormGroup>

          <Button color="primary" onClick={() => this.onSave()}>Save</Button>

        </Form>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  website: state.settings.websiteSettings,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getWebsiteSettings()),
    onSave: settings => dispatch(setWebsiteSettings(settings)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Website);
