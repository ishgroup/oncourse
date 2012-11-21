package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionAddConcessionTest {
	@Test
	public void test() {
		Date date = new Date();
		ConcessionType concessionType = new ConcessionType();
		concessionType.setName("concessionType");
		concessionType.setHasExpiryDate(Boolean.TRUE);
		concessionType.setHasConcessionNumber(Boolean.TRUE);

		PurchaseModel model = mock(PurchaseModel.class);
		PurchaseController controller = mock(PurchaseController.class);
		when(controller.getModel()).thenReturn(model);


		Student student = mock(Student.class);
		when(student.getStudentConcessions()).thenReturn(Collections.EMPTY_LIST);

		StudentConcession studentConcession = mock(StudentConcession.class);
		when(studentConcession.getConcessionNumber()).thenReturn("ConcessionNumber");
		when(studentConcession.getExpiresOn()).thenReturn(date);
		when(studentConcession.getConcessionType()).thenReturn(concessionType);
		when(studentConcession.getStudent()).thenReturn(student);
		doReturn(studentConcession).when(model).localizeObject(studentConcession);

		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addConcession);
		parameter.setValue(studentConcession);

		ActionAddConcession actionAddConcession = new ActionAddConcession();
		actionAddConcession.setController(controller);
		actionAddConcession.setParameter(parameter);
		assertTrue(actionAddConcession.action());
		verify(model, times(1)).addConcession(studentConcession);
		verify(controller, times(1)).recalculateEnrolmentInvoiceLines();
		verify(controller, times(1)).recalculateEnrolmentInvoiceLines();
		verify(controller, times(1)).setState(PurchaseController.State.editCheckout);


		List<StudentConcession> concessions = getConcessions(studentConcession, date, concessionType, student);
		when(student.getStudentConcessions()).thenReturn(concessions);
		actionAddConcession = new ActionAddConcession();
		actionAddConcession.setController(controller);
		actionAddConcession.setParameter(parameter);
		actionAddConcession.parse();
		assertFalse(actionAddConcession.action());
	}

	private List<StudentConcession> getConcessions(StudentConcession currentConcession, Date date, ConcessionType concessionType, Student student) {
		ArrayList<StudentConcession> result = new ArrayList<StudentConcession>();
		result.add(currentConcession);

		StudentConcession studentConcession = mock(StudentConcession.class);
		when(studentConcession.getConcessionNumber()).thenReturn("ConcessionNumber");
		when(studentConcession.getExpiresOn()).thenReturn(date);
		when(studentConcession.getConcessionType()).thenReturn(concessionType);
		when(studentConcession.getStudent()).thenReturn(student);

		result.add(studentConcession);
		return result;
	}
}
