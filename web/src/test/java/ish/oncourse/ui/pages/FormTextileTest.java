package ish.oncourse.ui.pages;

import ish.oncourse.ui.services.TestModule;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.test.PageTester;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class FormTextileTest {

	@Test
	public void testPageLoaded() throws Exception {
		PageTester tester = new PageTester("ish.oncourse.ui", "", "src/main/webapp", TestModule.class);
		Document doc = tester.renderPage("FormTextilePage");
		Element el = doc.getElementById("textile");

		String responseResult = el.toString();
		
		assertTrue(responseResult.contains(FormTextilePage.FORM_NAME));
		assertTrue(responseResult.contains(FormTextilePage.FIELD_LABEL));
		
		tester.shutdown();
	}
	
	@Test
	public void testPageLoadedForJrda() throws Exception {
		PageTester tester = new PageTester("ish.oncourse.ui", "", "src/main/webapp", TestModule.class);
		Document doc = tester.renderPage("FormTextilePage");
		Element el = doc.getElementById("textileForJrda");

		String responseResult = el.toString();

		String name = "Mandurah Contact";
		assertTrue(responseResult.contains(name));
		
		
		// labels with two words
		assertTrue(responseResult.contains("First name"));
		assertTrue(responseResult.contains("Last Name"));
		assertTrue(responseResult.contains("Email Address"));
		assertTrue(responseResult.contains("Enquiry"));
		
		// test the html structure
		// see label before the text field "First name" into the form
		int start = responseResult.indexOf("First name");
		start = responseResult.lastIndexOf("<label", start);
		int end = responseResult.indexOf("</label>", start) + 8;
		String cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		// see the text field "First name" into the form after label
		start = responseResult.indexOf("<input", end);
		end = responseResult.indexOf("</input>", start);
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		assertTrue(cutResult.contains("First name"));
		assertTrue(cutResult.contains("maxlength=\"50\""));
		assertTrue(cutResult.contains("type=\"text\""));
		assertTrue(cutResult.contains("class=\"formField required\""));
		
		// see label before the textarea "Enquiry" into the form
		start = responseResult.indexOf("Enquiry");
		start = responseResult.lastIndexOf("<label", start);
		end = responseResult.indexOf("</label>", start) + 8;
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		
		// textarea for "Enquiry"
		assertTrue(responseResult.contains("<textarea"));
		
		start = responseResult.indexOf("<textarea", end);
		end = responseResult.indexOf("</textarea>", start);
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		assertTrue(cutResult.contains("Enquiry"));
		assertTrue(cutResult.contains("rows=\"5\""));
		assertTrue(cutResult.contains("class=\"formField \""));
		
		tester.shutdown();
	}
	
	@Test
	public void testPageLoadedForScc() throws Exception {
		PageTester tester = new PageTester("ish.oncourse.ui", "", "src/main/webapp", TestModule.class);
		Document doc = tester.renderPage("FormTextilePage");
		
		Element el = doc.getElementById("textileForScc");
		
		String name = "Course Proposal Form";
		
		String responseResult = el.toString();
		assertTrue(responseResult.contains(name));
		
		// from "About You." part
		assertTrue(responseResult.contains("First Name"));
		assertTrue(responseResult.contains("Last Name"));
		assertTrue(responseResult.contains("Contact Number"));
		assertTrue(responseResult.contains("Date of Birth"));
		
		// from "Generally About Your Course" part
		assertTrue(responseResult.contains("Course Name"));
		assertTrue(responseResult.contains("Venue Name"));
		
		// from "More Specifically About Your Course?" part
		//check radiolist more 
		int start = responseResult.indexOf("1. How many sessions do you propose for your course?");
		int end = responseResult.indexOf("2. Is your course proposed for the Day, Evening or Weekend?");
		String cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.contains("value=\"1\""));
		assertTrue(cutResult.contains("value=\"2\""));
		assertTrue(cutResult.contains("value=\"3\""));
		assertTrue(cutResult.contains("value=\"4\""));
		assertTrue(cutResult.contains("value=\"5\""));
		assertTrue(cutResult.contains("value=\"6\""));
		assertTrue(cutResult.contains("value=\"7\""));
		assertTrue(cutResult.contains("value=\"8\""));
		
		start = cutResult.indexOf("<input");
		end = cutResult.indexOf("<script", start);
		cutResult = cutResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		String[] inputs = cutResult.split("<input");
		for(String inp: inputs){
			if (inp.length() > 0){
				assertTrue(inp.contains("type=\"radio\""));
				assertTrue(inp.contains("class=\"formField \""));
				if(inp.contains("value=\"1\"")){
					assertTrue(inp.contains("_input_checked_radio"));
				}
			}
		}
		
		start = responseResult.indexOf("2. Is your course proposed for the Day, Evening or Weekend?");
		end = responseResult.indexOf("3. Please describe your course as you would like it advertised.");
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.contains("value=\"Day\""));
		assertTrue(cutResult.contains("value=\"Evening\""));
		assertTrue(cutResult.contains("value=\"Weekend\""));
		
		start = cutResult.indexOf("<input");
		end = cutResult.indexOf("<script", start);
		cutResult = cutResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		String[] inputs2 = cutResult.split("<input");
		for(String inp: inputs2){
			if (inp.length() > 0){
				assertTrue(inp.contains("type=\"radio\""));
				assertTrue(inp.contains("class=\"formField required\""));
				if(inp.contains("value=\"Day\"")){
					assertTrue(inp.contains("_input_checked_radio"));
				}
			}
		}

		assertTrue(responseResult.contains("Please describe your course as you would like it advertised."));
		assertTrue(responseResult.contains("Agenda"));
		start = responseResult.indexOf("Please describe your course as you would like it advertised.");
		end = responseResult.indexOf("Agenda");
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.contains("<textarea"));
		assertTrue(cutResult.contains("rows=\"20\""));
		assertTrue(cutResult.contains("class=\"formField required\""));
		
		// from "Agenda" part
		assertTrue(responseResult.contains("Learning Outcomes"));
		start = responseResult.indexOf("Agenda");
		end = responseResult.indexOf("Learning Outcomes");
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.contains("<textarea"));
		assertTrue(cutResult.contains("rows=\"20\""));
		assertTrue(cutResult.contains("class=\"formField required\""));
		
		// from "Learning Outcomes" part
		assertTrue(responseResult.contains("a. First Outcome"));
		assertTrue(responseResult.contains("b. Second Outcome"));
		assertTrue(responseResult.contains("c. Third Outcome"));
		assertTrue(responseResult.contains("d. Fourth Outcome"));
				
		tester.shutdown();
	}
	
	@Test
	public void testPageLoadedForTextilePageDjwarehouse() throws Exception {
		PageTester tester = new PageTester("ish.oncourse.ui", "", "src/main/webapp", TestModule.class);
		Document doc = tester.renderPage("FormTextilePage");
		
		Element el = doc.getElementById("textileForDjwarehouse");
		
		String name = "Class feedback";
		
		String responseResult = el.toString();
		assertTrue(responseResult.contains(name));
		
		// test popuplist
		
		int start = responseResult.indexOf("Which course did you attend?");
		start = responseResult.indexOf("<select", start);
		int end = responseResult.indexOf("</select>", start) + 9;
		String cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		
		// test attribute for select
		start = cutResult.indexOf("<select");
		end = cutResult.indexOf(">", start) + 1;
		String tempResult = cutResult.substring(start, end);
		
		String patternStr = "(name=\")(\\d+)(_Which course did you attend)(\\?_input\")";
		Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(tempResult);
		assertTrue(matcher.find());
		
		patternStr = "(id=\")(\\d+)(_Which course did you attend)(\\?_input\")";
		pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
		matcher = pattern.matcher(tempResult);
		assertTrue(matcher.find());
		
		assertTrue(cutResult.contains("class=\"formField required\""));
		
		// test tag <option>
		start = cutResult.indexOf("<option>");
		end = cutResult.indexOf("</select>", start);
		tempResult = cutResult.substring(start, end);
		
		assertTrue(tempResult.contains("<option>1 Day DJ Crash Course</option>"));
		assertTrue(tempResult.contains("<option>8 Week DJ Course</option>"));
		assertTrue(tempResult.contains("<option>4 Week Scratching Course</option>"));
		assertTrue(tempResult.contains("<option>Producing Pro DJ Mix</option>"));
		assertTrue(tempResult.contains("<option>4 Week Ableton Music Production</option>"));
		assertTrue(tempResult.contains("<option>4 Week Advanced Ableton Music Production</option>"));
		assertTrue(tempResult.contains("<option>Live Performance and DJ With Ableton</option>"));
		
		start = responseResult.indexOf("How did you find the pace of classes?");
		start = responseResult.indexOf("<select", start);
		end = responseResult.indexOf("</select>", start) + 9;
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		assertTrue(cutResult.contains("class=\"formField \""));
		
		// test tag <option>
		start = cutResult.indexOf("<option>");
		end = cutResult.indexOf("</select>", start);
		tempResult = cutResult.substring(start, end);
		
		assertTrue(tempResult.contains("<option>Good</option>"));
		assertTrue(tempResult.contains("<option>Fast</option>"));
		assertTrue(tempResult.contains("<option>Slow</option>"));
		
		// test radiolist
		start = responseResult.indexOf("Did the course cover enough material?");
		end = responseResult.indexOf("</script>");
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.contains("value=\"Yes\""));
		assertTrue(cutResult.contains("value=\"No\""));
		
		start = cutResult.indexOf("<input");
		end = cutResult.indexOf("<script", start);
		cutResult = cutResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		String[] inputs2 = cutResult.split("<input");
		for(String inp: inputs2){
			if (inp.length() > 0){
				assertTrue(inp.contains("type=\"radio\""));
				assertTrue(inp.contains("class=\"formField \""));
			}
		}
	}
	
	// test for two form on one page
	@Test
	public void testPageWithTwoFormLoaded() throws Exception {
		PageTester tester = new PageTester("ish.oncourse.ui", "", "src/main/webapp", TestModule.class);
		Document doc = tester.renderPage("FormTextilePageWithTwoForms");
		
		Element el = doc.getElementById("textile");
		String name = "Additional enrolment information";
		String nameForSecondForm = "Online Enquiry";
		
		String responseResult = el.toString();
		assertTrue(responseResult.contains(name));
		assertTrue(responseResult.contains(nameForSecondForm));
		
		assertTrue(responseResult.contains("Student name"));
		assertTrue(responseResult.contains("First Name"));
		
		// test the html structure
		// test the text field for first form 
		int start = responseResult.indexOf("Student name");
		start = responseResult.lastIndexOf("<label", start);
		int end = responseResult.indexOf("</label>", start) + 8;
		String cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		// see the text field "Student name" into the form after label
		start = responseResult.indexOf("<input", end);
		end = responseResult.indexOf("</input>", start);
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		assertTrue(cutResult.contains("Student name"));
		
		// test the text field for second form 
		start = responseResult.indexOf("First Name");
		start = responseResult.lastIndexOf("<label", start);
		end = responseResult.indexOf("</label>", start) + 8;
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		// see the text field "First name" into the form after label
		start = responseResult.indexOf("<input", end);
		assertTrue(start > 0);
		end = responseResult.indexOf("</input>", start);
		assertTrue(end > 0);
		cutResult = responseResult.substring(start, end);
		assertTrue(cutResult.length() > 0);
		assertTrue(cutResult.contains("First Name"));
		
	}
}
