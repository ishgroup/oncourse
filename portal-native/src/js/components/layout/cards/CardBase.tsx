import React from 'react';
import { Card } from 'react-native-paper';
import { StyleSheet } from 'react-native';
import { useThemeContext } from '../../../styles';

const styles = StyleSheet.create({
  root: {
    minWidth: 320,
    margin: 16
  }
});

interface Props {
  title: string,
  content: React.ReactNode;
  titleStyle?: 'primary' | 'secondary' | 'transparent',
}

const CardBase = ({ title, content, titleStyle = 'primary' }: Props) => {
  const theme = useThemeContext();

  return (
    <Card style={[styles.root]}>
      <Card.Title
        title={title}
        titleStyle={{ color: titleStyle === 'transparent' ? theme.colors.primary : 'white', fontSize: 14 }}
        style={{ backgroundColor: titleStyle === 'primary' ? theme.colors.primary : theme.colors.secondary, minHeight: 30 }}
      />
      <Card.Content>
        {content}
      </Card.Content>
    </Card>
  );
};

export default CardBase;
