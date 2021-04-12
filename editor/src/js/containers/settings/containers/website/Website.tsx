import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {Checkbox, FormControlLabel, Grid, Paper, Select, TextField} from "@material-ui/core";
import classnames from "classnames";
import {getWebsiteSettings, setWebsiteSettings} from "./actions";
import {Condition, State as SuburbState} from "../../../../model";
import {State} from "../../../../reducers/state";
import {WebsiteSettingsState} from "./reducers/State";
import {toPositive} from "../../../../common/utils/NumberUtils";
import CustomButton from "../../../../common/components/CustomButton";
import {withStyles} from "@material-ui/core/styles";

const styles = theme => ({
  select: {
    width: "230px",
  }
});

interface Props {
  classes: any;
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
    const {classes, fetching} = this.props;

    return (
      <Paper className={classnames({fetching})}>
        <form>
          <Grid>
            <h4>ADD THIS</h4>
            <FormControlLabel
              control={
                <Checkbox
                  checked={enableSocialMedia}
                  onChange={e => {this.onChange(e.target.checked, 'enableSocialMedia')}}
                  name="enableSocialMedia"
                  color="primary"
                />
              }
              label="Enable social media links."
            />
          </Grid>

          <Grid className="mt-2">
            <label htmlFor="addThisId">AddThis profile id</label>
            <Grid container>
              <Grid item xs={3}>
                <TextField
                  type="text"
                  name="addThisId"
                  id="addThisId"
                  value={addThisId}
                  onChange={e => this.onChange(e.target.value, 'addThisId')}
                />
                <p><a target="_blank" href="http://www.addthis.com/">Click here</a> to to create one.</p>
              </Grid>
            </Grid>
          </Grid>

          <Grid className="mt-2 ">
            <h4>VISIBILITY RULES</h4>
            <h6>Enable for these pages</h6>
            <FormControlLabel
              control={
                <Checkbox
                  checked={enableForCourse}
                  onChange={e => {this.onChange(e.target.checked, 'enableForCourse')}}
                  name="enableForCourse"
                  color="primary"
                />
              }
              label="Course"
            />
          </Grid>

          <Grid>
            <FormControlLabel
              control={
                <Checkbox
                  checked={enableForWebpage}
                  onChange={e => {this.onChange(e.target.checked, 'enableForWebpage')}}
                  name="enableForWebpage"
                  color="primary"
                />
              }
              label="Web page"
            />
          </Grid>

          <Grid className="mt-3">
            <h6>Hide class on website</h6>
            <div className="form-inline">
              <TextField
                type="number"
                name="offset"
                className="xs"
                value={classAge.hideClass.offset}
                onChange={e => {this.onChangeClassAge(e.target.value, 'offset', 'hideClass');}}
                onBlur={e => {this.onChangeClassAge(toPositive(e.target.value), 'offset', 'hideClass');}}
              />
              <label className="ml-1 mr-2">days</label>
              <Select
                type="select"
                name="hideClassCondition"
                className={classes.select}
                value={classAge.hideClass.condition}
                onChange={e => {this.onChangeClassAge(e.target.value, 'condition', 'hideClass');}}
              >
                <option value={Condition.afterClassStarts}>after class starts</option>
                <option value={Condition.beforeClassStarts}>before class starts</option>
                <option value={Condition.afterClassEnds}>after class ends</option>
                <option value={Condition.beforeClassEnds}>before class ends</option>
              </Select>
            </div>
          </Grid>

          <Grid className="mt-2">
            <h6>Stop web enrolments</h6>
            <div className="form-inline">
              <TextField
                type="number"
                name="stopWebEnrolmentDays"
                className="xs"
                value={classAge.stopWebEnrolment.offset}
                onChange={e => {this.onChangeClassAge(e.target.value, 'offset', 'stopWebEnrolment');}}
                onBlur={e => {this.onChangeClassAge(toPositive(e.target.value), 'offset', 'stopWebEnrolment');}}
              />
              <label className="ml-1 mr-2">days</label>
                <Select
                  type="select"
                  name="stopWebEnrolmentCondition"
                  className={classes.select}
                  value={classAge.stopWebEnrolment.condition}
                  onChange={e => {this.onChangeClassAge(e.target.value, 'condition', 'stopWebEnrolment');}}
                >
                <option value={Condition.afterClassStarts}>after class starts</option>
                <option value={Condition.beforeClassStarts}>before class starts</option>
                <option value={Condition.beforeClassEnds}>before class ends</option>
              </Select>
            </div>
          </Grid>

          <Grid className="mt-3 mb-2">
            <label>Show suburbs from</label>
            <Grid container>
              <Grid item xs={3}>
                <Select
                  type="select"
                  name="suburbAutocompleteState"
                  value={suburbAutocompleteState}
                  className="w-100"
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
                </Select>
              </Grid>
            </Grid>
          </Grid>

          <CustomButton
            onClick={() => this.onSave()}
            styleType="submit"
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
  website: state.settings.websiteSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getWebsiteSettings()),
    onSave: settings => dispatch(setWebsiteSettings(settings)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Website));
