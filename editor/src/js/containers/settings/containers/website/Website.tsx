import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import classnames from "classnames";
import {getWebsiteSettings, setWebsiteSettings} from "./actions";
import {Checkbox} from "../../../../common/components/Checkbox";
import {Condition, State as SuburbState} from "../../../../model";
import {State} from "../../../../reducers/state";
import {WebsiteSettingsState} from "./reducers/State";
import {toPositive} from "../../../../common/utils/NumberUtils";

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
      suburbAutocompleteState: website.suburbAutocompleteState,
      classAge: {
        hideClass: (website.classAge && website.classAge.hideClass) || {},
        stopWebEnrolment: (website.classAge && website.classAge.stopWebEnrolment) || {},
      },
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentWillReceiveProps(props: Props) {
    if (props.website.refreshSettings) {
      const website = props.website;

      this.setState({
        enableSocialMedia: website.enableSocialMedia,
        addThisId: website.addThisId,
        enableForCourse: website.enableForCourse,
        enableForWebpage: website.enableForWebpage,
        suburbAutocompleteState: website.suburbAutocompleteState,
        classAge: {
          hideClass: website.classAge && website.classAge.hideClass || {},
          stopWebEnrolment: website.classAge && website.classAge.stopWebEnrolment || {},
        },
      });
    }
  }

  onChange(value, key, parent?) {

    if (parent) {
      this.setState({
        [parent]: {
          ...this.state[parent],
          [key]: value,
        },
      });

      return;
    }

    this.setState({
      [key]: value,
    });

  }

  onChangeClassAge (value, key, parent) {
    this.onChange({...this.state.classAge[parent], [key]: value}, parent, 'classAge');
  }

  onSave() {
    const {onSave} = this.props;
    onSave(this.state);
  }

  render() {
    const {
      enableSocialMedia, addThisId, enableForCourse, enableForWebpage, classAge, suburbAutocompleteState,
    } = this.state;
    const {fetching} = this.props;

    return (
      <div className={classnames({fetching})}>
        <Form>
          <FormGroup>
            <h4>ADD THIS</h4>
            <Checkbox
              label="Enable social media links."
              name="enableSocialMedia"
              checked={enableSocialMedia}
              onChange={e => {this.onChange(e.target.checked, 'enableSocialMedia');}}
            />
          </FormGroup>

          <FormGroup className="mb-6">
            <Label htmlFor="addThisId">AddThis profile id</Label>
            <Row>
              <Col sm={3}>
                <Input
                  type="text"
                  name="addThisId"
                  id="addThisId"
                  value={addThisId}
                  onChange={e => this.onChange(e.target.value, 'addThisId')}
                />
                <FormText><a target="_blank" href="http://www.addthis.com/">Click here</a> to to create one.</FormText>
              </Col>
            </Row>
          </FormGroup>

          <FormGroup className="pt-2">
            <h4>VISIBILITY RULES</h4>
            <h6>Enable for these pages</h6>
            <Checkbox
              label="Course"
              name="enableForCourse"
              checked={enableForCourse}
              onChange={e => {this.onChange(e.target.checked, 'enableForCourse');}}
            />
          </FormGroup>

          <FormGroup>
            <Checkbox
              label="Web page"
              name="enableForWebpage"
              checked={enableForWebpage}
              onChange={e => {this.onChange(e.target.checked, 'enableForWebpage');}}
            />
          </FormGroup>

          <FormGroup>
            <h6>Hide class on website</h6>
            <div className="form-inline">
              <Input
                type="number"
                name="offset"
                className="xs"
                value={classAge.hideClass.offset}
                onChange={e => {this.onChangeClassAge(e.target.value, 'offset', 'hideClass');}}
                onBlur={e => {this.onChangeClassAge(toPositive(e.target.value), 'offset', 'hideClass');}}
              />
              <Label>days</Label>
              <Input
                type="select"
                name="hideClassCondition"
                value={classAge.hideClass.condition}
                onChange={e => {this.onChangeClassAge(e.target.value, 'condition', 'hideClass');}}
              >
                <option value={Condition.afterClassStarts}>after class starts</option>
                <option value={Condition.beforeClassStarts}>before class starts</option>
                <option value={Condition.afterClassEnds}>after class ends</option>
                <option value={Condition.beforeClassEnds}>before class ends</option>
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
                value={classAge.stopWebEnrolment.offset}
                onChange={e => {this.onChangeClassAge(e.target.value, 'offset', 'stopWebEnrolment');}}
                onBlur={e => {this.onChangeClassAge(toPositive(e.target.value), 'offset', 'stopWebEnrolment');}}
              />
              <Label>days</Label>
                <Input
                  type="select"
                  name="stopWebEnrolmentCondition"
                  value={classAge.stopWebEnrolment.condition}
                  onChange={e => {this.onChangeClassAge(e.target.value, 'condition', 'stopWebEnrolment');}}
                >
                <option value={Condition.afterClassStarts}>after class starts</option>
                <option value={Condition.beforeClassStarts}>before class starts</option>
                <option value={Condition.beforeClassEnds}>before class ends</option>
              </Input>
            </div>
          </FormGroup>

          <FormGroup>
            <Label>Show suburbs from</Label>
            <Row>
              <Col sm={3}>
                <Input
                  type="select"
                  name="suburbAutocompleteState"
                  value={suburbAutocompleteState}
                  onChange={e => {
                    this.onChange(e.target.value, 'suburbAutocompleteState');
                  }}
                >
                  <option value={null}>All states</option>
                  <option value={SuburbState.NSW}>NSW</option>
                  <option value={SuburbState.QLD}>Queensland</option>
                  <option value={SuburbState.VIC}>Victoria</option>
                  <option value={SuburbState.TAS}>Tasmania</option>
                  <option value={SuburbState.ACT}>ACT</option>
                  <option value={SuburbState.WA}>Western Australia</option>
                  <option value={SuburbState.SA}>South Australia</option>
                </Input>
              </Col>
            </Row>
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

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Website as any);
