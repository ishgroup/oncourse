import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import { Dispatch } from 'redux';
import { State } from '../model/State';
import { IAction } from '../model/IshAction';

export const useAppDispatch = () => useDispatch<Dispatch<IAction>>();
export const useAppSelector: TypedUseSelectorHook<State> = useSelector;
