/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {
 makeStyles, Styles, WithStylesOptions, ClassNameMap 
} from "@mui/styles";
import { AppTheme } from '../../model/common/Theme';

export const makeAppStyles = <
  Props extends object = {},
  ClassKey extends string = string>(  
  styles: Styles<AppTheme, Props, ClassKey>,
  options?: Omit<WithStylesOptions<AppTheme>, 'withTheme'>
): keyof Props extends never
  ? (props?: any) => ClassNameMap<ClassKey>
  : (props: Props) => ClassNameMap<ClassKey> => makeStyles<AppTheme>(styles, options);
