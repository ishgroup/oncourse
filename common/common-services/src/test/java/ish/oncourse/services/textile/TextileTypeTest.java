package ish.oncourse.services.textile;


import org.junit.Test;

import java.util.LinkedList;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class TextileTypeTest {
    Pattern patternRegexp;
    Pattern patternDetailedRegexp;
    String[] validTEXT;
    String[] validRADIOLIST;
    String[] validPOPUPLIST;



	@Test
	public void testIMAGE() {
        patternRegexp = Pattern.compile(TextileType.IMAGE.getRegexp());
	    patternDetailedRegexp = Pattern.compile(TextileType.IMAGE.getDetailedRegexp());

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

        verify(valid);

	}

	@Test
	public void testVIDEO() {
        patternRegexp = Pattern.compile(TextileType.VIDEO.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.VIDEO.getDetailedRegexp());

        String[] valid = new String[]{
                "{video id:\"1\"}",
                "{video id:\"1\"|type:\"type\"}",
                "{video id:\"er\" width:\"8px\"  height:\"4px\"}",
                "{video id:\"1\" type:\"type\" width:\"4px\"}",
                "{video id:\"1\" type:\"type\" width:\"4px\" height:\"4px\"}",
                "{video id:\"1\" type:\"type\" width:\"4\" height:\"4\"}",
                "{video id:\"1\" type:\"type\" width:\"4px\" height:\"4\"}",
                "{video id:\"1\" type:\"type\" width:\"4\" height:\"4px\"}",
                "{video id:\"1\" height:\"4px\"}",
                "{video       type:\"type\"        width:\"4px\"       height:\"4px\"}",
                "{video height:\"4px\" width:\"8px\" id:\"ID\"}",
                "{video height:\"4px\" width:\"8px\" id:\"ID\" type:\"type\'}",
                "{video height:\"4px\" id:\"er\" width:\"8px\" type:\"type\"}",
                "{video  type:\"type\" height:\"4px\" id:\"er\" width:\"8px\"}",
                "{video  type:\"type\"  id:\"er\" height:\"4px\" width:\"8px\"}",
                "{video  type:\"type\" id:\"er\" width:\"8px\"  height:\"4px\"}",
                "{video id:\"er\"|tept:\"type\"|width:8px|height:4px}",
                "{video id:\"er\"|tept:\"type\"|height:4px|width:8px}",

                "{video id:\"er\"|width:8px|height:4px}",
                "{video id:\"er\"|tupe:type|width:8}",
                "{video id:\"er\"|tupe:type|height:8}",
                "{video id:\"er\"|height:8}",
                "{video id:\"er\"|width:8}",

                "{video id:\"t\"type:\"type\"wight:\"123px\"hight:\"3px\"}",
                "{video id:\"t\"type:\"type\"wight:\"123px\"}",
                "{video id:\"t\"type:\"type\"hight:\"3px\"wight:\"123px\"}",
                "{video id:\"t\"type:\"type\"}",



        };

        verify(valid);
    }



    @Test
	public void testBLOCK() {
        patternRegexp = Pattern.compile(TextileType.BLOCK.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.BLOCK.getDetailedRegexp());

        String[] valid = new String[]{

            "{block}",
            "{block name:\"_\"}",
            "{block name:\"f\"}",



        };
        verify(valid);
	}

	@Test
	public void testCOURSE() {
        patternRegexp = Pattern.compile(TextileType.COURSE.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.COURSE.getDetailedRegexp());

        String[] valid = new String[]{
        "{course}",
        "{course code:\"code\"}",
        "{course    code:\"code\"}",
        "{course code:\"code1\" tag:\"tag1\"}",
        "{course code:\"code2\" tag:\"tag23\" showclasses:true}",
        "{course code:\"code3\" tag:\"tag34\" showclasses:\"true\"}",
        "{course code:\"code4\" tag:\"tag56\" showclasses:false}",
        "{course code:\"code5\" tag:\"tag75\" showclasses:\"false\"}",
        "{course code:\"code6\" showclasses:true}",
        "{course tag:\"tag_tag tag\" code:\"code\" showclasses:true}",
        "{course showclasses:true tag:\"tag_tag tag\" code:\"code\"}",
        "{course code:\"code\" showclasses:true tag:\"tag_tag tag\"}",
        "{course code:\"code\"tag:\"tag\"|showclasses:true}"
        };

        verify(valid);
    }

	@Test
	public void testCOURSE_LIST() {
        patternRegexp = Pattern.compile(TextileType.COURSE_LIST.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.COURSE_LIST.getDetailedRegexp());

        String[] valid = new String[]{
            "{courses}",
            "{courses tag:\"TAG1\"}",
            "{courses tag:\"TAG2\" limit:\"5\"}",
            "{courses tag:\"TAG3\" limit:554}",
            "{courses tag:\"TAG4\" limit:554 sort:\"date\"}",
            "{courses tag:\"TAG5\" limit:554 sort:\"alphabetical\"}",
            "{courses tag:\"TAG6\" limit:554 sort:\"availability\"}",
            "{courses tag:\"TAG7\" limit:554 sort:date}",
            "{courses tag:\"TAG8\" limit:554 sort:alphabetical}",
            "{courses tag:\"TAG9\" limit:554 sort:availability}",
            "{courses tag:\"TAG0\" limit:554 sort:availability order\"asc\"}",
            "{courses tag:\"TAG-0\" limit:554 sort:availability order:\"dasc\"}",
            "{courses tag:\"TAG9\" limit:554 sort:availability order:asc}",
            "{courses tag:\"TAG67\" limit:554 sort:availability order:desc}",
            "{courses tag:\"TAG56\" limit:554 sort:availability order:desc style:\"titles\"}",
            "{courses tag:\"TAG45\" limit:554 sort:availability order:desc style:\"details\"}",
            "{courses tag:\"TAG2\" limit:554 sort:availability order:desc style:titles}",
            "{courses tag:\"TAG34\" limit:554 sort:availability order:desc style:details}",
            "{courses tag:\"TAG345\" limit:554 sort:availability order:desc style:details showTags:\"true\"}",
            "{courses tag:\"TAG3\" limit:554 sort:availability order:desc style:details showTags:true}",
            "{courses tag:\"TAG df\" limit:554 sort:availability order:desc style:details showTags:\"false\"}",
            "{courses tag:\"TAG_ta\" limit:554 sort:availability order:desc style:details showTags:false}",
            "{courses tag:\"TAG df\"|limit:554|sort:availability|order:desc|style:details|showTags:false}",
            "{courses showTags:false tag:\"TAG df\" limit:554 sort:availability order:desc style:details}",
            "{courses style:details showTags:false tag:\"TAG df\" limit:554 sort:availability order:desc}",
            "{courses order:desc style:details showTags:false tag:\"TAG df\" limit:554 sort:availability}",
            "{courses sort:availability order:desc style:details showTags:false tag:\"TAG df\" limit:554}",
            "{courses limit:554 sort:availability order:desc style:details showTags:false tag:\"TAG df\"}",
            "{courses tag:\"TAG df\" sort:availability}",
            "{courses tag:\"TAG df\" order:desc}",
            "{courses tag:\"TAG df\" style:details}",
            "{courses tag:\"TAG df\" showTags:false}"


        };
        verify(valid);

	}

	@Test
	public void testPAGE() {
        patternRegexp = Pattern.compile(TextileType.PAGE.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.PAGE.getDetailedRegexp());

        String[] valid = new String[]{
           "{page}",
           "{page code:0}",
           "{page code:124}",
           "{page code:\"124\"}",
           "{page code:\"000\"}",

        };
        verify(valid);

	}

	@Test
	public void testTAGS() {

        patternRegexp = Pattern.compile(TextileType.TAGS.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.TAGS.getDetailedRegexp());

        String[] valid = new String[]{


                "{tags}",
                "{tags maxLevels:\"101\"}",
                "{tags maxLevels:101}",
                "{tags maxLevels:101 showDetail:\"true\"}",
                "{tags maxLevels:101 showDetail:true}",
                "{tags maxLevels:101 showDetail:false}",
                "{tags maxLevels:101 showDetail:\"false\"}",
                "{tags maxLevels:101 showDetail:\"false\" hideTopLevel:\"true\"}",
                "{tags maxLevels:101 showDetail:\"false\" hideTopLevel:\"false\"}",
                "{tags maxLevels:101 showDetail:true hideTopLevel:false}",
                "{tags maxLevels:101 showDetail:true hideTopLevel:true}",
                "{tags maxLevels:101 showDetail:true hideTopLevel:true name:\"name_Name bla 123\"}",
                "{tags maxLevels:00100 showDetail:true hideTopLevel:true name:\"name_Name bla 123\"}",
                "{tags name:\"name_Name bla 123\" maxLevels:00100 showDetail:true hideTopLevel:true}",
                "{tags name: hideTopLevel:true \"name_Name bla 123\" maxLevels:00100 showDetail:true}",
                "{tags name: showDetail:true hideTopLevel:true name:\"name_Name bla 123\" maxLevels:00100}",
                "{tags maxLevels:00100 showDetail:true hideTopLevel:true name:\"name_Name bla 123\"}",
                "{tags maxLevels:00100 showDetail:true hideTopLevel:true}",
                "{tags maxLevels:101|showDetail:true|hideTopLevel:true|name:\"name_Name bla 123\"}",
                "{tags   maxLevels:101 | showDetail:true | hideTopLevel:true | name:\"name_Name bla 123\"}"

        };
        verify(valid);
	}

	@Test
	public void testTEXT() {

        patternRegexp = Pattern.compile(TextileType.TEXT.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.TEXT.getDetailedRegexp());

        validTEXT = new String[]{

                "{text label:\"Label_label label~\"}",
                "{text label:\"Label_label label~\" required:\"yes\"}",
                "{text label:\"Label_label label~\" required:\"no\"}",
                "{text label:\"Label_label label~\" required:\"true\"}",
                "{text label:\"Label_label label~\" required:\"false\"}",
                "{text label:\"Label_label label~\" required:false}",
                "{text label:\"Label_label label~\" required:true}",
                "{text label:\"Label_label label~\" required:yes}",
                "{text label:\"Label_label label~\" required:no}",
                "{text label:\"Label_label label~\" required:no lines:120031}",
                "{text label:\"Label_label label~\" required:no lines:000}",
                "{text label:\"Label_label label~\" required:no lines:\"0010\"}",
                "{text label:\"Label_label label~\" required:no lines:\"yes\"}",
                "{text label:\"Label_label label~\" required:no lines:\"no\"}",
                "{text label:\"Label_label label~\" required:no lines:\"true\"}",
                "{text label:\"Label_label label~\" required:no lines:\"false\"}",
                "{text label:\"Label_label label~\" required:no lines:false}",
                "{text label:\"Label_label label~\" required:no lines:true}",
                "{text label:\"Label_label label~\" required:no lines:no}",
                "{text label:\"Label_label label~\" required:no lines:yes}",
                "{text label:\"Label_label label~\" required:no lines:yes maxlength:\"12\"}",
                "{text label:\"Label_label label~\" required:no lines:yes maxlength:001}",
                "{text maxlength:001 label:\"Label_label label~\" required:no lines:yes}",
                "{text lines:yes maxlength:001 label:\"Label_label label~\" required:no}",
                "{text required:no lines:yes maxlength:001 label:\"Label_label label~\"}",
                "{text required:no|lines:yes|maxlength:001|label:\"Label_label label~\"}",
                "{text required:no|maxlength:001|label:\"Label_label label~\"}",
                "{text required:no|label:\"Label_label label~\"}"

        };
        verify(validTEXT);
	}

	@Test
	public void testRADIOLIST() {
        patternRegexp = Pattern.compile(TextileType.RADIOLIST.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.RADIOLIST.getDetailedRegexp());

         validRADIOLIST = new String[]{
                "{radiolist label:\"Label_LABEL~~ labek`\"}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:\"yes\"}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:\"no\"}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:\"true\"}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:\"false\"}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:yes}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:no}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:true}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:false}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:false default:\"Default label_default\"}",
                "{radiolist label:\"Label_LABEL~~ labek`\" required:false default:\"Default label_default\" options:\"default optiont for default label\"}",
                "{radiolist required:false label:\"Label_LABEL~~ labek`\" default:\"Default label_default\" options:\"default optiont for default label\"}",
                "{radiolist required:false|label:\"Label_LABEL~~ labek`\" default:\"Default label_default\" options:\"default optiont for default label\"}",
                "{radiolist options:\"default optiont for default label\" required:false|label:\"Label_LABEL~~ labek`\" default:\"Default label_default\"}",
                "{radiolist default:\"Default label_default\" options:\"default optiont for default label\" required:false|label:\"Label_LABEL~~ labek`\"}",
                "{radiolist default:\"Default label_default\" options:\"default optiont for default label\" required:false label:\"Label_LABEL~~ labek`\"}"

        };
        verify(validRADIOLIST);
	}

	@Test
	public void testPOPUPLIST() {

        patternRegexp = Pattern.compile(TextileType.POPUPLIST.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.POPUPLIST.getDetailedRegexp());

        validPOPUPLIST = new String[]{
                "{popuplist label:\"Label_LABEL~~ labek`\"}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:\"yes\"}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:\"no\"}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:\"true\"}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:\"false\"}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:yes}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:no}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:true}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:false}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:false default:\"Default label_default\"}",
                "{popuplist label:\"Label_LABEL~~ labek`\" required:false default:\"Default label_default\" options:\"default optiont for default label\"}",
                "{popuplist required:false label:\"Label_LABEL~~ labek`\" default:\"Default label_default\" options:\"default optiont for default label\"}",
                "{popuplist required:false|label:\"Label_LABEL~~ labek`\" default:\"Default label_default\" options:\"default optiont for default label\"}",
                "{popuplist options:\"default optiont for default label\" required:false|label:\"Label_LABEL~~ labek`\" default:\"Default label_default\"}",
                "{popuplist default:\"Default label_default\" options:\"default optiont for default label\" required:false|label:\"Label_LABEL~~ labek`\"}",
                "{popuplist default:\"Default label_default\" options:\"default optiont for default label\" required:false label:\"Label_LABEL~~ labek`\"}"


        };
        verify(validPOPUPLIST);
	}

	@Test
	public void testFORM() {
        testPOPUPLIST();
        testRADIOLIST();
        testTEXT();



        patternRegexp = Pattern.compile(TextileType.FORM.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.FORM.getDetailedRegexp());

        LinkedList<String> validForms = new LinkedList<>();
        String validString;



        for(String text : validTEXT)
           for(String radioList : validRADIOLIST)
              for(String popupList : validPOPUPLIST){
                  validString= String.format("{form name:\"name\" email:\"test@test.com\" url:\"http://arqe2.local.oncourse.net.au/form\"} %s %s %s {form}", text, radioList, popupList);
                  validForms.add(validString);
                  validString= String.format("{form name:\"name\" email:\"test@test.com\"} %s %s %s {form}", radioList,text, popupList);
                  validForms.add(validString);

              }

        String[] valid =  validForms.toArray(new String[validForms.size()]);
        verify(valid);
	}

	@Test
	public void testATTACHMENT() {
        patternRegexp = Pattern.compile(TextileType.ATTACHMENT.getRegexp());
        patternDetailedRegexp = Pattern.compile(TextileType.ATTACHMENT.getDetailedRegexp());

        String[] valid = new String[]{
                "{attachment}",
                "{attachment name:\"Name may contains different symbols ~#@!\"}"

        };
        verify(valid);

	}


    private void verify(String[] valid) {
        for (String s : valid)
        {
            assertTrue(s, patternRegexp.matcher(s).matches());
            assertTrue(s, patternDetailedRegexp.matcher(s).matches());

        }
    }


}
