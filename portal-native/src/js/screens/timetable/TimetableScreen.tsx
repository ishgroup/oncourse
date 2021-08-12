import React, {
  useCallback,
  useEffect,
  useState,
  useRef, useMemo
} from 'react';
import {
  StyleSheet, Platform, View, FlatList
} from 'react-native';
import {
  Appbar, Dialog
} from 'react-native-paper';
import '@expo/match-media';
import {
  addMonths,
  format,
  getDaysInMonth,
  isSameDay,
  isSameMonth,
  setDate
} from 'date-fns';
import debounce from 'lodash.debounce';
import { Session } from '@api/model';
import { useMediaQuery } from 'react-responsive';
import { denormalize } from 'normalizr';
import { DrawerScreenProps } from '@react-navigation/drawer';
import Agenda from './Agenda';
import { HeaderBase } from '../../components/navigation/Header';
import { Day } from '../../model/Timetable';
import Calendar from '../../components/layout/Calendar';
import { MMMM_YYYY } from '../../constants/DateTime';
import { useAppSelector } from '../../hooks/redux';
import { sessionSchema } from '../../model/Session';
import { RootDrawerParamList } from '../../model/Navigation';

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

const getRenderDays = (date: Date, sessions: Session[]): Day[] => [
  ...getMonthDays(addMonths(date, -2), sessions),
  ...getMonthDays(addMonths(date, -1), sessions),
  ...getMonthDays(date, sessions),
  ...getMonthDays(addMonths(date, 1), sessions),
  ...getMonthDays(addMonths(date, 2), sessions)
];

export const TimetableScreen = ({ navigation }: DrawerScreenProps<RootDrawerParamList, 'Timetable'>) => {
  const plainSessions = useAppSelector(
    (s) => s.sessions
  );
  const sessions = useMemo(() => denormalize(plainSessions.result, sessionSchema, plainSessions.entities),
    [plainSessions]);
  const [days, setDays] = useState(() => getRenderDays(today, sessions));
  const [month, setCurrentMonth] = useState<Date>(today);
  const [firstVisible, setFirstVisible] = useState<Date>(null);
  const [dialogOpened, setDialogOpened] = useState<boolean>(false);
  const [refreshing, setRefreshing] = useState<boolean>(true);
  const [maintainVisibleContentPosition, setMaintainVisibleContentPosition] = useState(null);

  const ref = useRef<FlatList<Day>>();
  const isSmallScreen = useMediaQuery({ query: '(max-width: 1024px)' });

  const scrollToToday = () => {
    ref.current.scrollToIndex({ index: days.findIndex((d) => isSameDay(d.date, new Date())) });
  };

  const onDayPress = (day) => {
    setRefreshing(true);
    const index = days.findIndex((d) => isSameDay(d.date, day));

    if (index !== -1) {
      ref.current.scrollToIndex({ index });
    } else {
      const updatedDays = getRenderDays(day, sessions);
      setDays(updatedDays);

      setTimeout(() => {
        ref.current.scrollToIndex({ index: updatedDays.findIndex((d) => isSameDay(d.date, day)) });
      }, 200);
    }
    setTimeout(() => {
      setRefreshing(false);
    }, 1000);
  };

  const onRefresh = (newMonth) => {
    if (!navigation.isFocused()) return;

    setRefreshing(true);
    const updatedDays = getRenderDays(newMonth, sessions);

    if (!isSameMonth(month, newMonth)) {
      setCurrentMonth(newMonth);
    }

    setDays(updatedDays);

    // TODO remove when https://github.com/facebook/react-native/pull/29466 will be merged and available
    if (Platform.OS === 'android') {
      ref.current.scrollToIndex({ index: Math.round(updatedDays.length / 2), animated: false });
    }

    setTimeout(() => {
      setRefreshing(false);
    }, 1000);
  };

  const onScroll = ({ nativeEvent: { contentOffset, contentSize, layoutMeasurement } }) => {
    if (contentOffset.y === 0) {
      ref.current.scrollToOffset({ offset: contentSize.height * 0.5, animated: false });
    }
    if (contentOffset.y + layoutMeasurement.height === contentSize.height) {
      ref.current.scrollToOffset({ offset: contentSize.height * 0.5, animated: false });
    }
  };

  const openDialog = () => {
    setDialogOpened(true);
  };

  const onViewableItemsChanged = useCallback(debounce(({ viewableItems }) => {
    if (viewableItems[0]) {
      setFirstVisible(viewableItems[0].item.date);
    }
  }, 50), []);

  const syncMonth = (isRefreshing, prevMonth, newMonth) => {
    if (!isRefreshing && newMonth && !isSameMonth(prevMonth, newMonth)) {
      onRefresh(newMonth);
    }
  };

  useEffect(() => {
    scrollToToday();
    // Initial scroll timeout
    setTimeout(() => {
      setRefreshing(false);
      setMaintainVisibleContentPosition({
        minIndexForVisible: 0,
        autoscrollToTopThreshold: 0.4
      });
    }, 2000);
  }, []);

  useEffect(() => {
    syncMonth(refreshing, month, firstVisible);
  }, [firstVisible, refreshing]);

  useEffect(() => {
    setDays(getRenderDays(month, sessions));
  }, [sessions]);

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
