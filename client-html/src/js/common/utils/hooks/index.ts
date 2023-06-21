/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { useEffect, useRef } from 'react';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { Dispatch } from "redux";
import { IAction } from "../../actions/IshAction";
import { State } from "../../../reducers/state";

export const usePrevious = <T = any>(value: any, initial?: any) => {
  const ref = useRef<T>(initial);

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
};

// Redux
export const useAppDispatch = () => useDispatch<Dispatch<IAction>>();
export const useAppSelector: TypedUseSelectorHook<State> = useSelector;
