package ish.oncourse.services.textile;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class TextileTypeTest {


	@Test
	public void testIMAGE() {
		Pattern patternRegexp = Pattern.compile(TextileType.IMAGE.getRegexp());
		Pattern patternDetailedRegexp = Pattern.compile(TextileType.IMAGE.getDetailedRegexp());

		String[] invalid = new String[]
				{
						"{image name:\"Untitled1\"|align:centre}",
						"{image name:\"Eco Living Centre\"|align:centre}",
						"{image name:\"Aussie Renewables\"|align:centre}",
						"{image:Sustainability Logo|align:centre}",
						"{image:Dancing}",
				};
		String[] valid = new String[]{
				"{image name:\"OnlineCourses\" alt:\"OnlineCourses\" width:\"204px\" height:\"275px\" link:\"http://www.ed2go.com/r-scc/\"}",
				"{image name:\"giftcertificate_banner\" alt:\"giftcertificate_banner\" width:\"590px\" height:\"336px\" link:\"/giftcertificates\"}",
				"{image name:\"abletonbanner\" alt:\"abletonbanner\" width:\"590px\" height:\"336px\" link:\"/course/8fal\"}",
				"{image name:\"100COMP_BANNER\" alt:\"100COMP_BANNER\" width:\"590px\" height:\"336px\" link:\"/competition\"}",
				"{image name:\"Jennifer234_67\" alt:\"Jennifer234_67\" width:\"733px\" height:\"412px\" caption:\"Improve your game with some group coaching.\" link:\"/courses/sport/tennis/adults+program\"}",
				"{image name:\"Jennifer234_62\" alt:\"Jennifer234_62\" width:\"733px\" height:\"412px\" caption:\" We cater for kids of all ages.\" link:\"/courses/sport/tennis/kids+program\"}",
				"{image name:\"Jennifer234_55\" alt:\"Jennifer234_55\" width:\"733px\" height:\"412px\" caption:\"Make sure you book the kids into our holiday program\" link:\"/courses/sport/tennis/Holiday+Program\"}",
				"{image name:\"Jed234_39\" alt:\"Jed234_39\" width:\"733px\" height:\"412px\" caption:\"Improve your game by joining our friendly coach led competition.\" link:\"/courses/sport/tennis/adults+program\"}",
				"{image name:\"Jed234_30\" alt:\"Jed234_30\" width:\"733px\" height:\"412px\" caption:\"Yes we have individual coaching available. Send us an enquiry \" link:\"/page/#\"}",
				"{image name:\"Jed234_29\" alt:\"Jed234_29\" width:\"733px\" height:\"412px\" caption:\"Our staff are fully accredited by Tennis Australia and have all appropriate screening checks to work with children:\"/courses/sport/tennis/kids+program\"}",
				"{image name:\"juanb_w\" alt:\"juanb_w\" width:\"140px\" height:\"140px\"}",
				"{image name:\"alisson sandi 200px\" alt:\"alisson sandi\" width:\"140px\" height:\"140px\"}",
				"{image name:\"Audrey2_email\" alt:\"Audrey2_email\" width:\"140px\" height:\"140px\"}",
				"{image name:\"TaniaPhotoWeb\" alt:\"TaniaPhotoWeb\" width:\"140px\" height:\"140px\"}",
				"{image name:\"Deb portrait JRDA\" alt:\"Deb portrait JRDA\" width:\"140px\" height:\"140px\"}",
				"{image name:\"Inge_Van_Winkel_Web\" alt:\"Inge_Van_Winkel_Web\" width:\"140px\" height:\"140px\"}",
				"{image name:\"Patricia_Profile_Web\" alt:\"Patricia_Profile_Web\" width:\"140px\" height:\"140px\"}",
				"{image name:\"ManBook\" align:right}",
				"{image name:\"Sze\" align:right}",
				"{image name:\"Mark Fogarty\"}",
				"{image name:\"Mark Gjerek\"}",
				"{image name:\"Alistair Duncan\"|align:left|width:100}",
				"{image name:\"Brassel\"align:right}",
				"{image name:\"Mary Hendricks\"|align:left|width:100}",
				"{image name:\"Shepherd\"|align\"right\"}",
				"{image name:\"GiftCertificateFront\" alt:\"GiftCertificateFront\" width:\"640px\" height:\"298px\"}",
				"{image name:\"GiftCertificateBack\" alt:\"GiftCertificateBack\" width:\"640px\" height:\"296px\"}",
				"{image name:\"SCCConcert\" alt:\"SCCConcert\" width:\"720px\" height:\"540px\"}",
				"{image name:\"08_2_Cabaret_FaheyKilleen\" alt:\"05_3_Prime of Miss Jean Brodie2\" width:\"255px\" height:\"170px\" align:\"left\"}",
				"{image name:\"Martin\" align:\"right\" width:\"100px\" height:\"134px\"}",
				"{image name:\"FinalTennisLogo_RGB\" alt:\"FinalTennisLogo_RGB\" width:\"250px\" height:\"167px\"}",
				"{image name:\"Concert09\" alt:\"Concert09\" width:\"675px\" height:\"468px\"}",
				"{image name:\"4 Para and Una by Su 20090919\" alt:\"4 Para and Una by Su 20090919\" width:\"200px\" height:\"180px\"}",
				"{image name:\"Deb portrait JRDA\" alt:\"Deb portrait JRDA\" width:\"150px\" height:\"190px\"}",
				"{image name:\"2010 Photo Exhibition Invite Web\" alt:\"2010 Photo Exhibition Invite Web\" width:\"420px\" height:\"595px\"}",
				"{image name:\"Invitation 2\" alt:\"Invitation 2\" width:\"420px\" height:\"595px\"}",
				"{image name:\"PJWolf LaunchfinalWeb\" alt:\"Staff - Sean O'Riordan\" title:\"\" caption:\"\" width:\"170px\" height:\"217px\" align:\"left\"}",
				"{image name:\"Thumb DRichards\" alt:\"Thumb DRichards\" title:\"\" caption:\"\" width:\"250px\" height:\"170px\" align:\"left\"}",
				"{image name:\"thumbJessMcAlister\" alt:\"thumbJessMcAlister\" title:\"\" caption:\"\" width:\"166px\" height:\"250px\" align:\"left\"}",
				"{image name:\"Cover Term 2 2011.\" width:\"150px\"}",
				"{image name:\"419\" alt:\"starlight\" width:\"670px\"}",
				"{image:Ghoreyshi|align:left|width:150}",
				"{image:Drew|align:left|width:150}",
				"{image name:\"GYBlogoSummer\"|align:centre|width:150}",
				"{image:AusIndustryStackedJPG|align:left|width:10}",
		};
		for (String s : valid)
		{
			assertTrue(s, patternRegexp.matcher(s).matches());
			assertTrue(s, patternDetailedRegexp.matcher(s).matches());
		}
	}

	@Test
	public void testVIDEO() {
	}

	@Test
	public void testBLOCK() {
	}

	@Test
	public void testCOURSE() {
	}

	@Test
	public void testCOURSE_LIST() {
	}

	@Test
	public void testPAGE() {
	}

	@Test
	public void testTAGS() {
	}

	@Test
	public void testTEXT() {
	}

	@Test
	public void testRADIOLIST() {
	}

	@Test
	public void testPOPUPLIST() {
	}

	@Test
	public void testFORM() {
	}

	@Test
	public void testATTACHMENT() {
	}
}
