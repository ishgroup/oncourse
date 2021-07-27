import { View } from 'react-native';
import { Caption } from 'react-native-paper';
import React from 'react';
import { useCommonStyles } from '../../hooks/styles';

const EmaiConfirmContent = () => {
  const cs = useCommonStyles();

  return (
    <View style={[cs.flexCenter, cs.flex1, cs.justifyContentCenter]}>
      <Caption style={{ fontSize: 16, textAlign: 'center' }}>
        Please check your email box for message with login link
      </Caption>
    </View>
  );
};

export default EmaiConfirmContent;
