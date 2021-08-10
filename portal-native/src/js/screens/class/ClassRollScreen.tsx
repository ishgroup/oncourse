import React, { useEffect, useState } from 'react';
import { View } from 'react-native';
import {
  ActivityIndicator, Appbar, Paragraph, Title
} from 'react-native-paper';
import { StackScreenProps } from '@react-navigation/stack';
import { CourseClass } from '@api/model';
import { useAppSelector } from '../../hooks/redux';
import { RootStackParamList } from '../../model/Navigation';
import { createStyles, useCommonStyles } from '../../hooks/styles';
import CourseClassService from '../../services/CourseClassService';

const useStyles = createStyles((theme) => ({
  root: {
    flex: 1,
    backgroundColor: theme.colors.background
  }
}))

const ClassRollScreen = (
  {
    navigation,
    route: { params }
  }: StackScreenProps<RootStackParamList>
) => {
  const [courseClass, setCourseClass] = useState<CourseClass>(null);

  const session = useAppSelector((s) => s.sessions.entities.session[params.sessionId]);

  useEffect(() => {
    if (session?.classId) {
      CourseClassService.getClass(session.classId)
        .then((res) => {
          setCourseClass(res);
        });
    }
  }, [session?.classId]);

  const cs = useCommonStyles();

  const onPressBack = () => {
    navigation.canGoBack() ? navigation.goBack() : navigation.navigate('Root');
  };

  return session && courseClass ? (
    <View style={cs.flex1}>
      <Appbar.Header style={{ backgroundColor: session.classColor }}>
        <Appbar.BackAction color="white" onPress={onPressBack} />
        <Appbar.Content color="white" title={session.name} />
        <Appbar.Action color="white" icon="open-in-new" onPress={() => {}} />
      </Appbar.Header>
      <View style={cs.flex1}>
        <Title>
          Description
        </Title>
        <Paragraph>
          {courseClass.description}
        </Paragraph>

      </View>

    </View>
  ) : <View style={[cs.flex1, cs.flexCenter]}><ActivityIndicator size="large" /></View>;
};

export default ClassRollScreen;
