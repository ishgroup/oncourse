import React, { useMemo, useState } from 'react';
import {
  Button, Card, Dialog, TextInput
} from 'react-native-paper';
import {
  StyleSheet,
  Platform,
  View,
  Modal,
  ScrollView,
  TouchableWithoutFeedback,
  Keyboard,
  KeyboardAvoidingView
} from 'react-native';
import { ClassAttendanceItem } from '@api/model';
import { format } from 'date-fns';
import { StatusBar } from 'expo-status-bar';
import { useCommonStyles } from '../../hooks/styles';
import TextField from '../../components/fields/TextField';
import { H_MM_A } from '../../constants/DateTime';
import TimePicker from '../../components/fields/TimePicker';

const style = StyleSheet.create({
  center: {
    alignSelf: 'center'
  },
  picture: {
    height: 411
  }
});

interface Props {
  onDismiss: any;
  onSubmit: any;
  setFieldValue: any;
  index: number;
  attendance: ClassAttendanceItem;
}

const AttendanceContent = ({
  handleSubmit, values, setFieldValue, index, onDismiss
}: {
  handleSubmit: any,
  index: number;
  values: ClassAttendanceItem,
  setFieldValue: any,
  onDismiss: any
}) => {
  const cs = useCommonStyles();
  const [activeDate, setActiveDate] = useState<string>(null);

  const activeDateValue = useMemo(() => (values[activeDate] ? new Date(values[activeDate]) : new Date()), [activeDate, values]);

  const onDismissPicker = () => {
    setActiveDate(null);
  };

  const onConfirmPicker = ({ hours, minutes }) => {
    activeDateValue.setHours(hours, minutes);
    setFieldValue(`attendance[${index}].${activeDate}`, activeDateValue.toISOString());
    setActiveDate(null);
  };

  return (
    <>
      <Card>
        <TimePicker
          hours={activeDateValue.getHours()}
          minutes={activeDateValue.getMinutes()}
          visible={Boolean(activeDate)}
          onDismiss={onDismissPicker}
          onConfirm={onConfirmPicker}
          webStyle={style.center}

        />
        <Card.Title title={values.studentName} subtitle={values.studentEmail} />
        <Card.Cover style={style.picture} source={{ uri: values.studentPicture }} />
        <Card.Content>
          <View style={[cs.flexRow, cs.pt2]}>
            <TextField
              name={`attendance[${index}].arriveTime`}
              label="Arrived"
              mode="outlined"
              style={[cs.flex1, cs.pr1]}
              right={(
                <TextInput.Icon
                  name="clock-outline"
                />
                )}
              format={(v) => format(new Date(v), H_MM_A)}
              onFocus={() => {
                Keyboard.dismiss();
                setActiveDate('arriveTime');
              }}
              showSoftInputOnFocus={false}
            />
            <TextField
              name={`attendance[${index}].departureTime`}
              label="Departured"
              mode="outlined"
              style={[cs.flex1, cs.pl1]}
              right={(
                <TextInput.Icon
                  name="clock-outline"
                />
                )}
              format={(v) => format(new Date(v), H_MM_A)}
              onFocus={() => {
                Keyboard.dismiss();
                setActiveDate('departureTime');
              }}
              showSoftInputOnFocus={false}
            />
          </View>

          <TextField
            name={`attendance[${index}].notes`}
            label="Notes"
            mode="outlined"
            numberOfLines={3}
            scrollEnabled={false}
            multiline
          />
        </Card.Content>
        <Card.Actions style={[cs.justifyContentEnd, cs.pb2, cs.pr2]}>
          <Button onPress={onDismiss} style={cs.mr2}>
            Close
          </Button>
          <Button mode="contained" dark onPress={handleSubmit}>
            Save
          </Button>
        </Card.Actions>
      </Card>
    </>
  );
};

export const AttendanceModal = (
  {
    onDismiss,
    onSubmit,
    attendance,
    setFieldValue,
    index
  }: Props
) => (attendance ? (
  <Dialog style={style.center} visible={index !== null} onDismiss={onDismiss}>
    <AttendanceContent index={index} setFieldValue={setFieldValue} values={attendance} handleSubmit={onSubmit} onDismiss={onDismiss} />
  </Dialog>

) : null);

export const AttendanceModalMobile = (
  {
    onDismiss,
    onSubmit,
    attendance,
    setFieldValue,
    index
  }: Props
) => (attendance ? (
  <Modal
    visible={index !== null}
    onDismiss={onDismiss}
    presentationStyle="fullScreen"
    animationType="slide"
  >
    <StatusBar hidden />
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <ScrollView>
          <AttendanceContent
            values={attendance}
            handleSubmit={onSubmit}
            onDismiss={onDismiss}
            setFieldValue={setFieldValue}
            index={index}
          />
        </ScrollView>
      </TouchableWithoutFeedback>
    </KeyboardAvoidingView>
  </Modal>
) : null);
