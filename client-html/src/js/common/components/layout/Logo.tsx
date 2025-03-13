/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { SVGProps, useContext, useEffect, useState } from 'react';
import IshLogoBlack from '../../../../images/IshLogoBlack.svg';
import IshLogoBlue from '../../../../images/IshLogoBlue.svg';
import IshLogoWhite from '../../../../images/IshLogoWhite.svg';
import IshOncourseLogoBlack from '../../../../images/IshOncourseLogoBlack.svg';
import IshOncourseLogoColour from '../../../../images/IshOncourseLogoColour.svg';
import IshOncourseLogoWhite from '../../../../images/IshOncourseLogoWhite.svg';
import { ThemeContext } from '../../../containers/ThemeContext';
import { useAppSelector } from '../../utils/hooks';

export default ({ whiteBackgound, small, className }: SVGProps<any> & { whiteBackgound?: boolean, small?: boolean, className?: string }) => {
  const themeC = useContext(ThemeContext);

  const [logos, setLogos] = useState({
    IshOncourseLogoBlack,
    IshOncourseLogoColour,
    IshOncourseLogoWhite,
    IshLogoBlack,
    IshLogoBlue,
    IshLogoWhite,
  });

  const logoPreferences = useAppSelector(state => state.preferences.logo);
  
  useEffect(() => {
    if (logoPreferences) {
      setLogos({
        IshOncourseLogoBlack: logoPreferences.customLogoBlack,
        IshOncourseLogoColour: logoPreferences.customLogoColour,
        IshOncourseLogoWhite: logoPreferences.customLogoWhite,
        IshLogoBlack: logoPreferences.customLogoBlackSmall,
        IshLogoBlue: logoPreferences.customLogoColourSmall,
        IshLogoWhite: logoPreferences.customLogoWhiteSmall
      });
    }
    
  }, [logoPreferences]);

  let src = small ? logos.IshLogoBlack : logos.IshOncourseLogoBlack;
  
  if (themeC.themeName === 'dark') {
    src = small ? logos.IshLogoWhite : logos.IshOncourseLogoWhite;
  } else if (whiteBackgound && themeC.themeName === 'default') {
    src = small ? logos.IshLogoBlue : logos.IshOncourseLogoColour;
  }

  return <img className={className} src={src} height={50} width={small ? 50 : 200} alt="IshOncourseLogo" />;
};