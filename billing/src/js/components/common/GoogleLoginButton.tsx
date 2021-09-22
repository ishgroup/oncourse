/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import { GoogleLogin } from 'react-google-login';

const responseGoogle = (response) => {
  console.log(response);
};

const GoogleLoginButton = (props) => (
  <GoogleLogin
    clientId="581533795667-4u647nf45tmmu4gh7mqkgse818hgc9h1.apps.googleusercontent.com"
    buttonText="Login with google"
    onSuccess={responseGoogle}
    onFailure={responseGoogle}
    cookiePolicy="single_host_origin"
    {...props}
  />
);

export default GoogleLoginButton;
