import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import clsx from "clsx";
import {Checkbox, FormControlLabel, Grid, Paper, Typography} from "@material-ui/core";
import {getWebsiteSettings, setWebsiteSettings} from "./actions";
import {Condition, State as SuburbState} from "../../../../model";
import {State} from "../../../../reducers/state";
import {WebsiteSettingsState} from "./reducers/State";
import {toPositive} from "../../../../common/utils/NumberUtils";
import CustomButton from "../../../../common/components/CustomButton";
import {withStyles} from "@material-ui/core/styles";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";
import {stubFunction} from "../../../../common/utils/Components";

const styles = () => ({
  select: {
    width: "230px",
  },
  number: {
    width: "50px",
  },
  websiteWrapper: {
    maxHeight: "calc(100vh - 30px)",
    overflowY: "auto",
    boxSizing: "border-box",
  }
});

const conditionItems = [
  {
    value: Condition.afterClassStarts,
    label: "after class starts",
  },
  {
    value: Condition.beforeClassStarts,
    label: "before class starts",
  },
  {
    value: Condition.afterClassEnds,
    label: "after class ends",
  },
  {
    value: Condition.beforeClassEnds,
    label: "before class ends",
  },
];

const suburbItems = [
  {
    value: "default",
    label: "All states",
  },
  {
    value: SuburbState.NSW,
    label: "NSW",
  },
  {
    value: SuburbState.QLD,
    label: "Queensland",
  },
  {
    value: SuburbState.VIC,
    label: "Victoria",
  },
  {
    value: SuburbState.TAS,
    label: "Tasmania",
  },
  {
    value: SuburbState.ACT,
    label: "ACT",
  },
  {
    value: SuburbState.WA,
    label: "Western Australia",
  },
  {
    value: SuburbState.SA,
    label: "South Australia",
  },
];

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
      enableSocialMedia: website.enableSocialMedia || false,
      addThisId: website.addThisId || "",
      moveGTM: website.moveGTM || "",
      rootTagFilter: website.rootTagFilter || "",
      enableForCourse: website.enableForCourse || false,
      enableForWebpage: website.enableForWebpage || false,
      suburbAutocompleteState: website.suburbAutocompleteState,
      classAge: {
        hideClass: (website.classAge && website.classAge.hideClass)
          || {offset: 0, condition: Condition.afterClassStarts},
        stopWebEnrolment: (website.classAge && website.classAge.stopWebEnrolment)
          || {offset: 0, condition: Condition.afterClassStarts},
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
      enableSocialMedia, addThisId, enableForCourse, enableForWebpage, classAge, suburbAutocompleteState, moveGTM, rootTagFilter
    } = this.state;
    const {classes, fetching} = this.props;

    return (
      <Paper className={clsx((fetching && "fetching"), "p-3", classes.websiteWrapper)}>
        <div>
          <h4 className="heading mb-1">ADD THIS</h4>
          <FormControlLabel
            control={
              <Checkbox
                checked={enableSocialMedia}
                onChange={e => this.onChange(e.target.checked, 'enableSocialMedia')}
                name="enableSocialMedia"
                color="primary"
              />
            }
            label="Enable social media links."
          />
        </div>

        <Grid container className="mt-1">
          <Grid item xs={3}>
            <EditInPlaceField
              label="AddThis profile id"
              name="addThisId"
              id="addThisId"
              meta={{}}
              input={{
                onChange: e => this.onChange(e.target.value, 'addThisId'),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: addThisId,
              }}
            />
            <p><a target="_blank" href="http://www.addthis.com/">Click here</a> to to create one.</p>
          </Grid>
        </Grid>

        <div className="mt-2">
          <h4 className="heading">VISIBILITY RULES</h4>
          <Typography variant="caption" component="h6">Enable for these pages</Typography>
          <FormControlLabel
            control={
              <Checkbox
                checked={enableForCourse}
                onChange={e => this.onChange(e.target.checked, 'enableForCourse')}
                name="enableForCourse"
                color="primary"
              />
            }
            label="Course"
          />
        </div>

        <div>
          <FormControlLabel
            control={
              <Checkbox
                checked={enableForWebpage}
                onChange={e => this.onChange(e.target.checked, 'enableForWebpage')}
                name="enableForWebpage"
                color="primary"
              />
            }
            label="Web page"
          />
        </div>

        <div className="mt-3">
          <Typography variant="caption" component="h6">Hide class on website</Typography>
          <Typography>
            <EditInPlaceField
              type="number"
              name="offset"
              id="addThisId"
              meta={{}}
              input={{
                onChange: e => this.onChangeClassAge(e.target.value, 'offset', 'hideClass'),
                onBlur: e => this.onChangeClassAge(toPositive(e), 'offset', 'hideClass'),
                onFocus: stubFunction,
                value: classAge.hideClass.offset,
              }}
              className={classes.number}
              formatting="inline"
              disableInputOffsets
              hideArrows
            />

            <label className="ml-1 mr-2">days</label>

            <EditInPlaceField
              select
              name="hideClassCondition"
              id="addThisId"
              className={classes.select}
              meta={{}}
              input={{
                onChange: e => this.onChangeClassAge(e, 'condition', 'hideClass'),
                onBlur: stubFunction,
                onFocus: stubFunction,
                value: classAge.hideClass.condition,
              }}
              items={conditionItems}
              formatting="inline"
            />
          </Typography>
        </div>

        <div className="mt-2">
          <Typography variant="caption" component="h6">Stop web enrolments</Typography>
          <Typography>
            <EditInPlaceField
              type="number"
              name="stopWebEnrolmentDays"
              meta={{}}
              input={{
                onChange: e => this.onChangeClassAge(e.target.value, 'offset', 'stopWebEnrolment'),
                onBlur: e => this.onChangeClassAge(toPositive(e), 'offset', 'stopWebEnrolment'),
                onFocus: stubFunction,
                value: classAge.stopWebEnrolment.offset,
              }}
              className={classes.number}
              formatting="inline"
              disableInputOffsets
              hideArrows
            />

            <label className="ml-1 mr-2">days</label>

            <EditInPlaceField
              select
              name="stopWebEnrolmentCondition"
              className={classes.select}
              meta={{}}
              input={{
                onChange: e => this.onChangeClassAge(e, 'condition', 'stopWebEnrolment'),
                onBlur: stubFunction,
                onFocus: stubFunction,
                value: classAge.stopWebEnrolment.condition,
              }}
              items={conditionItems.filter(i => i.value !== Condition.afterClassEnds)}
              formatting="inline"
            />
          </Typography>
        </div>

        <div className="mt-3 mb-2">
          <EditInPlaceField
            select
            label="Show suburbs from"
            name="suburbAutocompleteState"
            meta={{}}
            input={{
              onChange: e => this.onChange(e, 'suburbAutocompleteState'),
              onBlur: stubFunction,
              onFocus: stubFunction,
              value: suburbAutocompleteState || "default",
            }}
            items={suburbItems}
          />
        </div>

        {/*<Grid container className="mt-1">*/}
        {/*  <Grid item xs={3} className={"mr-2"}>*/}
        {/*    <EditInPlaceField*/}
        {/*      label="Move GTM"*/}
        {/*      name="moveGTM"*/}
        {/*      id="moveGTM"*/}
        {/*      meta={{}}*/}
        {/*      input={{*/}
        {/*        onChange: e => this.onChange(e.target.value, 'moveGTM'),*/}
        {/*        onFocus: stubFunction,*/}
        {/*        onBlur: stubFunction,*/}
        {/*        value: moveGTM,*/}
        {/*      }}*/}
        {/*    />*/}
        {/*  </Grid>*/}

        {/*  <Grid item xs={3}>*/}
        {/*    <EditInPlaceField*/}
        {/*      label="Root tag filter"*/}
        {/*      name="rootTagFilter"*/}
        {/*      id="rootTagFilter"*/}
        {/*      meta={{}}*/}
        {/*      input={{*/}
        {/*        onChange: e => this.onChange(e.target.value, 'rootTagFilter'),*/}
        {/*        onFocus: stubFunction,*/}
        {/*        onBlur: stubFunction,*/}
        {/*        value: rootTagFilter,*/}
        {/*      }}*/}
        {/*    />*/}
        {/*  </Grid>*/}
        {/*</Grid>*/}

        <CustomButton
          onClick={() => this.onSave()}
          styleType="submit"
          styles="mt-2"
        >
          Save
        </CustomButton>
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

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles as any)(Website));
