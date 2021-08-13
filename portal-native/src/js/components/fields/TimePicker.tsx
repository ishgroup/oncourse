import React from 'react';
import { TimePickerModal } from 'react-native-paper-dates';
import { Platform, StyleProp } from 'react-native';
import { Dialog, Portal } from 'react-native-paper';

interface Props {
  label?: string;
  cancelLabel?: string;
  confirmLabel?: string;
  hours?: number | undefined;
  minutes?: number | undefined;
  visible: boolean | undefined;
  onDismiss: () => any;
  onConfirm: ({ hours, minutes }: {
    hours: number;
    minutes: number;
  }) => any;
  animationType?: 'slide' | 'fade' | 'none';
  webStyle?: StyleProp<any>
}

const TimePicker = ({ webStyle, visible, ...rest }: Props) => (Platform.OS === 'web' ? (
  <Portal>
    <Dialog visible={visible} style={webStyle}>
      <TimePickerModal visible {...rest} locale="en" />
    </Dialog>
  </Portal>

) : <TimePickerModal visible={visible} {...rest} locale="en" />);

export default TimePicker;
