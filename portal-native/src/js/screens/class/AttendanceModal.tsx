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
import { Formik } from 'formik';
import { useCommonStyles } from '../../hooks/styles';
import TextField from '../../components/fields/TextField';
import { H_MM_A } from '../../constants/DateTime';
import TimePicker from '../../components/fields/TimePicker';
import AttendanceSelect from './AttendanceSelect';

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
  index: number;
  attendance: ClassAttendanceItem;
}

const AttendanceContent = ({
  handleSubmit, values, setFieldValue, onDismiss
}: {
  handleSubmit: any,
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
    setFieldValue(activeDate, activeDateValue.toISOString());
    setActiveDate(null);
  };

  const onAttendanceChange = (val) => {
    setFieldValue('attendance', val);
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
          <View style={[cs.flexRow, cs.justifyContentEnd, cs.zIndex1, cs.pt2]}>
            <AttendanceSelect value={values.attendance} onChange={onAttendanceChange} />
          </View>
          <View style={[cs.flexRow, cs.pt2]}>
            <TextField
              name="arriveTime"
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
              name="departureTime"
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
            name="notes"
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
    index
  }: Props
) => (attendance ? (
  <>
    <Formik
      initialValues={attendance}
      onSubmit={onSubmit}
    >
      {({ handleSubmit, values, setFieldValue }) => (
        <Dialog style={style.center} visible={index !== null} onDismiss={onDismiss}>
          <AttendanceContent setFieldValue={setFieldValue} values={values} handleSubmit={handleSubmit} onDismiss={onDismiss} />
        </Dialog>
      )}
    </Formik>
  </>
) : null);

export const AttendanceModalMobile = (
  {
    onDismiss,
    onSubmit,
    attendance,
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
          <Formik
            initialValues={attendance}
            onSubmit={onSubmit}
          >
            {({ handleSubmit, values, setFieldValue }) => (
              <AttendanceContent
                values={values}
                handleSubmit={handleSubmit}
                onDismiss={onDismiss}
                setFieldValue={setFieldValue}
              />
            )}
          </Formik>
        </ScrollView>
      </TouchableWithoutFeedback>
    </KeyboardAvoidingView>
  </Modal>
) : null);
