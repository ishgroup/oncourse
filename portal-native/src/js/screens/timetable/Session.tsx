import React, { useMemo } from 'react';
import { StyleSheet, View } from 'react-native';
import {
  Caption, Card, Subheading, Title
} from 'react-native-paper';
import '@expo/match-media';
import { Entypo } from '@expo/vector-icons';
import { format } from 'date-fns';
import { useMediaQuery } from 'react-responsive';
import { Session } from '@api/model';
import { spacing } from '../../styles';
import { H_MM_AAA } from '../../constants/DateTime';

const style = StyleSheet.create({
  root: {
    padding: spacing(2),
    marginBottom: spacing(1),
  },
  content: {
    flexDirection: 'row'
  },
  text: {
    color: '#fff',
    lineHeight: spacing(3),
  },
  icon: {
    marginLeft: -3
  },
  description: {
    flex: 1,
    paddingRight: spacing(2)
  }
});

const SessionComp = (
  {
    name,
    collegeName,
    siteName,
    roomName,
    start,
    end,
    color
  }: Session
) => {
  const isSmallScreen = useMediaQuery({ query: '(max-width: 1200px)' });

  const startTime = useMemo(() => format(new Date(start), H_MM_AAA), [start]);

  const endTime = useMemo(() => format(new Date(end), H_MM_AAA), [end]);

  return (
    <Card style={[style.root, { backgroundColor: color }]}>
      <View style={style.content}>
        <View style={style.description}>
          <Title numberOfLines={1} style={style.text}>{name}</Title>
          <Caption numberOfLines={1} style={style.text}>
            <Entypo style={style.icon} name="location-pin" size={18} color="#fff" />
            {collegeName}
            ,
            {' '}
            {siteName}
            ,
            {' '}
            {roomName}
          </Caption>
        </View>
        {isSmallScreen ? (
          <Caption numberOfLines={2} style={style.text}>
            {startTime}
            {' '}
            -
            {'\n'}
            {endTime}
          </Caption>
        ) : (
          <Subheading numberOfLines={2} style={style.text}>
            {startTime}
            {' '}
            -
            {' '}
            {endTime}
          </Subheading>
        )}
      </View>
    </Card>
  );
};

export default SessionComp;
