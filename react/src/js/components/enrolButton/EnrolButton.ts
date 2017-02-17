import * as React from 'react';
import {plural} from '../../lib/utils';
import {AddButton, AddButtonProps} from '../addButton/AddButton';
import nativeExtend from './EnrolButton.extend';

const custom = {}; // require('./EnrolButton.custom');
const extend = Object.assign({}, nativeExtend, custom);

export type EnrolButtonCommonProps = EnrolButtonProps & AddButtonProps;

export class EnrolButton extends AddButton<EnrolButtonCommonProps> {

  render() {
    const context = this.getContext();

    Object.assign(context.props, {
      isCanceled: this.props.isCanceled,
      isFinished: this.props.isFinished,
      hasAvailableEnrolmentPlaces: this.props.hasAvailableEnrolmentPlaces,
      allowByApplication: this.props.allowByApplication,
      freePlaces: this.props.freePlaces
    });

    Object.assign(context.utils, {plural});

    return extend.render.apply(context);
  }
}

interface EnrolButtonProps {
  isCanceled: boolean;
  isFinished: boolean;
  hasAvailableEnrolmentPlaces: boolean;
  allowByApplication: boolean;
  freePlaces: number;
}
