import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {Checkbox, FormControlLabel, Grid, Paper} from "@material-ui/core";
import {getCheckoutSettings, setCheckoutSettings} from "./actions";
import {State} from "../../../../reducers/state";
import {CheckoutSettingsState} from "./reducers/State";
import {CheckoutSettings} from "../../../../model";
import {toPositive} from "../../../../common/utils/NumberUtils";
import CustomButton from "../../../../common/components/CustomButton";
import clsx from "clsx";
import {stubFunction} from "../../../../common/utils/Components";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";

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
      <Paper className={clsx({fetching}, "p-3")}>
        <Grid className="flex-column">
          <FormControlLabel
            control={
              <Checkbox
                checked={allowCreateContactOnEnrol}
                onChange={e => this.onChange(e.target.checked, 'allowCreateContactOnEnrol')}
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
                onChange={e => this.onChange(e.target.checked, 'allowCreateContactOnWaitingList')}
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
                onChange={e => this.onChange(e.target.checked, 'allowCreateContactOnMailingList')}
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
                    onChange={e => this.onChange(e.target.checked, 'collectParentDetails')}
                    name="collectParentDetails"
                    color="primary"
                  />
                }
                label={
                  <span onClick={e => e.preventDefault()}>
                    Collect parent or guardian details for students under
                    <EditInPlaceField
                      type="number"
                      name="contactAgeWhenNeedParent"
                      meta={{}}
                      input={{
                        onChange: e => this.onChange(e.target.value, 'contactAgeWhenNeedParent'),
                        onBlur: e => this.onChange(toPositive(e), 'contactAgeWhenNeedParent'),
                        onFocus: stubFunction,
                        value: contactAgeWhenNeedParent,
                      }}
                      formatting="inline"
                      style={{width: "40px"}}
                      disableInputOffsets
                      hideArrows
                    />
                  </span>
                }
              />
            </div>
          </Grid>
        </Grid>

        <Grid className="mt-2">
          <EditInPlaceField
            type="number"
            label="Enrolment min age"
            name="enrolmentMinAge"
            id="enrolmentMinAge"
            meta={{}}
            input={{
              onChange: e => this.onChange(e.target.value, 'enrolmentMinAge'),
              onBlur: stubFunction,
              onFocus: stubFunction,
              value: enrolmentMinAge,
            }}
            hideArrows
          />
        </Grid>

        <CustomButton
          styleType="submit"
          onClick={() => this.onSave()}
          styles="mt-2"
        >
          Save
        </CustomButton>
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
