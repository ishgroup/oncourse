import React, { useEffect, useState } from 'react';
import DropDownPicker, {
  RenderListItemPropsInterface
} from 'react-native-dropdown-picker';
import { AttendanceTypes } from '@api/model';
import { StyleSheet, TouchableOpacity, View } from 'react-native';
import {
  Text
} from 'react-native-paper';
import AttendanceIcon from './AttendanceIcon';
import { spacing } from '../../styles';

const styles = StyleSheet.create({
  wrapper: {
    flexDirection: 'row',
    position: 'relative',
  },
  wrapperIcon: {
    position: 'absolute',
    right: 0
  },
  container: {
    width: 200,
    flexDirection: 'row-reverse',
  },
  root: {
    flexDirection: 'row-reverse',
    borderWidth: 0
  },
  item: {
    flexDirection: 'row',
    padding: 4,
    alignItems: 'center',
  },
  itemText: {
    flex: 1
  },
  itemSelected: {
    backgroundColor: 'rgba(0, 0, 0, 0.08)'
  },
  label: {
    paddingRight: spacing(3)
  }
});

const renderListItem = ({
  item, onPress, isSelected
}: RenderListItemPropsInterface & { onPress: any }) => (
  <TouchableOpacity style={[styles.item, isSelected && styles.itemSelected]} onPress={() => onPress(item)}>
    <Text style={styles.itemText}>{item.label}</Text>
    <AttendanceIcon type={item.value as any} size={20} />
  </TouchableOpacity>
);

const items = Object.keys(AttendanceTypes).map((value) => (
  {
    value,
    label: value
  }));

const AttendanceSelect = ({ value, onChange }) => {
  const [open, setOpen] = useState(false);
  const [valueInner, setValue] = useState(value);

  useEffect(() => {
    setValue(value);
  }, [value]);

  return (
    <View style={styles.wrapper}>
      <DropDownPicker
        style={styles.root}
        open={open}
        value={valueInner}
        items={items}
        setOpen={setOpen}
        setValue={setValue}
        onChangeValue={onChange}
        renderListItem={renderListItem}
        listMode="SCROLLVIEW"
        containerStyle={styles.container}
        labelStyle={styles.label}
      />
      <AttendanceIcon style={styles.wrapperIcon} type={value as any} size={20} />
    </View>

  );
};

export default AttendanceSelect;
