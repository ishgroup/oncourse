import React from 'react';
import { Card } from 'react-native-paper';
import { StyleSheet } from 'react-native';
import { theme } from '../../styles';

const styles = StyleSheet.create({
  root: {
    minWidth: 320,
    minHeight: 200,
    margin: 16
  }
});

const CardBase = ({ title, content = null, accent = false }) => (
  <Card style={styles.root}>
    <Card.Title
      title={title}
      titleStyle={{ color: 'white', fontSize: 14 }}
      style={{ backgroundColor: accent ? theme.colors.accent : theme.colors.primary, minHeight: 30 }}
    />
    <Card.Content>
      {content}
    </Card.Content>
  </Card>
);

export default CardBase;
