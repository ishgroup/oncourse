import React from 'react';
import { ListRenderItemInfo, StyleSheet, View, VirtualizedList } from 'react-native';
import { Caption, Divider, Title } from 'react-native-paper';
import { getDate, getDay } from 'date-fns';
import { Day } from '../../model/Timetable';
import { getShortWeekDay } from '../../utils/DateUtils';

const styles = StyleSheet.create({
  item: {
    marginVertical: 8,
    marginHorizontal: 16,
    padding: 20,
  },
  dayLabel: {
    textAlign: 'center',
    fontWeight: 'normal',
    fontSize: 24
  },
  weekDay: {
    paddingTop: 8
  },
  dayRow: {
    flexDirection: 'row',
    padding: 8,
  },
  dayColumn: {
    width: 50
  },
  sessionsColumn: {
    paddingTop: 16,
    paddingLeft: 12,
    flex: 1
  }
});

interface Props {
  days: Day[]
}

const renderDay = ({ item }: ListRenderItemInfo<Day>) => {
  const day = getDate(item.date);
  const weekDay = getShortWeekDay(getDay(item.date));

  return (
    <View style={styles.dayRow}>
      <View style={styles.dayColumn}>
        <Title style={styles.dayLabel}>
          {day}
        </Title>
        <Caption style={[styles.dayLabel, styles.weekDay]}>
          {weekDay}
        </Caption>
      </View>
      <View style={styles.sessionsColumn}>
        {item.sessions.length
          ? item.sessions.map((s) => s)
          : <Divider />}
      </View>
    </View>
  );
};

const Calendar = ({ days = [] }: Props) => (
  <VirtualizedList
    data={days}
    getItem={(i, n) => days[n]}
    keyExtractor={(item) => item.date.toISOString()}
    getItemCount={() => days.length}
    renderItem={renderDay}
  />
);

export default Calendar;
