import React, { useCallback, useMemo, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { format, getDaysInMonth, isSameDay } from 'date-fns';
import { IconButton, Colors, Subheading } from 'react-native-paper';

import { getCalendarDays, MMMM_YYYY } from '../../constants/DateTime';

const styles = StyleSheet.create({
  root: {
    width: 300,
    height: 300
  },
  row: {
    flexDirection: 'row',
    flexWrap: 'wrap'
  },
  bolder: {
    fontWeight: '500'
  },
  grayText: {
    color: '#00000099',
  },
  lightGrayText: {
    color: '#00000061'
  },
  headerLabel: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  calendarCell: {
    flexBasis: '14.285%',
    borderRadius: 50,
    border: '1px solid transparent',
    marginTop: 0,
    marginBottom: 0
  },
  flex1: {
    flex: 1
  }
});

interface Props {

}

const week = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];

const today = new Date();

const Calendar = ({}: Props) => {
  const [month, setMonth] = useState<Date>(today);

  const monthLabel = useMemo(() => format(month, MMMM_YYYY), [month]);

  const days = useMemo(() => getCalendarDays(month), [month]);

  return (
    <View style={styles.root}>
      <View style={styles.row}>
        <IconButton
          icon="chevron-left"
          onPress={() => console.log('Pressed')}
        />
        <Subheading style={[
          styles.headerLabel,
          styles.bolder,
          styles.grayText,
          styles.flex1]}
        >
          {monthLabel}
        </Subheading>
        <IconButton
          icon="chevron-right"
          onPress={() => console.log('Pressed')}
        />
      </View>
      <View style={[
        styles.flex1,
        styles.row
      ]}
      >
        {week.map((d, i) => (
          <Subheading
            key={d + i}
            style={[
              styles.headerLabel,
              styles.lightGrayText,
              styles.calendarCell
            ]}
          >
            {d}
          </Subheading>
        ))}
        {days.map((d, i) => (
          <Subheading
            key={d.day + i}
            style={[
              styles.headerLabel,
              styles.calendarCell,
              d.status !== 'current' && styles.lightGrayText,
              {
                borderColor: isSameDay(d.date, today) && '#00000061'
              }
            ]}
          >
            {d.day}
          </Subheading>
        ))}
      </View>
    </View>
  );
};

export default Calendar;
