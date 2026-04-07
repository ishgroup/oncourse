/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { Dispatch } from 'redux';
import { State } from '../../../reducers/state';
import { IAction } from '../../actions/IshAction';

export const useAppDispatch = () => useDispatch<Dispatch<IAction>>();
export const useAppSelector: TypedUseSelectorHook<State> = useSelector;