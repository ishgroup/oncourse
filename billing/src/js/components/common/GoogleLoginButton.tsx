/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import { GoogleLogin, GoogleLoginResponse, GoogleLogout } from 'react-google-login';
import { Button, Typography } from '@mui/material';
import google from '../../../images/google.svg';
import { useAppDispatch, useAppSelector } from '../../redux/hooks/redux';
import { setGoogleCredentials } from '../../redux/actions/Google';
import { showMessage } from '../../redux/actions';

const SCOPE = 'email '
  + 'profile '
  + 'openid '
  + 'https://www.googleapis.com/auth/userinfo.email '
  + 'https://www.googleapis.com/auth/userinfo.profile '
  + 'https://www.googleapis.com/auth/tagmanager.readonly '
  + 'https://www.googleapis.com/auth/tagmanager.edit.containers '
  + 'https://www.googleapis.com/auth/tagmanager.delete.containers '
  + 'https://www.googleapis.com/auth/tagmanager.edit.containerversions '
  + 'https://www.googleapis.com/auth/tagmanager.publish '
  + 'https://www.googleapis.com/auth/tagmanager.manage.users '
  + 'https://www.googleapis.com/auth/tagmanager.manage.accounts';

const ButtonComp = ({ text, ...props }) => (
  <Button
    {...props}
    disableElevation
    variant="outlined"
    startIcon={<img height={20} width={20} src={google} alt="Google" />}
  >
    {text}
  </Button>
);

const GoogleLoginButton = () => {
  const dispatch = useAppDispatch();
  const profile = useAppSelector((state) => state.google.profile);

  const responseGoogle = (response: GoogleLoginResponse) => {
    dispatch(setGoogleCredentials(
      response.profileObj,
      response.tokenObj
    ));
  };

  const onLogoutSuccess = () => {
    dispatch(setGoogleCredentials(
      null,
      null
    ));
  };

  const onFailure = (error?) => {
    if (error) {
      console.error(error);
    }
    dispatch(
      showMessage({
        message: 'Something went wrong',
        success: false
      })
    );
  };

  return (
    <div>
      <div className="d-flex justify-content-end">
        {
        profile
          ? (
            <GoogleLogout
              onLogoutSuccess={onLogoutSuccess}
              onFailure={onFailure}
              clientId="581533795667-4u647nf45tmmu4gh7mqkgse818hgc9h1.apps.googleusercontent.com"
              render={(props) => (
                <ButtonComp
                  text="Logout"
                  {...props}
                />
              )}
            />
          )
          : (
            <GoogleLogin
              clientId="581533795667-4u647nf45tmmu4gh7mqkgse818hgc9h1.apps.googleusercontent.com"
              onSuccess={responseGoogle}
              onFailure={onFailure}
              scope={SCOPE}
              cookiePolicy="single_host_origin"
              render={(props) => (
                <ButtonComp
                  text="Log in"
                  {...props}
                />
              )}
              isSignedIn
            />
          )
      }
      </div>
      <Typography variant="caption" color="textSecondary">
        {
          profile ? `Logged as ${profile.email}` : 'Login to connect your sites with google services'
        }
      </Typography>
    </div>
  );
};

export default GoogleLoginButton;
