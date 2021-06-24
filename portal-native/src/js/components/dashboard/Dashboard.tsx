import { StyleSheet, ScrollView, View } from 'react-native';
import React from 'react';
import CardBase from './CardBase';

const styles = StyleSheet.create({
  root: {
    flex: 1,
    justifyContent: 'center',
    flexWrap: 'wrap',
    flexDirection: 'row',
    alignContent: 'flex-start'
  }
});

export function Dashboard({ navigation }) {
  return (
    <ScrollView>
      <View style={styles.root}>
        <CardBase title="Classes" />
        <CardBase title="Resources" />
        <CardBase title="Timetable" accent />
        <CardBase title="Advanced driving"  />
        <CardBase title="Budgeting"  accent />
      </View>
    </ScrollView>
  );
}
