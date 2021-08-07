import React, {
  useCallback,
  useEffect,
  useState,
  useRef, useMemo
} from 'react';
import {
  StyleSheet, Platform, View, NativeSyntheticEvent, NativeScrollEvent, FlatList
} from 'react-native';
import {
  Appbar, Dialog
} from 'react-native-paper';
import '@expo/match-media';
import {
  addMonths, format, getDaysInMonth, isSameDay, isSameMonth, setDate
} from 'date-fns';
import debounce from 'lodash.debounce';
import { Session } from '@api/model';
import { useMediaQuery } from 'react-responsive';
import Agenda from './Agenda';
import { HeaderBase } from '../../components/navigation/Header';
import { Day } from '../../model/Timetable';
import Calendar from '../../components/layout/Calendar';
import { MMMM_YYYY } from '../../constants/DateTime';

const styles = StyleSheet.create({
  root: {
    flex: 1
  },
  title: {
    textAlign: 'center'
  },
  content: {
    flex: 1,
    flexDirection: 'row'
  },
  calendar: {
    width: 360,
    backgroundColor: '#fff',
    alignItems: 'center'
  }
});

const today = new Date();
today.setHours(0, 0, 0, 0);

const getMonthDays = (
  month: Date,
  sessions: Session[]
): Day[] => [...Array(getDaysInMonth(month)).keys()]
  .map((d) => {
    const date = setDate(month, d + 1);
    return {
      date,
      sessions: sessions.filter((s) => isSameDay(date, new Date(s.start)))
    };
  });

const getRenderDays = (date: Date, sessions: Session[]): Day[] => {
  const prevMonth = addMonths(date, -1);
  const nextMonth = addMonths(date, 1);
  return [
    ...getMonthDays(prevMonth, sessions),
    ...getMonthDays(date, sessions),
    ...getMonthDays(nextMonth, sessions)
  ];
};

// TODO Add real sessions
const sessions: Session[] = [{
  id: '11',
  name: 'ACT -RSA course',
  collegeName: 'Acme College',
  siteName: 'Sydney Campus',
  roomName: 'Newtown Learning',
  start: '2021-08-04T12:40:44.273Z',
  end: '2021-08-04T18:40:44.273Z',
  color: '#1abc9c'
},
{
  id: '12',
  name: 'Introduction to Quick Enrol',
  collegeName: 'Acme College',
  siteName: 'Sydney Campus',
  roomName: 'Newtown Learning',
  start: '2021-08-11T12:40:44.273Z',
  end: '2021-08-11T14:40:44.273Z',
  color: '#5339f3'
},
{
  id: '13',
  name: 'Certificate III in Early Childhood Education and Care (no units)',
  collegeName: 'Acme College',
  siteName: 'Sydney Campus',
  roomName: 'Newtown Learning',
  start: '2021-08-11T12:40:44.273Z',
  end: '2021-08-11T14:40:44.273Z',
  color: '#f25b3a'
}];

