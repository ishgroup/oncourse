import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import classnames from "classnames";
import {getWebsiteSettings, setWebsiteSettings} from "./actions";
import {Checkbox} from "../../../../common/components/Checkbox";
import {ClassCondition} from "../../../../model/settings/ClassCondition";
import {ClassEnrolmentCondition} from "../../../../model/settings/ClassEnrolmentCondition";
import {State} from "../../../../reducers/state";
import {WebsiteSettingsState} from "./reducers/State";

interface Props {
  onInit: () => any;
  onSave: (settings) => any;
  website: WebsiteSettingsState;
  fetching: boolean;
}

export class Website extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    const website = props.website;

    this.state = {
      enableSocialMedia: website.enableSocialMedia,
      addThisId: website.addThisId,
      enableForCourse: website.enableForCourse,
      enableForWebpage: website.enableForWebpage,
      classAge: {
        hideClassDays: website.classAge && website.classAge.hideClassDays,
        hideClassCondition: website.classAge && website.classAge.hideClassCondition,
        stopWebEnrolmentDays: website.classAge && website.classAge.stopWebEnrolmentDays,
        stopWebEnrolmentCondition: website.classAge && website.classAge.stopWebEnrolmentCondition,
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
    const {fetching} = this.props;

    return (
      <div className={classnames({fetching})}>
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

          <h6>Enable for these pages</h6>
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
            <h6>Hide class on website</h6>
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
            <h6>Stop web enrolments</h6>
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
                <option value={ClassCondition.beforeClassEnds}>before class ends</option>
              </Input>
            </div>
          </FormGroup>

          <Button color="primary" onClick={() => this.onSave()}>Save</Button>

        </Form>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  website: state.settings.websiteSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getWebsiteSettings()),
    onSave: settings => dispatch(setWebsiteSettings(settings)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Website);
