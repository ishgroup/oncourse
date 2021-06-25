import { StyleSheet, ScrollView, View } from 'react-native';
import React from 'react';
import CardBase from '../components/layout/cards/CardBase';

const styles = StyleSheet.create({
  root: {
    flex: 1,
    justifyContent: 'center',
    flexWrap: 'wrap',
    flexDirection: 'row',
    alignContent: 'flex-start'
  }
});

const data = [
  { title: 'Classes', height: 300 },
  { title: 'Resources', height: 250 },
  { title: 'Timetable', height: 200 },
  { title: 'Advanced driving', height: 320 },
  { title: 'Budgeting', height: 280 },
  { title: 'Feedback', height: 220 }
];

export function DashboardScreen({ navigation }) {
  return (
    <ScrollView>
      <View style={styles.root}>
        {data.map(d => <CardBase title={d.title} content={""} />)}
      </View>
    </ScrollView>
  );
}
