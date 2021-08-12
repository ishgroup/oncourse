import React, { useState } from 'react';
import {
  Button, Card, Dialog, TextInput
} from 'react-native-paper';
import {
  StyleSheet, Platform, View, Modal, ScrollView, TouchableHighlight, TouchableWithoutFeedback, Dimensions, StyleProp
} from 'react-native';
import { ClassAttendanceItem } from '@api/model';
import { Formik } from 'formik';
import * as yup from 'yup';
import { format } from 'date-fns';
import TextField from '../../components/fields/TextField';
import { useCommonStyles } from '../../hooks/styles';

import { H_MM_A } from '../../constants/DateTime';

const style = StyleSheet.create({
  card: {
    alignSelf: 'center'
  },
  picture: {
    minHeight: 411
  }
});

interface Props {
  visible: boolean;
  onDismiss: any;
  onSubmit: any;
  attendance: ClassAttendanceItem;
}

const validationSchema = yup.object({
  arriveTime: yup.string().nullable().notRequired(),
  departureTime: yup.mixed().nullable().notRequired()
});

const AttendanceContent = ({
  handleSubmit, values, onDismiss
}: {
  handleSubmit: any,
  values: ClassAttendanceItem,
  onDismiss: any,
}) => {
  const cs = useCommonStyles();
  return (
    <Card>
      <Card.Title title={values.studentName} subtitle={values.studentEmail} />
      <Card.Cover style={style.picture} source={{ uri: values.studentPicture }} />
      <Card.Content>
        <View style={[cs.flexRow, cs.pt2]}>
          <TextField
            name="arriveTime"
            label="Arrived"
            mode="outlined"
            style={cs.pr1}
            right={(
              <TextInput.Icon
                name="clock-outline"
              />
              )}
            format={(v) => format(new Date(v), H_MM_A)}
          />
          <TextField
            name="departureTime"
            label="Departured"
            mode="outlined"
            style={cs.pl1}
            right={(
              <TextInput.Icon
                name="clock-outline"
              />
              )}
            format={(v) => format(new Date(v), H_MM_A)}
          />
        </View>

        <TextField
          name="notes"
          label="Notes"
          mode="outlined"
          numberOfLines={3}
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

  );
};

export const AttendanceModal = (
  {
    visible,
    onDismiss,
    onSubmit,
    attendance
  }: Props
) => (attendance ? (
  <Formik
    initialValues={attendance}
    validationSchema={validationSchema}
    onSubmit={onSubmit}
  >
    {({ handleSubmit, values }) => (
      <Dialog style={style.card} visible={visible} onDismiss={onDismiss}>
        <AttendanceContent values={values} handleSubmit={handleSubmit} onDismiss={onDismiss} />
      </Dialog>
    )}
  </Formik>
) : null);

export const AttendanceModalMobile = (
  {
    visible,
    onDismiss,
    onSubmit,
    attendance
  }: Props
) => (attendance ? (
  <Formik
    initialValues={attendance}
    validationSchema={validationSchema}
    onSubmit={onSubmit}
  >
    {({ handleSubmit, values }) => (

        <Modal
          visible={visible}
          onDismiss={onDismiss}
          presentationStyle="fullScreen"
          animationType="slide"
        >
          <ScrollView>
            <AttendanceContent
              values={values}
              handleSubmit={handleSubmit}
              onDismiss={onDismiss}
            />
          </ScrollView>
        </Modal>


    )}
  </Formik>
) : null);
