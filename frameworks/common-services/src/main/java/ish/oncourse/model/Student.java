package ish.oncourse.model;

import java.util.Calendar;

import ish.oncourse.model.auto._Student;

public class Student extends _Student {
	/**
	 * @return the number of years since the contact's birth date.
	 */
	public Integer getYearsOfAge() {

		if (getBirthDate() == null) {
			return null;
		}

		Calendar ref = Calendar.getInstance();
		Calendar stu = Calendar.getInstance();
		stu.setTime(getBirthDate());

		int refMonth = ref.get(Calendar.MONTH);
		int refDay = ref.get(Calendar.DATE);
		int stuMonth = stu.get(Calendar.MONTH);
		int stuDay = stu.get(Calendar.DATE);

		int age = ref.get(Calendar.YEAR) - stu.get(Calendar.YEAR);
		if (refMonth > stuMonth || refMonth == stuMonth && refDay > stuDay) {
			age--;
		}
		return Integer.valueOf(age);

	}

}
