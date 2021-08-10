import React, {
  useCallback, useEffect, useMemo, useState
} from 'react';
import { View } from 'react-native';
import {
  ActivityIndicator, Appbar, Paragraph, Title
} from 'react-native-paper';
import { StackScreenProps } from '@react-navigation/stack';
import { AttendanceTypes, ClassAttendanceItem, CourseClass } from '@api/model';
import { formatDistanceStrict } from 'date-fns';
import debounce from 'lodash.debounce';
import { useAppDispatch, useAppSelector } from '../../hooks/redux';
import { RootStackParamList } from '../../model/Navigation';
import { useCommonStyles } from '../../hooks/styles';
import CourseClassService from '../../services/CourseClassService';
import instantFetchErrorHandler from '../../utils/ApiUtils';
import AttendanceCard from './AttendanceCard';

const ClassRollScreen = (
  {
    navigation,
    route: { params }
  }: StackScreenProps<RootStackParamList>
) => {
  const [courseClass, setCourseClass] = useState<CourseClass>();
  const [dialogOpened, setDialogOpened] = useState(false);

  const dispatch = useAppDispatch();
  const session = useAppSelector((s) => s.sessions.entities.session[params.sessionId]);

  useEffect(() => {
    if (session?.classId) {
      CourseClassService
        .getClass(session.classId)
        .then((res) => {
          setCourseClass(res);
        })
        .catch((e) => instantFetchErrorHandler(dispatch, e));
    }
  }, [session?.classId]);

  const cs = useCommonStyles();

  const onPressBack = () => {
    navigation.canGoBack() ? navigation.goBack() : navigation.navigate('Root');
  };

  const onPressStudentPicture = useCallback(debounce((item: ClassAttendanceItem, attendance: AttendanceTypes) => {
    CourseClassService
      .markAttendance({ ...item, attendance })
      .catch((e) => instantFetchErrorHandler(dispatch, e, 'Something went wrong. Changes not saved'));
  }, 500), []);

  const onPressStudentName = (attendance: ClassAttendanceItem) => {

  };

  const rollLabel = useMemo(() => {
    if (courseClass?.attendance && courseClass?.start) {
      return `${courseClass.attendance.length} student${courseClass.attendance.length === 1 ? '' : 's'}, starting in ${formatDistanceStrict(new Date(), new Date(courseClass.start))}`;
    }
    return null;
  }, [courseClass?.attendance, courseClass?.start]);

  return session && courseClass ? (
    <View style={cs.flex1}>
      <Appbar.Header style={{ backgroundColor: session.classColor }}>
        <Appbar.BackAction color="white" onPress={onPressBack} />
        <Appbar.Content color="white" title={session.name} />
        <Appbar.Action color="white" icon="open-in-new" onPress={() => {}} />
      </Appbar.Header>
      <View style={[cs.flex1, cs.p3, cs.bgThemed]}>
        <Title>
          Description
        </Title>
        <Paragraph style={cs.pb2}>
          {courseClass.description}
        </Paragraph>
        <Title>
          Class Roll
        </Title>
        <Paragraph style={cs.pb2}>
          {rollLabel}
        </Paragraph>
        <View style={cs.flexRow}>
          {courseClass.attendance.map((a) => (
            <AttendanceCard
              key={a.id}
              onPicPress={(newStatus) => onPressStudentPicture(a, newStatus)}
              onNamePress={() => onPressStudentName(a)}
              {...a}
            />
          ))}
        </View>
      </View>
    </View>
  ) : <View style={[cs.flex1, cs.flexCenter]}><ActivityIndicator size="large" /></View>;
};

export default ClassRollScreen;
