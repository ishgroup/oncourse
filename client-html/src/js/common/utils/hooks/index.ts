/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { Dispatch } from "redux";
import { State } from "../../../reducers/state";
import { IAction } from "../../actions/IshAction";

// Redux
export const useAppDispatch = () => useDispatch<Dispatch<IAction>>();
export const useAppSelector: TypedUseSelectorHook<State> = useSelector;
