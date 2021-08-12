import React from 'react';
import {
  Divider, Subheading
} from 'react-native-paper';
import { FontAwesome5 } from '@expo/vector-icons';
import {
  ColorValue, StyleProp, StyleSheet, TouchableOpacity, View, ViewStyle
} from 'react-native';
import { ClassResource } from '@api/model';
import * as Linking from 'expo-linking';
import { spacing } from '../../styles';

const FileIcon = (
  {
    mimeType,
    ...rest
  }: {
    mimeType: string,
    size: number,
    color: ColorValue,
    style?: StyleProp<ViewStyle>
  }
) => {
  switch (mimeType) {
    case 'image/tiff':
    case 'image/bmp':
    case 'image/png':
    case 'image/gif':
    case 'image/jpeg':
      return <FontAwesome5 name="file-image" {...rest} />;
    case 'application/pdf':
      return <FontAwesome5 name="file-pdf" {...rest} />;
    case 'application/msword':
      return <FontAwesome5 name="file-word" {...rest} />;
    case 'application/vnd.ms-excel':
      return <FontAwesome5 name="file-excel" {...rest} />;
    case 'application/vnd.ms-powerpoint':
      return <FontAwesome5 name="file-powerpoint" {...rest} />;
    case 'application/zip':
    case 'application/x-gzip':
      return <FontAwesome5 name="file-archive" {...rest} />;
    case 'application/txt':
      return <FontAwesome5 name="file-alt" {...rest} />;
    default:
      return <FontAwesome5 name="file" {...rest} />;
  }
};

const styles = StyleSheet.create({
  root: {
    flexDirection: 'row',
    alignItems: 'flex-end'
  },
  name: {
    paddingBottom: spacing(2)
  },
  icon: {
    paddingRight: spacing(2),
    paddingBottom: spacing(1),
  },
  content: {
    paddingRight: spacing(5),
  }
});

const ResourceCard = ({ mimeType, name, link }: ClassResource) => (
  <TouchableOpacity style={styles.root} onPress={() => Linking.openURL(link)}>
    <FileIcon style={styles.icon} size={44} color="lightgray" mimeType={mimeType} />
    <View style={styles.content}>
      <Subheading style={styles.name}>{name}</Subheading>
      <Divider />
    </View>
  </TouchableOpacity>
);

export default ResourceCard;
