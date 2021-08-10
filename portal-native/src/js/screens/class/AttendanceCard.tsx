import React, { useState } from 'react';
import {
  StyleSheet, Image, View, TouchableOpacity
} from 'react-native';
import { AttendanceTypes, ClassAttendanceItem } from '@api/model';
import { Paragraph, TouchableRipple } from 'react-native-paper';
import { spacing } from '../../styles';
import AttendanceIcon from './AttendanceIcon';

const styles = StyleSheet.create({
  root: {
    marginRight: 18,
    marginBottom: 18
  },
  content: {
    width: 180,
    height: 180,
    position: 'relative',
  },
  backDrop: {
    height: 48,
    width: '100%',
    position: 'absolute',
    justifyContent: 'center',
    paddingHorizontal: spacing(2),
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    flexDirection: 'row',
    alignItems: 'center',
    bottom: 0
  },
  image: {
    flex: 1,
    borderStyle: 'solid',
    borderWidth: 1,
    borderColor: 'lightgray'
  },
  nameLabel: {
    color: '#fff',
    flex: 1,
  }
});

interface Props extends ClassAttendanceItem {
  onNamePress: any;
  onPicPress: any;
}

const attendanceBasic: AttendanceTypes[] = [
  'Unmarked',
  'Attended',
  'Absent without reason'
];

const AttendanceCard = (
  {
    studentPicture,
    studentName,
    attendance,
    onPicPress,
    onNamePress
  }: Props
) => {
  const [attendanceType, setAttendanceType] = useState(attendance);

  const onPicPressHandler = () => {
    const prevIndex = attendanceBasic.indexOf(attendanceType);
    const newIndex = prevIndex === attendanceBasic.length - 1 ? 0 : prevIndex + 1;

    setAttendanceType(attendanceBasic[newIndex]);
    onPicPress(attendanceBasic[newIndex]);
  };

  return (
    <TouchableRipple style={styles.root} onPress={onPicPressHandler}>
      <View style={styles.content}>
        <Image
          style={styles.image}
          source={{
            uri: studentPicture
          }}
        />
        <TouchableOpacity style={styles.backDrop} onPress={onNamePress}>
          <Paragraph style={styles.nameLabel}>
            {studentName}
          </Paragraph>
          <AttendanceIcon type={attendanceType} size={24} color="#fff" />
        </TouchableOpacity>
      </View>
    </TouchableRipple>
  );
};

export default AttendanceCard;
