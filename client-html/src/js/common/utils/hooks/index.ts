/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { useState, useEffect, useRef } from 'react';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { STICKY_HEADER_EVENT } from "../../../constants/Config";
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

export const useComponentVisible = initialIsVisible => {
  const [isComponentVisible, setIsComponentVisible] = useState(initialIsVisible);
  const ref = useRef(null);

  const handleClickOutside = event => {
    if (ref.current && !ref.current.contains(event.target)) {
      setIsComponentVisible(false);
    }
  };

  useEffect(() => {
    document.addEventListener('click', handleClickOutside, true);
    return () => {
      document.removeEventListener('click', handleClickOutside, true);
    };
  });

  return { ref, isComponentVisible, setIsComponentVisible };
};

// Sticky scroll spy
function fire(stuck) {
  const evt = new CustomEvent(STICKY_HEADER_EVENT, { detail: { stuck } });
  document.dispatchEvent(evt);
}

export const useStickyScrollSpy = () => {
  const scrollSpy = e => {
    if (e.target) {
      fire(e.target.scrollTop > 20);
    }
  };

  return { scrollSpy };
};

// Redux
export const useAppDispatch = () => useDispatch<Dispatch<IAction>>();
export const useAppSelector: TypedUseSelectorHook<State> = useSelector;
