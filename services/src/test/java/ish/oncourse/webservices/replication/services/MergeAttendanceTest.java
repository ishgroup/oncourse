package ish.oncourse.webservices.replication.services;

import ish.common.types.AttendanceType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.replication.builders.WillowStubBuilderTest;
import ish.oncourse.webservices.soap.ReplicationTestModule;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pavel on 6/21/17.
 */
public class MergeAttendanceTest extends ServiceTest {
    ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);

        InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/services/MergeAttendanceTest.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DataSource onDataSource = getDataSource();
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);

        cayenneService = getService(ICayenneService.class);
    }

    @Test
    public void testAttendanceMerge() throws Exception {
        ObjectContext context = cayenneService.newContext();
        Student studentToUpdate = ObjectSelect.query(Student.class, Student.ANGEL_ID.eq(1L)).selectFirst(context);
        Student studentToDelete = ObjectSelect.query(Student.class, Student.ANGEL_ID.eq(2L)).selectFirst(context);

        MergeAttendances.valueOf(context, studentToUpdate, studentToDelete).merge();

        context.commitChanges();

        context = cayenneService.newContext();

        List<Attendance> resultOfUpdStud = ObjectSelect.query(Attendance.class).where(Attendance.STUDENT.eq(studentToUpdate)).select(context);
        List<Attendance> resultOfDelStud = ObjectSelect.query(Attendance.class).where(Attendance.STUDENT.eq(studentToDelete)).select(context);

        assertNotNull(resultOfUpdStud);
        assertNotNull(resultOfDelStud);

        assertEquals(9, resultOfUpdStud.size());
        assertEquals(0, resultOfDelStud.size());

        //check rank of attendance type

        // type of attendances of single session should be same as before merge, check for first student
        assertEquals(AttendanceType.ATTENDED.getDatabaseValue(),
                ObjectSelect.query(Attendance.class).where(ExpressionFactory.matchDbExp(Attendance.ID_PK_COLUMN, 1L)).selectOne(context)
                        .getAttendanceType());
        assertEquals(AttendanceType.PARTIAL.getDatabaseValue(),
                ObjectSelect.query(Attendance.class).where(ExpressionFactory.matchDbExp(Attendance.ID_PK_COLUMN, 2L)).selectOne(context)
                        .getAttendanceType());
        assertEquals(AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON.getDatabaseValue(),
                ObjectSelect.query(Attendance.class).where(ExpressionFactory.matchDbExp(Attendance.ID_PK_COLUMN, 3L)).selectOne(context)
                        .getAttendanceType());

        // check for second student
        assertEquals(AttendanceType.UNMARKED.getDatabaseValue(),
                ObjectSelect.query(Attendance.class).where(ExpressionFactory.matchDbExp(Attendance.ID_PK_COLUMN, 10L)).selectOne(context)
                        .getAttendanceType());
        assertEquals(AttendanceType.DID_NOT_ATTEND_WITH_REASON.getDatabaseValue(),
                ObjectSelect.query(Attendance.class).where(ExpressionFactory.matchDbExp(Attendance.ID_PK_COLUMN, 11L)).selectOne(context)
                        .getAttendanceType());
        assertEquals(AttendanceType.PARTIAL.getDatabaseValue(),
                ObjectSelect.query(Attendance.class).where(ExpressionFactory.matchDbExp(Attendance.ID_PK_COLUMN, 12L)).selectOne(context)
                        .getAttendanceType());

        List<Integer> expectedTypes = new ArrayList<>();
        expectedTypes.add(1);                                   // AttendanceType.ATTENDED
        expectedTypes.add(2);                                   // AttendanceType.DID_NOT_ATTEND_WITH_REASON
        expectedTypes.add(3);                                   // AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON

        for (Attendance a : resultOfUpdStud){
            if (expectedTypes.contains(a.getAttendanceType())){
                expectedTypes.remove(a.getAttendanceType());
            }
        }

        assertEquals(0, expectedTypes.size());
    }
}
