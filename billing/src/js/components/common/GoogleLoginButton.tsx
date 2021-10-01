/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import { GoogleLogin, GoogleLogout } from 'react-google-login';
import { Button } from '@mui/material';
import google from '../../../images/google.svg';

const responseGoogle = (response) => {
  console.log(response);
};

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

const GoogleLoginButton = () => {


  return <GoogleLogin
    clientId="581533795667-4u647nf45tmmu4gh7mqkgse818hgc9h1.apps.googleusercontent.com"
    onSuccess={responseGoogle}
    onFailure={responseGoogle}
    scope={SCOPE}
    cookiePolicy="single_host_origin"
    render={(props) => (
      <Button
        {...props}
        disableElevation
        variant="outlined"
        startIcon={<img height={20} width={20} src={google} alt="Google" />}
      >
        Log in
      </Button>
    )}
    isSignedIn
  />
};

export default GoogleLoginButton;
