import React, { useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { Appbar } from 'react-native-paper';
import { getDaysInMonth, setDate } from 'date-fns';
import Calendar from './Calendar';
import { HeaderBase } from '../../components/navigation/Header';
import { Day } from '../../model/Timetable';

const styles = StyleSheet.create({
  root: {
    flex: 1
  },
  title: {
    textAlign: 'center'
  }
});

// TODO Add sessions
const getInitialDays = (): Day[] => {
  const todayMonth = new Date();
  todayMonth.setHours(0, 0, 0, 0);
  return [...Array(getDaysInMonth(todayMonth))].map((d, index) => ({
    date: setDate(todayMonth, index + 1),
    sessions: []
  }));
};

export const TimetableScreen = ({ navigation }) => {
  const [days, setDays] = useState(getInitialDays);

  return (
    <View style={styles.root}>
      <HeaderBase>
        <Appbar.Action
          icon="menu"
          color="white"
          onPress={() => navigation.openDrawer()}
        />
        <Appbar.Content titleStyle={styles.title} title="Timetable" color="white" />
        <Appbar.Action icon="calendar" />
      </HeaderBase>
      <Calendar days={days} />
    </View>
  );
};
