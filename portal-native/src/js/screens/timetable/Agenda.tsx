import React from 'react';
import {
  FlatList, FlatListProps,
  ListRenderItemInfo, StyleSheet, View
} from 'react-native';
import { Caption, Divider, Title } from 'react-native-paper';
import {
  getDate, getDay, isSameDay
} from 'date-fns';
import { Day } from '../../model/Timetable';
import Session from './Session';
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
  days: Day[];
}

const isEqual = (prev: ListRenderItemInfo<Day>, next: ListRenderItemInfo<Day>) => {
  let equal = true;

  if ((!prev.item.sessions.length && next.item.sessions.length)
    || !isSameDay(prev.item.date, next.item.date)
  ) {
    equal = false;
  }

  return equal;
};

const DayRow = React.memo(({ item }: ListRenderItemInfo<Day>) => {
  const day = getDate(item.date);
  const weekDay = getShortWeekDay(getDay(item.date));

  return (
    <View key={item.date.toISOString()} style={styles.dayRow}>
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
          ? item.sessions.map((s) => <Session key={s.id} {...s} />)
          : <Divider />}
      </View>
    </View>
  );
}, isEqual);

const renderDay = (props) => <DayRow {...props} />;

const EMPTY_ITEM_HEIGHT = 82;
const ITEM_HEIGHT = 32;
const SESSION_HEIGHT = 98;

const getItemHeight = (item: Day) => (item.sessions.length
  ? ITEM_HEIGHT + (item.sessions.length * SESSION_HEIGHT)
  : EMPTY_ITEM_HEIGHT
);

const getItemLayout = (data: Day[], index) => ({
  length: getItemHeight(data[index]),
  offset: data.slice(0, index).reduce((p, c) => p + getItemHeight(c), 0),
  index
});

const viewabilityConfig = {
  waitForInteraction: false,
  itemVisiblePercentThreshold: 25,
};

const Agenda = ({
  days = [],
  ...rest
}: Props & Partial<FlatListProps<Day>>, ref) => (
  <FlatList
    ref={ref}
    data={days}
    keyExtractor={(item) => item.date.toISOString()}
    renderItem={renderDay}
    getItemLayout={getItemLayout}
    viewabilityConfig={viewabilityConfig}
    {...rest}
  />
);

export default React.memo(React.forwardRef(Agenda));