export const TimetableScreen = ({ navigation }) => {
  const [days, setDays] = useState(() => getRenderDays(today, sessions));
  const [month, setCurrentMonth] = useState<Date>(today);
  const [firstVisible, setFirstVisible] = useState<Date>(null);
  const [dialogOpened, setDialogOpened] = useState<boolean>(false);
  const [refreshing, setRefreshing] = useState<boolean>(true);
  const [isMounted, setIsMounted] = useState<boolean>(false);
  const [maintainVisibleContentPosition, setMaintainVisibleContentPosition] = useState(null);

  const ref = useRef<FlatList<Day>>();
  const isSmallScreen = useMediaQuery({ query: '(max-width: 1024px)' });

  const scrollToToday = () => {
    ref.current.scrollToIndex({ index: days.findIndex((d) => isSameDay(d.date, new Date())) });
  };

  const onDayPress = (day) => {
    const index = days.findIndex((d) => isSameDay(d.date, day));

    if (!isSameMonth(firstVisible, day)) {
      setCurrentMonth(day);
    }

    if (index !== -1) {
      ref.current.scrollToIndex({ index });
    } else {
      const updatedDays = getRenderDays(day, sessions);
      setDays(updatedDays);

      setTimeout(() => {
        ref.current.scrollToIndex({ index: updatedDays.findIndex((d) => isSameDay(d.date, day)) });
      }, 200);
    }
  };

  const onRefresh = (monthToAdd: number) => {
    setRefreshing(true);
    const updatedMonth = addMonths(month, monthToAdd);
    const updatedDays = getRenderDays(updatedMonth, sessions);
    setCurrentMonth(updatedMonth);
    setDays(updatedDays);

    // TODO remove when https://github.com/facebook/react-native/pull/29466 will be merged and available
    if (Platform.OS === 'android') {
      ref.current.scrollToIndex({ index: Math.round(updatedDays.length / 2), animated: false });
    }

    setTimeout(() => {
      setRefreshing(false);
    }, 2000);
  };

  const onEndReached = () => {
    onRefresh(1);
  };

  const onStartReached = () => {
    onRefresh(-1);
  };

  const openDialog = () => {
    setDialogOpened(true);
  };

  const onViewableItemsChanged = useCallback(debounce(({ viewableItems }) => {
    if (viewableItems[0]) {
      setFirstVisible(viewableItems[0].item.date);
    }
  }, 50), []);

  const syncScroll = useCallback(debounce((scrolledPersent, onEnd, onStart) => {
    if (scrolledPersent > 85) {
      onEnd();
    }
    if (scrolledPersent < 25) {
      onStart();
    }
  }, 50), []);

  const onScroll = (
    {
      nativeEvent:
        {
          contentOffset,
          layoutMeasurement,
          contentSize
        }
    }: NativeSyntheticEvent<NativeScrollEvent>
  ) => {
    if (isMounted) {
      const scrolledPersent = ((contentOffset.y > layoutMeasurement.height
        ? contentOffset.y + layoutMeasurement.height
        : contentOffset.y
      ) / contentSize.height) * 100;

      syncScroll(scrolledPersent, onEndReached, onStartReached);
    }
  };

  const syncMonth = (isScrolling, prevMonth, newMonth) => {
    if (!isScrolling && newMonth && !isSameMonth(prevMonth, newMonth)) {
      setCurrentMonth(newMonth);
    }
  };

  const updateMonth = useMemo(
    () => debounce(syncMonth, 100),
    []
  );

  useEffect(() => {
    scrollToToday();
    // Initial scroll timeout
    setTimeout(() => {
      setRefreshing(false);
      setMaintainVisibleContentPosition({
        minIndexForVisible: 0,
        autoscrollToTopThreshold: 0.4
      });
      setIsMounted(true);
    }, 2000);
  }, []);

  useEffect(() => {
    updateMonth(refreshing, month, firstVisible);
  }, [firstVisible, refreshing]);

  const monthLabel = useMemo(() => format(month, MMMM_YYYY), [month]);

  const renderCalendar = useMemo(() => (
    <View style={styles.calendar}>
      <Calendar
        month={month}
        sessions={sessions}
        onDayPress={onDayPress}
        firstVisible={firstVisible}
        setCurrentMonth={setCurrentMonth}
        monthLabel={monthLabel}
      />
    </View>
  ), [month, sessions, firstVisible, isSmallScreen]);

  const portal = useMemo(() => isSmallScreen && (
    <Dialog
      visible={dialogOpened}
      onDismiss={() => setDialogOpened(false)}
      style={{ alignItems: 'center', alignSelf: 'center', width: 360 }}
    >
      <Dialog.Content>
        {renderCalendar}
      </Dialog.Content>
    </Dialog>
  ), [dialogOpened, isSmallScreen, renderCalendar]);

  return (
    <View style={styles.root}>
      <HeaderBase>
        <Appbar.Action
          icon="menu"
          color="white"
          onPress={() => navigation.openDrawer()}
        />
        <Appbar.Content titleStyle={isSmallScreen && styles.title} title={monthLabel} color="white" />
        {isSmallScreen && <Appbar.Action onPress={openDialog} icon="calendar" />}
      </HeaderBase>
      <View style={styles.content}>
        <Agenda
          days={days}
          ref={ref}
          onViewableItemsChanged={onViewableItemsChanged}
          initialNumToRender={10}
            // IOS
          maintainVisibleContentPosition={maintainVisibleContentPosition}
          onScroll={onScroll}
          removeClippedSubviews
        />
        {!isSmallScreen && renderCalendar}
      </View>
      {portal}
    </View>
  );
};
