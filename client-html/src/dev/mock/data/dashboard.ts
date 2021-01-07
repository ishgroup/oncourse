import { Category, CategoryItem, StatisticData } from "@api/model";

export function mockDashboard() {
  this.getStatistic = (): StatisticData => ({
      enrolmentsChartLine: [2, 3, 4, 8, 2, 2],
      revenueChartLine: [1, 5, 2, 5, 4, 3],
      studentsCount: 232,
      moneyCount: 500,
      latestEnrolments: [
        {
          title: "Spanish 103 (Level 2)",
          info: "2019-03-18T10:00:25.764Z",
          link: "/swing/enrolments/196234"
        },
        {
          title: "Spanish 102 (Level 1)",
          info: "2019-03-18T10:00:25.764Z",
          link: "/swing/enrolments/196235"
        },
        {
          title: "Children's book illustration",
          info: "2019-03-18T10:00:25.764Z",
          link: "/swing/enrolments/196236"
        },
        {
          title: "University preparation",
          info: "2019-03-18T10:00:25.764Z",
          link: "/swing/enrolments/196237"
        }
      ],
      latestWaitingLists: [
        {
          title: "French 101 (Beginners)",
          info: "F101 220",
          link: "/waitingList?search=course.id=1"
        },
        {
          title: "Changing your thinking",
          info: "CYTH 144",
          link: "/waitingList?search=course.id=2"
        },
        {
          title: "Archeology & Artifacts Works",
          info: "AWNM 125",
          link: "/waitingList?search=course.id=3"
        }
      ],
      openedClasses: 42,
      inDevelopmentClasses: 4,
      cancelledClasses: 34,
      completedClasses: 128,
      commencedClasses: 95
    });

  this.getDashboardFeeds = () => "{  \"title\": \"onCourse dashboard feed\",  \"entry\": [      {      \"id\": \"f5591b91da2591e3034c6e6c976359e3\",      \"title\": \"Christmas\",      \"published\": \"2020-12-15T00:00:00+11:00\",      \"event\": \"\",      \"duration\": \"\",      \"content\": \"<p>Happy holidays from everyone here at ish. Its been a wild year but we’ve enjoyed helping many of you transition to online learning.</p> <p>If the Christmas colours are scrooging you out, you can switch back to your regular programming with the icon to the top right of this dashboard. Your mood can be as dark or festive as you like. Once you turn your back on Christmas, there is no coming back!</p> <p>We’ll be working through the holidays for the most part, but our usual support will be delayed for non-urgent things until the 11 January.</p>\",      \"excerpt\": \"Happy holidays from everyone here at ish. Its been a wild year but we’ve enjoyed helping many of you transition to online learning. If the Christmas colours are scrooging you out, you can switch back to your regular programming with the icon to the top right of this dashboard. Your mood can be as dark or festive as you like. Once you turn your back on Christmas, there is no coming back! We’ll be working through the holidays for the most part, but our usual support will be delayed for non-urgent things until the 11 January.\",      \"tags\": []    },      {      \"id\": \"3eab5135e861f62a03f9acc2eb1eeb16\",      \"title\": \"Collecting better student info webinar\",      \"published\": \"2020-12-04T00:00:00+11:00\",      \"event\": \"2020-12-11T12:00:00+11:00\",      \"duration\": \"30\",      \"content\": \"<p>Our next webinar will focus on data collection rules in onCourse and how you can collect custom information about your students and their needs.</p> <p>Use this information for VET compliance, to learn more about your students or better understand how to deliver your courses.</p> <p><a href=\\\"https://meet.google.com/tvk-zimy-rmd\\\">Join us at this link</a>, 5 minutes before the session starts.</p>\",      \"excerpt\": \"Our next webinar will focus on data collection rules in onCourse and how you can collect custom information about your students and their needs. Use this information for VET compliance, to learn more about your students or better understand how to deliver your courses. Join us at this link, 5 minutes before the session starts.\",      \"tags\": []    },      {      \"id\": \"d7389239f020528ca9f9c0d699231019\",      \"title\": \"onCourse documents and permissions webinar\",      \"published\": \"2020-11-19T00:00:00+11:00\",      \"event\": \"2020-12-04T12:00:00+11:00\",      \"duration\": \"30\",      \"content\": \"<p>Our next webinar will focus on the document management portion of onCourse, including:</p> <ul> <li>adding new documents</li> <li>how documents can be used</li> <li>explaining document permissions &amp; sharing</li> <li>document audit history</li> <li>handling deleted documents</li> </ul> <p><a href=\\\"https://meet.google.com/tvk-zimy-rmd\\\">Join us at this link</a>, 5 minutes before the session starts.</p>\",      \"excerpt\": \"Our next webinar will focus on the document management portion of onCourse, including: adding new documents how documents can be used explaining document permissions &amp;amp; sharing document audit history handling deleted documents Join us at this link, 5 minutes before the session starts.\",      \"tags\": []    },      {      \"id\": \"47c2a53f8f7e7839c3f5d830fc40569a\",      \"title\": \"onCourse course relationships webinar\",      \"published\": \"2020-11-19T00:00:00+11:00\",      \"event\": \"2020-11-27T12:00:00+11:00\",      \"duration\": \"30\",      \"content\": \"<p>Course relationships are a major new feature in onCourse 53. They will allow you to create powerful new packaging or ordering of courses and products including:</p> <ul> <li>pre-requisites</li> <li>co-requisites</li> <li>rolling intakes</li> <li>audition fees</li> <li>special pricing bundles</li> <li>course progression</li> </ul> <p>Join us on this webinar for a group discussion about how you could use this feature to market courses in new ways, save data entry effort and offer programmes of study.</p> <p><a href=\\\"https://meet.google.com/tvk-zimy-rmd\\\">Join us at this link</a>, 5 minutes before the session starts.</p>\",      \"excerpt\": \"Course relationships are a major new feature in onCourse 53. They will allow you to create powerful new packaging or ordering of courses and products including: pre-requisites co-requisites rolling intakes audition fees special pricing bundles course progression Join us on this webinar for a group discussion about how you could use this feature to market courses in new ways, save data entry effort and offer programmes of study. Join us at this link, 5 minutes before the session starts.\",      \"tags\": []    },      {      \"id\": \"55d1946e70324e868a357edf43b305f0\",      \"title\": \"ish support - squish returns\",      \"published\": \"2020-11-18T00:00:00+11:00\",      \"event\": \"\",      \"duration\": \"\",      \"content\": \"<p>Squish is back!</p> <p>The upgrade of our task tracking server wasn’t smooth, but Atlassian have resolved the issues and you can <a href=\\\"https://ish-group.atlassian.net\\\">now log in again.</a></p> <p>You’ll need to reset your password when you first try to log in, since they were not able to be migrated in the move. Apologies for the inconvenience over the last week.</p>\",      \"excerpt\": \"Squish is back! The upgrade of our task tracking server wasn’t smooth, but Atlassian have resolved the issues and you can now log in again. You’ll need to reset your password when you first try to log in, since they were not able to be migrated in the move. Apologies for the inconvenience over the last week.\",      \"tags\": []    },      {      \"id\": \"51b554f722f6d4614cb9a5111d9195d2\",      \"title\": \"Important - rich text changes\",      \"published\": \"2020-10-27T00:00:00+11:00\",      \"event\": \"\",      \"duration\": \"\",      \"content\": \"<p>Rich text markup in onCourse is changing.</p> <p>Last week we rolled out some changes to the website #editor that make it easier for you to customise your website content by offering you a choice between three types of markup; legacy, rich text and advanced (html). Rich Text mode offers a very easy-to-use WYSIWYG mode that should help users enter their content in a faster and easier manner, while legacy retains the old behaviour. HTML mode is recommended for advanced users only who wish to use markup in the description fields directly.</p> <p>Right now we’ve only upgraded Sites with the new feature. You can see the changes by opening a <a href=\\\"/site\\\">Sites record</a> and clicking any of the description fields.</p> <p><em>IMPORTANT</em> - If you change the type from ‘legacy’ to ‘rich text’ you’ll need to update your content so that it uses the correct markup. onCourse will not convert your content for you. For example, in legacy mode headings are created with “h2. “ and with rich text mode they are created with “## “</p> <p>If using a WYSIWYG editor sounds appealing to you then set the mode to ‘rich text’ and convert your content to the new markdown syntax. If you’re unfamiliar with markdown you can use the WYSIWYG editor to help with this and simply edit your content using the formatting tools. We have a <a href=\\\"https://www.ish.com.au/onCourse/doc/web/#richText-md\\\">handy guide</a> in our manual you can reference at any time.</p>\",      \"excerpt\": \"Rich text markup in onCourse is changing. Last week we rolled out some changes to the website #editor that make it easier for you to customise your website content by offering you a choice between three types of markup; legacy, rich text and advanced (html). Rich Text mode offers a very easy-to-use WYSIWYG mode that should help users enter their content in a faster and easier manner, while legacy retains the old behaviour. HTML mode is recommended for advanced users only who wish to use markup in the description fields directly. Right now we’ve only upgraded Sites with the new feature. You can see the changes by opening a Sites record and clicking any of the description fields. IMPORTANT - If you change the type from ‘legacy’ to ‘rich text’ you’ll need to update your content so that it uses the correct markup. onCourse will not convert your content for you. For example, in legacy mode headings are created with “h2. “ and with rich text mode they are created with “## “ If using a WYSIWYG editor sounds appealing to you then set the mode to ‘rich text’ and convert your content to the new markdown syntax. If you’re unfamiliar with markdown you can use the WYSIWYG editor to help with this and simply edit your content using the formatting tools. We have a handy guide in our manual you can reference at any time.\",      \"tags\": []    },      {      \"id\": \"02ec72e20bad93d1d195b8f6a6b5b5a9\",      \"title\": \"Where is the Training Plan?\",      \"published\": \"2020-10-26T00:00:00+11:00\",      \"event\": \"\",      \"duration\": \"\",      \"content\": \"<p>If you’re an RTO, you’ll want to know that we’ve moved the Training Plan within Class records from the Attendance section to the VET section. Not only that, we’ve also implemented into the date column the ability to check/uncheck all records, just like you can do with the outcome rows.</p> <p>We hope this will help those of you who work within this area of onCourse each day to organise your classes faster and with greater ease.</p>\",      \"excerpt\": \"If you’re an RTO, you’ll want to know that we’ve moved the Training Plan within Class records from the Attendance section to the VET section. Not only that, we’ve also implemented into the date column the ability to check/uncheck all records, just like you can do with the outcome rows. We hope this will help those of you who work within this area of onCourse each day to organise your classes faster and with greater ease.\",      \"tags\": []    },      {      \"id\": \"acc873fbec51389065b92c550846effb\",      \"title\": \"WYSIWYG website editor\",      \"published\": \"2020-10-19T00:00:00+11:00\",      \"event\": \"\",      \"duration\": \"\",      \"content\": \"<p>The website #editor is now even easier to use. We’ve added a new preview mode for pages and blocks in which you can style text with simple menu options.</p> <p>By default all your pages will be in “legacy” mode so as to not disrupt any existing styling, but we encourage you to switch to the new “rich text” mode which is built on a markdown editor. For advanced users there is also an html mode which ensures the tools don’t get in the way of the html you are crafting.</p> <p>We believe this new feature will make it much easier for you to style up your website just the way you want.</p> <p>We also plan on rolling these changes out in to onCourse as well, so you’ll eventually be able to update web descriptions using a selected markup. As always, we’d love to hear any feedback you have on this as we are working on this right now. You can <a href=\\\"https://github.com/ishgroup/onCourse-roadmap/issues/10\\\">do so on our roadmap.</a></p>\",      \"excerpt\": \"The website #editor is now even easier to use. We’ve added a new preview mode for pages and blocks in which you can style text with simple menu options. By default all your pages will be in “legacy” mode so as to not disrupt any existing styling, but we encourage you to switch to the new “rich text” mode which is built on a markdown editor. For advanced users there is also an html mode which ensures the tools don’t get in the way of the html you are crafting. We believe this new feature will make it much easier for you to style up your website just the way you want. We also plan on rolling these changes out in to onCourse as well, so you’ll eventually be able to update web descriptions using a selected markup. As always, we’d love to hear any feedback you have on this as we are working on this right now. You can do so on our roadmap.\",      \"tags\": []    },      {      \"id\": \"5764211e73f999ceecc403e2dcb6534b\",      \"title\": \"Terms and conditions website changes\",      \"published\": \"2020-09-20T00:00:00+10:00\",      \"event\": \"\",      \"duration\": \"\",      \"content\": \"<p>Your onCourse website checkout process is changing slightly this week. In August we upgraded the credit card panel to a more modern implementation, which then required that the Terms &amp; Conditions checkbox has to be ticked before the credit card is entered.</p> <p>To avoid confusion, we’ve now moved the T&amp;C checkbox higher up the page and hidden the payment panel until the user has ticked the checkbox. It cannot then be unticked.</p> <p><a href=\\\"https://invis.io/WBYTN8RU96M#/432174420_Accept_T-C_preview\\\">Take a look at at 10 second video of the new payment process.</a></p>\",      \"excerpt\": \"Your onCourse website checkout process is changing slightly this week. In August we upgraded the credit card panel to a more modern implementation, which then required that the Terms &amp;amp; Conditions checkbox has to be ticked before the credit card is entered. To avoid confusion, we’ve now moved the T&amp;amp;C checkbox higher up the page and hidden the payment panel until the user has ticked the checkbox. It cannot then be unticked. Take a look at at 10 second video of the new payment process.\",      \"tags\": []    },      {      \"id\": \"33e0cfdcbfbaef15d0fdbc140d7b34b1\",      \"title\": \"Portal Roadmap\",      \"published\": \"2020-09-17T00:00:00+10:00\",      \"event\": \"\",      \"duration\": \"\",      \"content\": \"<p>We are excited to launch our new skillsOnCourse portal project. The existing portal is the third generation and its time for a new iteration with exciting new features. Some highlights could include:</p> <ul> <li>More LMS features around assessment submissions</li> <li>CRICOS features</li> <li>Native phone apps</li> <li>CPD tracking</li> <li>Advertising</li> <li>Much much more…</li> </ul> <p>Please head over to our <a href=\\\"https://github.com/ishgroup/onCourse-roadmap/projects/2\\\">public tracker</a> where we’ve outlined some key features. Please vote for features with the emoji icon, make comments to let us know how you want to use the feature and even create new feature ideas.</p> <p>You need to tell us about the way you want to use the portal to get what you need. Our public roadmap is a step towards our goal of open sourcing onCourse and making our development processes more collaborative and open. But this only works if you get involved. Don’t wait for other people to drive features in the direction they want.</p> <p>Every user of onCourse is welcome to contribute. We talk to managers all the time, and this is your opportunity to shape the tools you use every day regardless of your job description. Ask your tutors to contribute too!</p>\",      \"excerpt\": \"We are excited to launch our new skillsOnCourse portal project. The existing portal is the third generation and its time for a new iteration with exciting new features. Some highlights could include: More LMS features around assessment submissions CRICOS features Native phone apps CPD tracking Advertising Much much more… Please head over to our public tracker where we’ve outlined some key features. Please vote for features with the emoji icon, make comments to let us know how you want to use the feature and even create new feature ideas. You need to tell us about the way you want to use the portal to get what you need. Our public roadmap is a step towards our goal of open sourcing onCourse and making our development processes more collaborative and open. But this only works if you get involved. Don’t wait for other people to drive features in the direction they want. Every user of onCourse is welcome to contribute. We talk to managers all the time, and this is your opportunity to shape the tools you use every day regardless of your job description. Ask your tutors to contribute too!\",      \"tags\": []    }    ]}";

  this.getCategories = () => {
    const categories: CategoryItem[] = Object.keys(Category).map((c: Category) => ({
      url: "/",
      category: c,
      favorite: false
    }));

    return { categories, upgradePlanLink: "https://www.ish.com.au/oncourse/signup?securityKey=123456" };
  };

  this.getDashboardSearchResult = () => [
    {
      "entity": "CourseClass",
      "items": [
        {
          "id": 2349,
          "name": "VET tester vtest-1"
        },
        {
          "id": 2348,
          "name": "Art Prize Test (Kids) ART001-1"
        },
        {
          "id": 2296,
          "name": "Animal Welfare Officer Skill Set SKATEST-5"
        },
        {
          "id": 2285,
          "name": "Pottery Class Test POT001-8"
        },
        {
          "id": 2284,
          "name": "Pottery Class Test POT001-7"
        },
        {
          "id": 2283,
          "name": "Pottery Class Test POT001-6"
        },
        {
          "id": 2282,
          "name": "Pottery Class Test POT001-5"
        },
        {
          "id": 2281,
          "name": "Pottery Class Test POT001-4"
        },
        {
          "id": 2280,
          "name": "Pottery Class Test POT001-3"
        },
        {
          "id": 2279,
          "name": "Pottery Class Test POT001-2"
        },
        {
          "id": 2278,
          "name": "Pottery Class Test POT001-1"
        },
        {
          "id": 2205,
          "name": "Test Course for On-line Enrolment Simulation TEST-21"
        },
        {
          "id": 2130,
          "name": "Test Course for On-line Enrolment Simulation TEST-20"
        },
        {
          "id": 2122,
          "name": "Animal Welfare Officer Skill Set SKATEST-3"
        },
        {
          "id": 2107,
          "name": "Module Testing MTST-2"
        },
        {
          "id": 2062,
          "name": "Test Course for On-line Enrolment Simulation TEST-14"
        },
        {
          "id": 2049,
          "name": "Test Course for On-line Enrolment Simulation TEST-16"
        },
        {
          "id": 1995,
          "name": "Test Course for On-line Enrolment Simulation TEST-13"
        },
        {
          "id": 1989,
          "name": "Test Course for On-line Enrolment Simulation TEST-11"
        },
        {
          "id": 1986,
          "name": "Test Course for On-line Enrolment Simulation TEST-12"
        },
        {
          "id": 1921,
          "name": "Animal Welfare Officer Skill Set SKATEST-4"
        },
        {
          "id": 1903,
          "name": "Test Course for On-line Enrolment Simulation TEST-17"
        },
        {
          "id": 1857,
          "name": "Test Course for On-line Enrolment Simulation TEST-19"
        },
        {
          "id": 1821,
          "name": "Test Course for On-line Enrolment Simulation TEST-15"
        },
        {
          "id": 1793,
          "name": "Test Course for On-line Enrolment Simulation TEST-18"
        },
        {
          "id": 1764,
          "name": "Animal Welfare Officer Skill Set SKATEST-2"
        },
        {
          "id": 1620,
          "name": "Animal Welfare Officer Skill Set SKATEST-1"
        },
        {
          "id": 1460,
          "name": "Module Testing MTST-1"
        },
        {
          "id": 1007,
          "name": "certificate in software testing sss-1"
        },
        {
          "id": 487,
          "name": "Test Course for On-line Enrolment Simulation TEST-08"
        },
        {
          "id": 476,
          "name": "Test Course for On-line Enrolment Simulation TEST-06"
        },
        {
          "id": 463,
          "name": "Test Course for On-line Enrolment Simulation TEST-07"
        },
        {
          "id": 460,
          "name": "Test Course for On-line Enrolment Simulation TEST-09"
        },
        {
          "id": 440,
          "name": "Test Course for On-line Enrolment Simulation TEST-10"
        },
        {
          "id": 354,
          "name": "Test Course for On-line Enrolment Simulation TEST-04"
        },
        {
          "id": 335,
          "name": "Test Course for On-line Enrolment Simulation TEST-05"
        },
        {
          "id": 228,
          "name": "Test Course for On-line Enrolment Simulation TEST-03"
        },
        {
          "id": 224,
          "name": "Test Course for On-line Enrolment Simulation TEST-02"
        },
        {
          "id": 221,
          "name": "Test Course for On-line Enrolment Simulation TEST-01"
        }
      ]
    },
    {
      "entity": "Contact",
      "items": [
        {
          "id": 3330,
          "name": "Test TEst123 "
        },
        {
          "id": 3329,
          "name": "Test "
        },
        {
          "id": 3327,
          "name": "My Test "
        },
        {
          "id": 3310,
          "name": "payer test #8157"
        },
        {
          "id": 3278,
          "name": "Credit Card Test #8147"
        },
        {
          "id": 3276,
          "name": "James Testing #8145"
        },
        {
          "id": 3265,
          "name": "Jim Tester #8134"
        },
        {
          "id": 3258,
          "name": "test #8127"
        },
        {
          "id": 3257,
          "name": "Test #8126"
        },
        {
          "id": 3252,
          "name": "Tony Tester #8120"
        },
        {
          "id": 3249,
          "name": "jim tester #8117"
        },
        {
          "id": 3212,
          "name": "Jimmy Testing #8081"
        },
        {
          "id": 3211,
          "name": "Jimmy Tester #8080"
        },
        {
          "id": 3196,
          "name": "Joe Tester #8065"
        },
        {
          "id": 3179,
          "name": "Tester Test #8049"
        },
        {
          "id": 3168,
          "name": "Natalli Test #8039"
        },
        {
          "id": 3167,
          "name": "Natallia Test #8038"
        },
        {
          "id": 3065,
          "name": "Megan Test mobile #6916"
        },
        {
          "id": 3064,
          "name": "Megan Test test #6915"
        },
        {
          "id": 3060,
          "name": "temp c test #6911"
        },
        {
          "id": 3041,
          "name": "Megan Test #6883"
        },
        {
          "id": 2927,
          "name": "Test Company Pty Ltd "
        },
        {
          "id": 2919,
          "name": "test #6768"
        },
        {
          "id": 2905,
          "name": "Test "
        },
        {
          "id": 2897,
          "name": "test "
        },
        {
          "id": 2891,
          "name": "test #6749"
        },
        {
          "id": 2803,
          "name": "bob test #6638"
        },
        {
          "id": 2549,
          "name": "tom test #6384"
        },
        {
          "id": 2548,
          "name": "james test #6382"
        },
        {
          "id": 2547,
          "name": "john test #6383"
        },
        {
          "id": 1842,
          "name": "new test #5697"
        },
        {
          "id": 1446,
          "name": "Discount Test #5298"
        },
        {
          "id": 1404,
          "name": "testing test #5259"
        },
        {
          "id": 1049,
          "name": "tesdfsdfsf test #4883"
        },
        {
          "id": 903,
          "name": "Test76 #4738"
        },
        {
          "id": 902,
          "name": "Test75 #4737"
        },
        {
          "id": 102,
          "name": "test #5"
        }
      ]
    },
    {
      "entity": "Course",
      "items": [
        {
          "id": 840,
          "name": "Art Prize Test (Kids) ART001"
        },
        {
          "id": 822,
          "name": "Pottery Class Test POT001"
        },
        {
          "id": 813,
          "name": "Course Name Test 555666"
        },
        {
          "id": 680,
          "name": "Animal Welfare Officer Skill Set SKATEST"
        },
        {
          "id": 541,
          "name": "VET tester vtest"
        },
        {
          "id": 480,
          "name": "Module Testing MTST"
        },
        {
          "id": 462,
          "name": "certificate in software testing sss"
        },
        {
          "id": 218,
          "name": "Test Course for On-line Enrolment Simulation TEST"
        }
      ]
    },
    {
      "entity": "Enrolment",
      "items": [
        {
          "id": 3873,
          "name": "Discount Test #5298 VETC-05"
        },
        {
          "id": 3866,
          "name": "Natalli Test #8039 VETC-01"
        },
        {
          "id": 3865,
          "name": "john test #6383 TAGS-25"
        },
        {
          "id": 3862,
          "name": "james test #6382 CERT-08"
        },
        {
          "id": 3860,
          "name": "payer test #8157 ENRL-14"
        },
        {
          "id": 3857,
          "name": "payer test #8157 TAGS-25"
        },
        {
          "id": 3852,
          "name": "john test #6383 CERT-01"
        },
        {
          "id": 3851,
          "name": "Jimmy Tester #8080 CERT-01"
        },
        {
          "id": 3850,
          "name": "Natallia Test #8038 CERT-02"
        },
        {
          "id": 3849,
          "name": "tom test #6384 CERT-02"
        },
        {
          "id": 3848,
          "name": "bob test #6638 CERT-07"
        },
        {
          "id": 3847,
          "name": "Natalli Test #8039 CERT-07"
        },
        {
          "id": 3846,
          "name": "james test #6382 CERT-02"
        },
        {
          "id": 3845,
          "name": "Megan Test test #6915 CERT-02"
        },
        {
          "id": 3784,
          "name": "Credit Card Test #8147 BJ1-11"
        },
        {
          "id": 3780,
          "name": "Credit Card Test #8147 act-act26"
        },
        {
          "id": 3779,
          "name": "Credit Card Test #8147 ThinkIn-4"
        },
        {
          "id": 3778,
          "name": "Credit Card Test #8147 CHC2-21"
        },
        {
          "id": 3767,
          "name": "Jim Tester #8134 BUDG-42"
        },
        {
          "id": 3766,
          "name": "Jim Tester #8134 CHC2-24"
        },
        {
          "id": 3758,
          "name": "Test #8126 LPWJ-7"
        },
        {
          "id": 3731,
          "name": "Tony Tester #8120 POT001-7"
        },
        {
          "id": 3608,
          "name": "Joe Tester #8065 CAT0001-3"
        },
        {
          "id": 3579,
          "name": "bob test #6638 TAGS-30"
        },
        {
          "id": 3578,
          "name": "bob test #6638 RP-1"
        },
        {
          "id": 3274,
          "name": "test #6768 CLAS-26"
        },
        {
          "id": 3081,
          "name": "bob test #6638 RSASYD-14"
        },
        {
          "id": 2729,
          "name": "tom test #6384 CLAS-16"
        },
        {
          "id": 2728,
          "name": "james test #6382 CLAS-16"
        },
        {
          "id": 1880,
          "name": "new test #5697 CERT-03"
        },
        {
          "id": 1484,
          "name": "testing test #5259 TRAIN-2"
        },
        {
          "id": 1089,
          "name": "tesdfsdfsf test #4883 INST-05"
        },
        {
          "id": 962,
          "name": "Test76 #4738 GOGL-03"
        },
        {
          "id": 961,
          "name": "Test75 #4737 GOGL-04"
        },
        {
          "id": 598,
          "name": "test #5 ENRL-05"
        },
        {
          "id": 510,
          "name": "test #5 ENRL-07"
        }
      ]
    },
    {
      "entity": "Invoice",
      "items": [
        {
          "id": 5311,
          "name": "Discount Test, #2787"
        },
        {
          "id": 5307,
          "name": "Natalli Test, #2717"
        },
        {
          "id": 5306,
          "name": "john test, #2672"
        },
        {
          "id": 5303,
          "name": "james test, #2649"
        },
        {
          "id": 5300,
          "name": "john test, #2587"
        },
        {
          "id": 5299,
          "name": "Natallia Test, #2585"
        },
        {
          "id": 5298,
          "name": "Natalli Test, #2583"
        },
        {
          "id": 5297,
          "name": "james test, #2576"
        },
        {
          "id": 5272,
          "name": "bob test, #2477"
        },
        {
          "id": 5264,
          "name": "Jim Tester, #2472"
        },
        {
          "id": 5176,
          "name": "Credit Card Test, #2440"
        },
        {
          "id": 5175,
          "name": "Credit Card Test, #2439"
        },
        {
          "id": 5174,
          "name": "Credit Card Test, #2438"
        },
        {
          "id": 5172,
          "name": "Credit Card Test, #2436"
        },
        {
          "id": 5171,
          "name": "Credit Card Test, #2435"
        },
        {
          "id": 5170,
          "name": "Credit Card Test, #2434"
        },
        {
          "id": 5169,
          "name": "Credit Card Test, #2433"
        },
        {
          "id": 5168,
          "name": "Credit Card Test, #2432"
        },
        {
          "id": 5167,
          "name": "Credit Card Test, #2431"
        },
        {
          "id": 5154,
          "name": "Jim Tester, #2418"
        },
        {
          "id": 5153,
          "name": "Jim Tester, #2417"
        },
        {
          "id": 5152,
          "name": "Jim Tester, #2416"
        },
        {
          "id": 5135,
          "name": "Test, #2399"
        },
        {
          "id": 5104,
          "name": "Tony Tester, #2368"
        },
        {
          "id": 5043,
          "name": "Jimmy Testing, #2307"
        },
        {
          "id": 4923,
          "name": "test, #2025"
        },
        {
          "id": 4902,
          "name": "bob test, #1972"
        },
        {
          "id": 4863,
          "name": "Discount Test, #1738"
        },
        {
          "id": 4859,
          "name": "Test, #2033"
        },
        {
          "id": 4816,
          "name": "john test, #1921"
        },
        {
          "id": 4812,
          "name": "test, #2047"
        },
        {
          "id": 4791,
          "name": "Test Company Pty Ltd, #2055"
        },
        {
          "id": 4767,
          "name": "Natalli Test, #2238"
        },
        {
          "id": 4756,
          "name": "tesdfsdfsf test, #1639"
        },
        {
          "id": 4691,
          "name": "james test, #1922"
        },
        {
          "id": 4665,
          "name": "Megan Test, #2167"
        },
        {
          "id": 4652,
          "name": "new test, #1812"
        },
        {
          "id": 4637,
          "name": "Megan Test test, #2190"
        },
        {
          "id": 4577,
          "name": "Megan Test mobile, #2191"
        },
        {
          "id": 4560,
          "name": "temp c test, #2186"
        },
        {
          "id": 4534,
          "name": "testing test, #1730"
        },
        {
          "id": 4530,
          "name": "test, #1458"
        },
        {
          "id": 4484,
          "name": "Test76, #1609"
        },
        {
          "id": 4442,
          "name": "tom test, #1923"
        },
        {
          "id": 4384,
          "name": "Tester Test, #2249"
        },
        {
          "id": 4312,
          "name": "Test75, #1608"
        },
        {
          "id": 4280,
          "name": "test, #2019"
        },
        {
          "id": 4234,
          "name": "Natallia Test, #2237"
        },
        {
          "id": 4208,
          "name": "Joe Tester, #2266"
        },
        {
          "id": 4184,
          "name": "bob test, #1448"
        },
        {
          "id": 4149,
          "name": "Joe Tester, #1412"
        },
        {
          "id": 4103,
          "name": "bob test, #1363"
        },
        {
          "id": 4102,
          "name": "bob test, #1362"
        },
        {
          "id": 3837,
          "name": "test, #1130"
        },
        {
          "id": 2300,
          "name": "new test, #640"
        },
        {
          "id": 1744,
          "name": "testing test, #504"
        },
        {
          "id": 1161,
          "name": "Test75, #309"
        },
        {
          "id": 1143,
          "name": "Test76, #307"
        },
        {
          "id": 1142,
          "name": "Test75, #306"
        },
        {
          "id": 753,
          "name": "test, #166"
        },
        {
          "id": 752,
          "name": "test, #165"
        },
        {
          "id": 597,
          "name": "test, #91"
        },
        {
          "id": 596,
          "name": "test, #90"
        }
      ]
    },
    {
      "entity": "Document",
      "items": [
        {
          "id": 1836,
          "name": "test1"
        },
        {
          "id": 1822,
          "name": "Test Movie File"
        },
        {
          "id": 1820,
          "name": "Test PDF"
        },
        {
          "id": 1818,
          "name": "Test Image"
        },
        {
          "id": 1598,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion_LINKEDIN-1.pdf"
        },
        {
          "id": 1597,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion_LINKEDIN-1.pdf"
        },
        {
          "id": 1497,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion_LINKEDIN-1.pdf"
        },
        {
          "id": 1496,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion_LINKEDIN-1.pdf"
        },
        {
          "id": 1365,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion_4.pdf"
        },
        {
          "id": 1364,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion_4.pdf"
        },
        {
          "id": 1270,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion_3.pdf"
        },
        {
          "id": 1269,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion_3.pdf"
        },
        {
          "id": 1175,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion_2.pdf"
        },
        {
          "id": 1174,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion_2.pdf"
        },
        {
          "id": 1080,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion_1.pdf"
        },
        {
          "id": 1079,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion_1.pdf"
        },
        {
          "id": 985,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion.pdf"
        },
        {
          "id": 984,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion.pdf"
        },
        {
          "id": 890,
          "name": "TEST-16_Jackson_Jackson_Statement_of_Completion.pdf"
        },
        {
          "id": 889,
          "name": "TEST-16_Buchanan_Amanda_Statement_of_Completion.pdf"
        },
        {
          "id": 812,
          "name": "Test PDF"
        },
        {
          "id": 738,
          "name": "test"
        },
        {
          "id": 516,
          "name": "test"
        },
        {
          "id": 381,
          "name": "Test Upload of Excel"
        },
        {
          "id": 380,
          "name": "Test upload of word doc"
        }
      ]
    }
  ];
}
