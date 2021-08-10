import React from 'react';
import { AttendanceTypes } from '@api/model';
import { MaterialIcons } from '@expo/vector-icons';
import { IconProps } from '@expo/vector-icons/build/createIconSet';

const AttendanceIcon = ({ type, ...rest }: { type: AttendanceTypes } & Omit<IconProps<any>, 'name'>) => {
  switch (type) {
    default:
    case 'Attended':
      return <MaterialIcons name="check" {...rest} />;
    case 'Absent with reason':
      return <MaterialIcons name="unsubscribe" {...rest} />;
    case 'Absent without reason':
      return <MaterialIcons name="close" {...rest} />;
    case 'Unmarked':
      return <MaterialIcons name="radio-button-off" {...rest} />;
    case 'Partial':
      return <MaterialIcons name="timelapse" {...rest} />;
  }
};

export default AttendanceIcon;
