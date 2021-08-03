import React, {
  useEffect, useMemo, useRef, useState
} from 'react';
import { StyleSheet, View, VirtualizedList } from 'react-native';
import { Appbar } from 'react-native-paper';
import { getDaysInMonth, isSameDay, setDate } from 'date-fns';
import Calendar from './Calendar';
import { HeaderBase } from '../../components/navigation/Header';
import { Day } from '../../model/Timetable';
import { Session } from '../../model/Calendar';

const styles = StyleSheet.create({
  root: {
    flex: 1
  },
  title: {
    textAlign: 'center'
  }
});

const getInitialDays = (): Day[] => {
  const todayMonth = new Date();
  todayMonth.setHours(0, 0, 0, 0);
  return [...Array(getDaysInMonth(todayMonth))].map((d, index) => ({
    date: setDate(todayMonth, index + 1),
    sessions: []
  }));
};

// TODO Add real sessions
const sessions: Session[] = [{
  id: 11,
  name: 'ACT -RSA course',
  collegeName: 'Acme College',
  siteName: 'Sydney Campus',
  roomName: 'Newtown Learning',
  start: '2021-08-02T12:40:44.273Z',
  end: '2021-08-02T18:40:44.273Z',
  color: '#1abc9c'
},
{
  id: 12,
  name: 'Introduction to Quick Enrol',
  collegeName: 'Acme College',
  siteName: 'Sydney Campus',
  roomName: 'Newtown Learning',
  start: '2021-08-11T12:40:44.273Z',
  end: '2021-08-11T14:40:44.273Z',
  color: '#5339f3'
},
{
  id: 13,
  name: 'Certificate III in Early Childhood Education and Care (no units)',
  collegeName: 'Acme College',
  siteName: 'Sydney Campus',
  roomName: 'Newtown Learning',
  start: '2021-08-11T12:40:44.273Z',
  end: '2021-08-11T14:40:44.273Z',
  color: '#f25b3a'
}];

export const TimetableScreen = ({ navigation }) => {
  const [days, setDays] = useState(getInitialDays);
  const ref = useRef<VirtualizedList<Day>>();

  useEffect(() => {
    const updatedDays = days.map((d) => ({
      ...d,
      sessions: sessions.filter((s) => isSameDay(d.date, new Date(s.start)))
    }));

    setDays(updatedDays);
  }, [sessions]);

  const initialScrollIndex = useMemo(
    () => days.findIndex((d) => isSameDay(d.date, new Date())), [days]
  );

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
      <Calendar days={days} ref={ref} initialScrollIndex={initialScrollIndex} />
    </View>
  );
};
