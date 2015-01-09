package ish.oncourse.webservices.utils;

import ish.common.types.AvetmissStudentSchoolLevel;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.services.AppModule;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.test.PageTester;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CityEastContactImport extends AbstractUtil {
    private File source;
    private List<String> columns;
    private ObjectContext context;
    private College college;

    public static void main(String[] args) throws Exception {
        //student.specialNeeds	contact.givenName	contact.familyName	contact.isMale	contact.street	contact.suburb	contact.state	contact.postcode	contact.dateOfBirth	student.countryOfBirthId	student.languageID	student.highestSchoolLevel	student.LabourForceType
        CityEastContactImport converter = new CityEastContactImport();
        converter.setUser(args[0]);
        converter.setPassword(args[1]);
        converter.setDataSourceUrl(args[2]);
        converter.init();

        converter.setSource(new File(args[3]));
        converter.convert();
    }

    public void init() throws SQLException, NamingException {
        super.init();

        ArrayList<String> columns = new ArrayList<>();
        columns.add(Contact.STUDENT_PROPERTY + "." + Student.SPECIAL_NEEDS_PROPERTY);
        columns.add(Contact.GIVEN_NAME_PROPERTY);
        columns.add(Contact.FAMILY_NAME_PROPERTY);
        columns.add(Contact.IS_MALE_PROPERTY);
        columns.add(Contact.STREET_PROPERTY);
        columns.add(Contact.SUBURB_PROPERTY);
        columns.add(Contact.STATE_PROPERTY);
        columns.add(Contact.POSTCODE_PROPERTY);
        columns.add(Contact.DATE_OF_BIRTH_PROPERTY);
        columns.add(Contact.STUDENT_PROPERTY + "." + Student.COUNTRY_OF_BIRTH_PROPERTY);
        columns.add(Contact.STUDENT_PROPERTY + "." + Student.LANGUAGE_PROPERTY);
        columns.add(Contact.STUDENT_PROPERTY + "." + Student.HIGHEST_SCHOOL_LEVEL_PROPERTY);
        columns.add(Contact.STUDENT_PROPERTY + "." + Student.LABOUR_FORCE_TYPE_PROPERTY);
        setColumns(columns);

        PageTester tester = new PageTester("ish.oncourse.webservices.services", StringUtils.EMPTY, "src/main/webapp", AppModule.class);
        ICayenneService cayenneService = tester.getService(ICayenneService.class);
        ObjectContext context = cayenneService.newContext();
        setContext(context);

        college = Cayenne.objectForPK(context, College.class, 338);
    }

    public void convert() {
        FileInputStream fin = null;
        InputStream din = null;

        try {
            fin = new FileInputStream(source);
            XSSFWorkbook workbook = new XSSFWorkbook(fin);
            int count = workbook.getNumberOfSheets();
            for (int index = 0; index < count; index++) {
                XSSFSheet sheet = workbook.getSheetAt(index);
                Iterator<Row> rows = sheet.rowIterator();
                boolean isHeaders = true;
                while (rows.hasNext()) {
                    Row row = rows.next();
                    if (isHeaders) {
                        isHeaders = false;
                        continue;
                    }

                    int cCount = row.getLastCellNum();
                    String[] values = new String[columns.size()];
                    for (int i = 0; i < cCount; i++) {
                        Cell cell = row.getCell(i);
                        if (cell == null) {
                            values[i] = null;
                            continue;
                        }
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_BLANK:
                                values[i] = null;
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    values[i] = cell.toString();
                                } else {
                                    values[i] = String.valueOf((int) cell.getNumericCellValue());
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                values[i] = cell.getStringCellValue();
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                values[i] = String.valueOf(cell.getBooleanCellValue());
                                break;
                            default:
                                throw new IllegalArgumentException();

                        }
                    }

//                    updateContact(values);
                    createContact(values);

                }
            }

        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.closeQuietly(fin);
            IOUtils.closeQuietly(din);
        }
    }

    private void updateContact(String[] values) {

        Expression exp = ExpressionFactory.matchExp(Contact.GIVEN_NAME_PROPERTY, values[0]);
        exp = exp.andExp(ExpressionFactory.matchExp(Contact.FAMILY_NAME_PROPERTY, values[1]));
        exp = exp.andExp(ExpressionFactory.matchExp(Contact.EMAIL_ADDRESS_PROPERTY, values[3]));

        SelectQuery q = new SelectQuery(Contact.class, exp);
        List<Contact> contacts = context.performQuery(q);
        if (contacts.isEmpty())
            System.out.println(String.format("Cannot find %s %s %s", values[0], values[1], values[7]));
        if (contacts.size() > 1)
            System.out.println(String.format("More then one %s %s %s", values[0], values[1], values[7]));
        context.commitChanges();
    }

    private void createContact(String[] values) {
        Contact contact = context.newObject(Contact.class);
        contact.setCollege(college);
        Student student = contact.createNewStudent();
        for (int i = 0; i < columns.size(); i++) {
            String property = columns.get(i);
            if (i < values.length) {
                String value = StringUtils.trimToNull(values[i]);
                if (value == null)
                    continue;

                switch (property) {
                    case Contact.GIVEN_NAME_PROPERTY:
                    case Contact.FAMILY_NAME_PROPERTY:
                    case Contact.STREET_PROPERTY:
                    case Contact.SUBURB_PROPERTY:
                    case Contact.STATE_PROPERTY:
                    case Contact.POSTCODE_PROPERTY:
                        contact.writeProperty(property, value);
                        break;
                    case Contact.STUDENT_PROPERTY + "." + Student.SPECIAL_NEEDS_PROPERTY:
                        student.setSpecialNeeds(value);
                        break;
                    case Contact.STUDENT_PROPERTY + "." + Student.LABOUR_FORCE_TYPE_PROPERTY:
                        if (StringUtils.isNumeric(value)) {
                            student.setLabourForceType(Integer.valueOf(value));
                        }
                        break;
                    case Contact.STUDENT_PROPERTY + "." + Student.HIGHEST_SCHOOL_LEVEL_PROPERTY:
                        if (StringUtils.isNumeric(value)) {
                            int dv = Integer.valueOf(value);
                            switch (dv)
                            {
                                case 8:
                                    student.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW);
                                    break;
                                case 9:
                                    student.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9);
                                    break;
                                case 10:
                                    student.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10);
                                    break;
                                case 11:
                                    student.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11);
                                    break;
                                case 12:
                                    student.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12);
                                    break;
                            }
                            break;
                        }
                        break;
                    case Contact.IS_MALE_PROPERTY:
                        switch (value.toLowerCase()) {
                            case "m":
                                contact.setIsMale(true);
                                break;
                            case "f":
                                contact.setIsMale(false);
                                break;
                        }
                        break;
                    case Contact.DATE_OF_BIRTH_PROPERTY:
                        contact.setDateOfBirth(parseBirthDate(value));
                        break;
                    case Contact.STUDENT_PROPERTY + "." + Student.COUNTRY_OF_BIRTH_PROPERTY:
                        if (StringUtils.isNumeric(value)) {
                            student.setCountryOfBirth(Cayenne.objectForPK(context, Country.class, Long.valueOf(value)));
                        }
                        break;
                    case Contact.STUDENT_PROPERTY + "." + Student.LANGUAGE_PROPERTY:
                        if (StringUtils.isNumeric(value)) {
                            student.setLanguage(Cayenne.objectForPK(context, Language.class, Long.valueOf(value)));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
        context.commitChanges();
    }

    public Date parseBirthDate(String s) {
        String[] formats = new String[]{
                "dd-MM-yyyy",
                "MM/dd/yyyy",
        };
        for (int i = 0; i < formats.length; i++) {
            String format = formats[i];
            Date result = parseBirthDate(s, format);
            if (result != null) {
                if (result.after(new Date())) {
                    result = DateUtils.addYears(result, -100);
                }
            }
            return result;
        }
        return null;
    }

    public Date parseBirthDate(String s, String format) {
        if (s == null)
            return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date result = simpleDateFormat.parse(s);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public ObjectContext getContext() {
        return context;
    }

    public void setContext(ObjectContext context) {
        this.context = context;
    }

    public class EventExample
            implements HSSFListener {
        private SSTRecord sstrec;

        @Override
        public void processRecord(Record record) {

            switch (record.getSid()) {
                // the BOFRecord can represent either the beginning of a sheet or the workbook
                case BOFRecord.sid:
                    BOFRecord bof = (BOFRecord) record;
                    if (bof.getType() == bof.TYPE_WORKBOOK) {
                        System.out.println("Encountered workbook");
                        // assigned to the class level member
                    } else if (bof.getType() == bof.TYPE_WORKSHEET) {
                        System.out.println("Encountered sheet reference");
                    }
                    break;
                case BoundSheetRecord.sid:
                    BoundSheetRecord bsr = (BoundSheetRecord) record;
                    System.out.println("New sheet named: " + bsr.getSheetname());
                    break;
                case RowRecord.sid:
                    RowRecord rowrec = (RowRecord) record;
                    System.out.println("Row found, first column at "
                            + rowrec.getFirstCol() + " last column at " + rowrec.getLastCol());
                    break;
                case NumberRecord.sid:
                    NumberRecord numrec = (NumberRecord) record;
                    System.out.println("Cell found with value " + numrec.getValue()
                            + " at row " + numrec.getRow() + " and column " + numrec.getColumn());
                    break;
                // SSTRecords store a array of unique strings used in Excel.
                case SSTRecord.sid:
                    sstrec = (SSTRecord) record;
                    for (int k = 0; k < sstrec.getNumUniqueStrings(); k++) {
                        System.out.println("String table value " + k + " = " + sstrec.getString(k));
                    }
                    break;
                case LabelSSTRecord.sid:
                    LabelSSTRecord lrec = (LabelSSTRecord) record;
                    System.out.println("String cell found with value "
                            + sstrec.getString(lrec.getSSTIndex()));
                    break;
            }
        }
    }
}
