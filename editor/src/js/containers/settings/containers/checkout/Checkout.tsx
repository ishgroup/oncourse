import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {Checkbox, FormControlLabel, Grid, Paper, TextField} from "@material-ui/core";
import classnames from "classnames";
import {getCheckoutSettings, setCheckoutSettings} from "./actions";
import {State} from "../../../../reducers/state";
import {CheckoutSettingsState} from "./reducers/State";
import {CheckoutSettings} from "../../../../model";
import {toPositive} from "../../../../common/utils/NumberUtils";
import CustomButton from "../../../../common/components/CustomButton";

interface Props {
  fetching?: boolean;
  onInit: () => any;
  onSave: (settings: CheckoutSettings) => any;
  checkout: CheckoutSettingsState;
}

export class Checkout extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    const checkout = props.checkout;

    this.state = {
      allowCreateContactOnEnrol: checkout.allowCreateContactOnEnrol || false,
      allowCreateContactOnWaitingList:  checkout.allowCreateContactOnWaitingList || false,
      allowCreateContactOnMailingList: checkout.allowCreateContactOnMailingList || false,
      collectParentDetails: checkout.collectParentDetails || false,
      contactAgeWhenNeedParent: checkout.contactAgeWhenNeedParent,
      enrolmentMinAge: checkout.enrolmentMinAge,
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentWillReceiveProps(props: Props) {
    if (props.checkout.refreshSettings) {
      const checkout = props.checkout;

      this.setState({
        allowCreateContactOnEnrol: checkout.allowCreateContactOnEnrol,
        allowCreateContactOnWaitingList:  checkout.allowCreateContactOnWaitingList,
        allowCreateContactOnMailingList: checkout.allowCreateContactOnMailingList,
        collectParentDetails: checkout.collectParentDetails,
        contactAgeWhenNeedParent: checkout.contactAgeWhenNeedParent,
        enrolmentMinAge: checkout.enrolmentMinAge,
      });
    }
  }

  onChange(value, key) {

    this.setState({
      [key]: value,
    });

  }

  onSave() {
    const {onSave} = this.props;
    onSave(this.state);
  }

  render() {
    const {allowCreateContactOnEnrol, allowCreateContactOnWaitingList, allowCreateContactOnMailingList,
      collectParentDetails, enrolmentMinAge, contactAgeWhenNeedParent} = this.state;
    const {fetching} = this.props;

    return (
      <Paper className={classnames({fetching})}>
        <form>
          <Grid className="flex-column">
            <FormControlLabel
              control={
                <Checkbox
                  checked={allowCreateContactOnEnrol}
                  onChange={e => {this.onChange(e.target.checked, 'allowCreateContactOnEnrol')}}
                  name="enableSocialMedia"
                  color="primary"
                />
              }
              label="Allow create new student on enrol."
            />

            <FormControlLabel
              control={
                <Checkbox
                  checked={allowCreateContactOnWaitingList}
                  onChange={e => {this.onChange(e.target.checked, 'allowCreateContactOnWaitingList')}}
                  name="allowCreateContactOnWaitingList"
                  color="primary"
                />
              }
              label="Allow create new student on waiting list."
            />

            <FormControlLabel
              control={
                <Checkbox
                  checked={allowCreateContactOnMailingList}
                  onChange={e => {this.onChange(e.target.checked, 'allowCreateContactOnMailingList')}}
                  name="allowCreateContactOnMailingList"
                  color="primary"
                />
              }
              label="Allow create new student on mailing list"
            />

            <Grid>
              <div className="form-inline">
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={collectParentDetails}
                      onChange={e => {this.onChange(e.target.checked, 'collectParentDetails')}}
                      name="collectParentDetails"
                      color="primary"
                    />
                  }
                  label="Collect parent or guardian details for students under"
                />

                <TextField
                  type="number"
                  name="contactAgeWhenNeedParent"
                  className="xs"
                  value={contactAgeWhenNeedParent}
                  onChange={e => {this.onChange(e.target.value, 'contactAgeWhenNeedParent');}}
                  onBlur={e => {this.onChange(toPositive(e.target.value), 'contactAgeWhenNeedParent');}}
                />

              </div>
            </Grid>
          </Grid>

          <Grid className="mt-2 mb-2">
            <label htmlFor="addThisId">Enrolment min age</label>
            <Grid container>
              <Grid item xs={2}>
                <TextField
                  type="number"
                  name="enrolmentMinAge"
                  id="enrolmentMinAge"
                  value={enrolmentMinAge}
                  onChange={e => this.onChange(e.target.value, 'enrolmentMinAge')}
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
  checkout: state.settings.checkoutSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getCheckoutSettings()),
    onSave: settings => dispatch(setCheckoutSettings(settings)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Checkout as any);
