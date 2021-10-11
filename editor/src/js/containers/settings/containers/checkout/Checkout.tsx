import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import {
  Checkbox, FormControlLabel, Grid, Paper
} from '@material-ui/core';
import clsx from 'clsx';
import { getCheckoutSettings, setCheckoutSettings } from './actions';
import { State } from '../../../../reducers/state';
import { CheckoutSettings } from '../../../../model';
import { toPositive } from '../../../../common/utils/NumberUtils';
import CustomButton from '../../../../common/components/CustomButton';
import { stubFunction } from '../../../../common/utils/Components';
import EditInPlaceField from '../../../../common/components/form/form-fields/EditInPlaceField';

interface Props {
  fetching?: boolean;
  onInit: () => any;
  onSave: (settings: CheckoutSettings) => any;
  checkout: CheckoutSettings;
}

interface CompState extends CheckoutSettings{
  termslabelError: string;
  termsUrlError: string;
  collectParentDetails: boolean;
}

export class Checkout extends React.Component<Props, CompState> {
  constructor(props) {
    super(props);

    this.state = {
      ...props.checkout,
      collectParentDetails: Boolean(props.checkout.contactAgeWhenNeedParent),
      termslabelError: null,
      termsUrlError: null,
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  componentDidUpdate(prevProps) {
    const { checkout } = this.props;
    if (prevProps.checkout !== checkout) {
      this.setState((prev) => ({
        ...prev,
        ...checkout,
        ...{
          collectParentDetails: Boolean(checkout.contactAgeWhenNeedParent),
          contactAgeWhenNeedParent: checkout.contactAgeWhenNeedParent || 18
        },

      }));
      this.validateTermsLabel(checkout.termsLabel);
      this.validateTermsUrl(checkout.termsUrl);
    }
  }

  onChange(value, key) {
    this.setState({
      [key]: value,
    } as any);
  }

  onSave = () => {
    const {
      termslabelError, termsUrlError, collectParentDetails, ...rest
    } = this.state;
    this.props.onSave({
      ...rest,
      contactAgeWhenNeedParent: rest.contactAgeWhenNeedParent || 0
    });
  };

  validateTermsLabel = (value) => {
    const termslabelError = value
      ? null
      : 'Field is required';
    if (termslabelError !== this.state.termslabelError) {
      this.setState({
        termslabelError
      });
    }
  };

  validateTermsUrl = (value) => {
    const termsUrlError = value
      ? !(value.startsWith('/') || value.startsWith('https://'))
        ? 'Url should start from / or https://'
        : null
      : 'Field is required';

    if (termsUrlError !== this.state.termsUrlError) {
      this.setState({
        termsUrlError,
      });
    }
  };

  onTermsLabelChange = (e) => {
    this.validateTermsLabel(e.target.value);
    this.onChange(e.target.value, 'termsLabel');
  };

  onTermsUrlChange = (e) => {
    this.validateTermsUrl(e.target.value);
    this.onChange(e.target.value, 'termsUrl');
  };

  render() {
    const {
      allowCreateContact,
      termsLabel,
      termsUrl,
      contactAgeWhenNeedParent,
      termsUrlError,
      termslabelError,
      collectParentDetails
    } = this.state;
    const { fetching } = this.props;

    return (
      <Paper className={clsx({ fetching }, 'p-3')}>
        <div className="flex-column">
          <FormControlLabel
            control={(
              <Checkbox
                checked={allowCreateContact}
                onChange={(e) => this.onChange(e.target.checked, 'allowCreateContact')}
                name="allowCreateContact"
                color="primary"
              />
            )}
            label="Allow create new student"
          />
        </div>

        <div>
          <div className="form-inline">
            <FormControlLabel
              control={(
                <Checkbox
                  checked={collectParentDetails}
                  onChange={(e) => this.onChange(e.target.checked, 'collectParentDetails')}
                  name="collectParentDetails"
                  color="primary"
                />
              )}
              label={(
                <span onClick={(e) => e.preventDefault()}>
                  Collect parent or guardian details for students under
                  <EditInPlaceField
                    type="number"
                    name="contactAgeWhenNeedParent"
                    meta={{}}
                    input={{
                      onChange: (e) => this.onChange(e.target.value, 'contactAgeWhenNeedParent'),
                      onBlur: (e) => this.onChange(toPositive(e), 'contactAgeWhenNeedParent'),
                      onFocus: stubFunction,
                      value: contactAgeWhenNeedParent,
                    }}
                    formatting="inline"
                    style={{ width: '40px' }}
                    disabled={!collectParentDetails}
                    disableInputOffsets
                    hideArrows
                  />
                </span>
              )}
            />
          </div>
        </div>

        <Grid container>
          <Grid item container xs={6}>
            <Grid item xs={12} className="mt-2">
              <EditInPlaceField
                label="Terms and conditions checkbox label"
                name="termsLabel"
                meta={{ error: termslabelError, invalid: Boolean(termslabelError) }}
                input={{
                  onChange: this.onTermsLabelChange,
                  onBlur: stubFunction,
                  onFocus: stubFunction,
                  value: termsLabel,
                }}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} className="mt-2">
              <EditInPlaceField
                label="Terms and conditions url"
                name="termsUrl"
                meta={{ error: termsUrlError, invalid: Boolean(termsUrlError) }}
                input={{
                  onChange: this.onTermsUrlChange,
                  onBlur: stubFunction,
                  onFocus: stubFunction,
                  value: termsUrl,
                }}
                fullWidth
              />
            </Grid>
          </Grid>
        </Grid>

        <CustomButton
          styleType="submit"
          onClick={this.onSave}
          disabled={termsUrlError || termslabelError}
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

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => dispatch(getCheckoutSettings()),
  onSave: (settings) => dispatch(setCheckoutSettings(settings)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Checkout as any);
