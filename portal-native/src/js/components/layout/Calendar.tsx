import React, { useMemo } from 'react';
import { TouchableOpacity, View } from 'react-native';
import { addMonths, isSameDay } from 'date-fns';
import { IconButton, Subheading } from 'react-native-paper';
import { Session } from '@api/model';
import { getCalendarDays } from '../../constants/DateTime';
import { createStyles } from '../../hooks/styles';

const useStyles = createStyles((theme) => ({
  root: {
    width: 300,
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
    minHeight: 42.84,
    marginTop: 0,
    marginBottom: 0,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 50,
    borderWidth: 1,
    borderStyle: 'solid',
    borderColor: '#fff'
  },
  todayMark: {
    backgroundColor: theme.colors.primary,
  },
  todayMarkColor: {
    color: '#fff'
  },
  marks: {
    bottom: 6,
    paddingTop: 2,
    position: 'absolute',
    justifyContent: 'flex-end',
    alignItems: 'flex-start',
    flexDirection: 'row',
  },
  mark: {
    marginHorizontal: 1,
    borderRadius: 2,
    height: 4,
    width: 4
  },
  flex1: {
    flex: 1
  }
}));

interface Props {
  month: Date;
  firstVisible: Date;
  sessions: Session[];
  onDayPress: (day: Date) => void;
  setCurrentMonth: (month: Date) => void;
  monthLabel: string;
}

const week = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];

const today = new Date();

const Calendar = ({
  sessions, onDayPress, month, firstVisible, setCurrentMonth, monthLabel
}: Props) => {
  const days = useMemo(() => getCalendarDays(month, sessions), [month]);

  const styles = useStyles();

  const renderDays = useMemo(() => days.map((d) => {
    const isToday = isSameDay(d.date, today);

    return (
      <TouchableOpacity
        onPress={() => onDayPress(d.date)}
        key={d.date.toISOString()}
        style={[
          styles.calendarCell,
          {
            borderColor: isSameDay(d.date, firstVisible) ? '#00000061' : '#fff'
          },
          isToday && styles.todayMark
        ]}
      >
        <Subheading
          style={[
            styles.headerLabel,
            isToday ? styles.todayMarkColor : d.status !== 'current' && styles.lightGrayText
          ]}
        >
          {d.day}
          <View style={styles.marks}>
            {d.marks.map((m) => <View key={m} style={[styles.mark, { backgroundColor: m }]} />)}
          </View>
        </Subheading>
      </TouchableOpacity>
    );
  }), [days, styles]);

  return (
    <View style={styles.root}>
      <View style={styles.row}>
        <IconButton
          icon="chevron-left"
          onPress={() => setCurrentMonth(addMonths(month, -1))}
        />
        <View style={[styles.headerLabel, styles.flex1]}>
          <Subheading style={[
            styles.bolder,
            styles.grayText
          ]}
          >
            {monthLabel}
          </Subheading>
        </View>

        <IconButton
          icon="chevron-right"
          onPress={() => setCurrentMonth(addMonths(month, 1))}
        />
      </View>
      <View style={[
        styles.row
      ]}
      >
        {week.map((d, i) => (
          <View
            key={d + i}
            style={styles.calendarCell}
          >
            <Subheading
              style={[
                styles.headerLabel,
                styles.lightGrayText,
              ]}
            >
              {d}
            </Subheading>
          </View>

        ))}
        {renderDays}
      </View>
    </View>
  );
};

export default Calendar;
