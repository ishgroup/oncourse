/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import LoadingButton from '@mui/lab/LoadingButton';
import { Typography } from '@mui/material';
import $t from '@t';
import React, { useCallback, useEffect, useState } from 'react';
import { useHistory } from 'react-router';
import okta from '../../../../../../images/okta.svg';
import instantFetchErrorHandler from '../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import { useAppDispatch } from '../../../../../common/utils/hooks';
import { IntegrationTypesEnum } from '../../../../../model/automation/integrations/IntegrationSchema';
import { postSsoAuthenticationRequest } from '../../../../login/actions';
import LoginService from '../../../../login/services/LoginService';

export const OktaButton = () => {
  const [loading, setLoading] = useState(false);
  const dispatch = useAppDispatch();
  const history = useHistory();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");
    const isKickOut = params.get("isKickOut");

    if (code) {
      dispatch(postSsoAuthenticationRequest("okta", code, isKickOut && JSON.parse(isKickOut)));
      params.delete('code');
      history.push({
        pathname: location.pathname,
        search: ''
      });
    }
  }, []);
  
  const openOktaConcent = async () => {
    setLoading(true);
    try {
      const link = await LoginService.getSsoLink("okta");
      window.open(link, "_self");
    } catch (e) {
      setLoading(false);
      instantFetchErrorHandler(dispatch, e);
    }
  };
  
  return <LoadingButton
    loading={loading}
    variant="outlined"
    color="inherit"
    onClick={openOktaConcent}
  >
    <img src={okta} width={80} alt={$t('okta')} className={loading ? 'invisible' : undefined}/>
  </LoadingButton>;
};

interface SSOProvidersProps {
  providers: number[]
}

export const SSOProviders = ({ providers }: SSOProvidersProps) => {
  
  const getProvider = useCallback((provider: number) => {
    switch (provider) {
      case IntegrationTypesEnum.Okta:
        return <OktaButton key={provider} />;
      default:
        return null;
    }
  }, []);
  
  return providers.length ? <div className="w-100 d-flex justify-content-center">
    <div>
      <Typography component="div" variant="caption" align="center" className="mb-2">{$t('or_login_with')}</Typography>
      <div className="w-100 d-flex justify-content-center">
        {
          providers.map(p => getProvider(p))
        }
      </div>
    </div>
  </div> : null;
};