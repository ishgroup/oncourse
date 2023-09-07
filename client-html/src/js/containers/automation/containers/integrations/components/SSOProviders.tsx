/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useEffect } from "react";
import { Typography, Button } from "@mui/material";
import okta from "../../../../../../images/okta.svg";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { useAppDispatch } from "../../../../../common/utils/hooks";
import { IntegrationTypesEnum } from "../../../../../model/automation/integrations/IntegrationSchema";
import LoginService from "../../../../login/services/LoginService";

export const OktaButton = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    
    const code = params.get("code");
    
    if (code) {
      LoginService.loginSso("okta", code).then(res => {
        console.log('!!!!!!!!!', res);
      });
    }
    
  }, []);
  
  const openOktaConcent = async () => {
    try {
      const link = await LoginService.getSsoLink("okta");
      window.open(link, "_self");
    } catch (e) {
      instantFetchErrorHandler(dispatch, e);
    }
  };
  
  return <Button
    variant="outlined"
    color="inherit"
    onClick={openOktaConcent}
  >
    <img src={okta} width={80} alt="okta"/>
  </Button>;
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
      <Typography component="div" variant="caption" align="center" className="mb-2">or login with</Typography>
      <div className="w-100 d-flex justify-content-center">
        {
          providers.map(p => getProvider(p))
        }
      </div>
    </div>
  </div> : null;
};