const fs = require("fs");

generateIndex();
generateCourses();

function cart() {
  return `<div data-cid="cart"></div><div data-cid="promotions"></div>`;
}

function buyButton(id) {
  return `<div data-cid="buy-button"
       data-prop-can-buy="true"
       data-prop-payment-gateway-enabled="true"
       data-prop-id="${id}"
       data-prop-name="Name ${id}"></div>`;
}

function fee(id) {
  return `<div data-cid="fees" data-prop-id="${id}"></div>`;
}

function enrolButton(id) {
  return `${fee(id)}
    <div data-cid="enrol-button"
     data-prop-id="${id}"
     data-prop-name="Course ${id}"
     data-prop-unique-identifier="2424"
     data-prop-is-canceled="false"
     data-prop-is-finished="false"
     data-prop-has-available-enrolment-places="true"
     data-prop-payment-gateway-enabled="true"
     data-prop-allow-by-application="false"
     data-prop-free-places="3"></div>`;
}

function popup() {
  return `<div data-cid="popup"></div>`;
}

function saveFile(file, data) {
  fs.writeFileSync(`${__dirname}/${file}`, data);
}

function generateIndex() {
  saveFile("index.html", `
<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Application</title>
    <style>
      .cart {
        background-color: azure;
      }
      
      .enrol-button {
        border-top: 1px solid #000000;
        background-color: aliceblue;
      }
    </style>
    <script>
      function htmlToElement(html) {
        var template = document.createElement('template');
        template.innerHTML = html;
        return template.content.firstChild;
      }
      var count = 0;
      
      var ids = [
        '5038512',
        '5038573',
        '5035698',
        '5036062',
        '5036477',
        '5036433',
        '5036589',
        '5034827',
        '5034921',
        '5036405',
        '5036425',
        '5036774',
        '5036974',
        '5036460',
        '5036207',
        '5036157',
        '5036902',
        '5034095',
        '5034138',
        '5034760',
        '5035259',
        '5035330',
        '5035368',
        '5035407',
        '5035429',
        '5035706',
        '5035766',
        '5034160',
        '5035074',
        '5035140',
        '5035158',
        '5035235',
        '5035401',
        '5035530',
        '5035804',
        '5034864',
        '5035011',
        '5034973',
        '5035463',
        '5035715',
        '5034744',
        '5035246',
        '5035296',
        '5035538',
        '5035627',
        '5035705',
        '5035719',
        '5035726',
        '5036534',
        '5036703',
        '5036876',
        '5037058',
        '5038578',
        '5039125',
        '5039126',
        '5039131'
      ];
      
      function append() {
        count++;
      
        document.body.appendChild(htmlToElement([
        '<div>',
        '<div data-cid="fees" data-prop-id="' + ids[count] + '"></div>',
        '<div class="enrol-button">',
         '<div data-cid="enrol-button"',
         '  data-prop-id="' + ids[count] + '"',
         '  data-prop-name="Course ' + ids[count] + '"',
         '  data-prop-unique-identifier="2424"',
         '  data-prop-is-canceled="false"',
         '  data-prop-is-finished="false"',
         '  data-prop-has-available-enrolment-places="true"',
         '  data-prop-payment-gateway-enabled="true"',
         '  data-prop-allow-by-application="false"',
         '  data-prop-free-places="3"></div>',
         '</div>',
         '</div>'].join('')));
        
        window.Ish.react.bootstrap();
      }
      
    </script>
  </head>
  <body>
    <button onclick="append()">Add New Enrol Button</button>
    <div class="cart">${cart()}</div>
    <div class="popup">${popup()}</div>
    <div class="enrol-button">${enrolButton(5038511)}</div>
    <div class="enrol-button">${enrolButton(5038575)}</div>
    <div class="enrol-button">${enrolButton(5037328)}</div>
  </body>
</html>
`);
  console.log("index.html generated");
}

function generateCourses() {
  saveFile("courses.html", `
<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Application - Cources</title>
    <style>
      .cart {
        background-color: azure;
      }
      
      .enrol-button {
        border-top: 1px solid #000000;
        background-color: aliceblue;
      }
    </style>
  </head>
  <link rel="stylesheet" href="https://www.sydneycommunitycollege.edu.au/s/stylesheets/css/site.css?v=r27609"/>

<body id="DetailsPage" class="internal-page">


<script type="text/javascript">
    dataLayer = [];
</script>
<script>(function (w, d, s, l, i) {
        w[l] = w[l] || [];
        w[l].push({
            'gtm.start': new Date().getTime(), event: 'gtm.js'
        });
        var f = d.getElementsByTagName(s)[0],
            j = d.createElement(s), dl = l != 'dataLayer' ? '\u0026l=' + l : '';
        j.async = true;
        j.src =
            '//www.googletagmanager.com/gtm.js?id=' + i + dl;
        f.parentNode.insertBefore(j, f);
    })(window, document, 'script', 'dataLayer', 'GTM-54LK2H');</script>


<section class="site-wrapper">
    <section id="container" class="site-container">
        <header id="header" class="site-header">
            <section class="header-container">

                <hgroup class="container header-hgroup">
                    <aside class="pull-left">
                        <h2 id="siteLogo" class="site-logo">
                            <a href="/">
                                <img class="normal_logo" alt="Sydney Community College" src="/s/img/logos/logo.png"/>
                            </a>
                        </h2>
                        <h1 id="siteTitle" class="site-title">
                            <a href="/">Sydney Community College</a>
                        </h1>
                    </aside>
                    <aside class="pull-right">
                        <ul class="header-top-links">
                            <li>
                                <a href="/about">About</a>
                            </li>
                            <li>
                                <a href="https://skillsoncourse.com.au/portal/login">Login</a>
                            </li>
                            <li>
                                <a href="/contact">Contact</a>
                            </li>
                        </ul>
                        ${cart()}
                        <a href="#" class="nav-toggle js-nav-toggle pull-right">
                            <div class="">
					<span class="toggle-title pull-right">
						<!-- <i class="sicon toggle-icon"></i>Menu-->
					</span>
                            </div>
                        </a>
                    </aside>
                </hgroup>
                <div class="clearfix"></div>
                <section class="container content find-course-form">
                    <div class="header-course-form clearfix">
                        <aside id="headerToolbar" class="header-toolbar">

                            <div id="search_box" class="noprint search-box">
                                <h3 class="search-box-title">
                                    <span>Search</span>
                                </h3>
                                <form name="search" id="search" method="get" action="/courses">
                                    <input placeholder="Search courses and keywords..." autocomplete="off" size="15"
                                           name="s" class="quicksearch" id="s" type="text"></input>
                                    <button class="find btn abtn gamma" id="find" type="submit">
                                        <i class="sicon search-icon"></i>Find Courses
                                    </button>
                                </form>
                                <!-- searchTagNames="literal:Subjects" by default even if no property specified,
                                if we need to add more then one element we need to separate then using ';' character without extra spaces
                                for example searchTagNames="literal:Subjects;some_group_tag_1;some_group_tag_1"
                                please note that group elements available only for tag group with valid names -->

                                <div class="clearfix" id="advanced_search_container">
                                    <div id="advanced_search" class="">
                                        <!--clientValidation should be NONE to exclude usage Tapestry java script injection-->
                                        <form name="search" class="advanced-search-form"
                                              action="/ui/coursedetails.pagestructure.bodystructure.bodyheader.coursesearchform.searchinputs.search2"
                                              method="post" id="search2">
                                            <div class="t-invisible"><input
                                                        value="H4sIAAAAAAAAAM2VP2gUQRTGJ4GAeiCiaG0RixS3dxdyIopIiMTmiOKioIS7vJ19eze52Z1x/mTvEKwt7O0Ewc4/rb0pJI2NYi/YCqJgpeBs1suRRkixw3X75r233/eDb2dffSMLOZCeZY01YZXG62iAcX1ZQh+1UZYaqzCIRDw+XA0QYlQB3V/SCIoOEqHSoHxkmbRGBwZHJmHIY61IW6h+ABLoAAMDsnj5uO32FXIWBRFoDFYjdwjUrBcriyEaKy/c2a19Ofv+9zyZ65AaFZlRgm9Aioac7mzDDjQ4ZP1GaBTL+ldG0pAaxDu9IY5zoWI/ZKtHJbulBEWtQxulTGsmst038Ury69mHeUJGMt8k96sxzYWQzm/wX79UpFJkmLn5jptfbNdv7n1eIg/3ZtFc697X1z+Ttx+f+zG3cSRzt13ftUIDBteVSMOiitc4cyN3gVusP3336TE5/+dfuKeNB+QRmSvCfMxlZBup0XmXbFaDppEXAoUkcYJl2WtWLVjoLcxYmlZ+yGTpxrmX12Yx6hebp75f6r54crU0RwlUfLP1mpNQnNE2siqqgzWisMXRoBcDZUg8KLUmqMeLv4dUjHoBbJWAW6RbjZJLFh1GYnTwdU8OqpcsySKyVa3MNKQnpkc+ZH3xHSTzZI44jGFcl6DcreBD2hfj8iFGzGJ/jMv7jH8B4/9tigYLAAA="
                                                        name="t:formdata" type="hidden"></input></div>
                                            <div id="adv_search_keyword">
                                                <label for="adv_keyword" title="Search keywords and functions">I'm
                                                    Looking For</label>
                                                <input default="keyword" id="adv_keyword" name="adv_keyword"
                                                       type="text"></input>
                                            </div>
                                            <div id="adv_search_tag">

                                                <label for="tagGroup">Area of study</label>
                                                <select id="select" name="select_0">
                                                    <option value="">Any area of study</option>
                                                    <option value="Arts">Arts</option>
                                                    <option value="Business">Business</option>
                                                    <option value="Languages">Languages</option>
                                                    <option value="Lifestyle">Lifestyle</option>
                                                    <option value="New Courses">New Courses</option>
                                                    <option value="Other">Other</option>
                                                    <option value="Sport">Sport</option>
                                                    <option value="Training">Training</option>
                                                </select>

                                            </div>
                                            <div id="adv_search_location">
                                                <label for="suburb-autocomplete"
                                                       title="Enter a suburb or postcode near where you'd like to do the course">Location</label>
                                                <input default="postcode or suburb" placeholder="Any location"
                                                       class="suburb-autocomplete" autocomplete="off" size="18"
                                                       id="suburb-autocomplete" name="suburb-autocomplete"
                                                       type="text"></input>
                                            </div>
                                            <div id="adv_search_price">
                                                <label for="adv_price"
                                                       title="Select the maximum price you'd like to pay for a course">Costing
                                                    up to</label>
                                                <input size="8" id="adv_price" name="adv_price" type="text"></input>
                                            </div>
                                            <div id="adv_search_time">
                                                <label class="time checkbox">
                                                    <input id="checkbox" name="checkbox" type="checkbox"></input> Day
                                                    time </label>
                                                <label class="time checkbox">
                                                    <input id="checkbox_0" name="checkbox_0" type="checkbox"></input>
                                                    Evenings </label>
                                            </div>
                                            <div id="adv_search_day">
                                                <label class="day checkbox">
                                                    <input class="parent" id="weekday-parent" name="weekday-parent"
                                                           type="checkbox"></input> Weekdays </label>
                                                <label class="day checkbox">
                                                    <input class="parent" id="weekend-parent" name="weekend-parent"
                                                           type="checkbox"></input> Weekends </label>
                                            </div>
                                            <div id="adv_search_submit">
                                                <button class="btn btn-primary abtn gamma" id="searcher" type="submit">
                                                    Search
                                                </button>
                                                <a class="btn abtn gamma" onClick="clearAdvancedSearch();"
                                                   id="cancel-search">Clear</a>
                                            </div>
                                        </form>
                                    </div>
                                </div>

                                <div class="advanced-search-button more-options">
                                    <a class="show-advanced-search sicon arrow down">
                                        <span>More search options</span>
                                    </a>
                                </div>
                                <div class="quicksearch-wrap-container">
                                    <div class="quicksearch-wrap"></div>
                                </div>
                            </div>

                        </aside>
                    </div>
                </section>
                <!-- <section class="top-bar">
                    <div class="container">
                        <aside class="header-toolbar" id="headerToolbar">
                            <a href="#" class="header-search-toggle pull-right">
                                <i class="sicon search-icon"></i>
                            </a>
                            <span t:type="ui/PromoCodesView" />
                        </aside>
                    </div>
                </section>-->


            </section>
            <section class="container navbar">
                <nav id="nav" class="site-nav">
                    <ul class="header-top-links">
                        <li>
                            <a href="/about">About</a>
                        </li>
                        <li>
                            <a href="https://skillsoncourse.com.au/login">Login</a>
                        </li>
                        <li>
                            <a href="/contact">Contact</a>
                        </li>
                    </ul>
                    <ul class="list-horizontal">


                        <li class="">
                            <a href="/">Home</a>
                            <ul>

                                <li class="">
                                    <a href="/about">About</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/propose-a-course">Propose a course</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/contact">Contact</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/policies">Policies</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/Sites">Sites</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/vacancies">Vacancies</a>
                                    <ul></ul>
                                </li>

                            </ul>
                        </li>


                        <li class="">
                            <a href="/products">Gift Vouchers</a>
                            <ul></ul>
                        </li>


                        <li class="">
                            <a href="/courses/Arts">Arts</a>
                            <ul></ul>
                        </li>


                        <li class="">
                            <a href="/courses/learn-languages-sydney-beginners-classes">Languages</a>
                            <ul></ul>
                        </li>


                        <li class="">
                            <a href="/courses/Lifestyle">Lifestyle</a>
                            <ul></ul>
                        </li>


                        <li class="">
                            <a href="/courses/Business">Business</a>
                            <ul></ul>
                        </li>


                        <li class="">
                            <a href="/courses/Sport">Sport</a>
                            <ul></ul>
                        </li>


                        <li class="">
                            <a href="/courses/Training">Training</a>
                            <ul>

                                <li class="">
                                    <a href="/funded-programmes">Funded Programmes</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/trainer-and-assessor-information">Trainers and Assessors</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/policies/rto">Training Policies</a>
                                    <ul></ul>
                                </li>


                                <li class="">
                                    <a href="/courses/Training">Courses</a>
                                    <ul></ul>
                                </li>

                            </ul>
                        </li>


                    </ul>
                </nav>
            </section>


            <section class="find-course-form course-search-form-block">
                <div class="container content"></div>
            </section>


        </header>
        <section id="contentContainer" class="container content-container">
            <div class="clearfix sidebar-dropdown">
                <a href="#" class="btn abtn alpha">
                    Sections <i class="sicon arrow down"></i>
                </a>
            </div>
            <aside id="sidebarLeft" class="sidebar-left">


                <div class="courses-menu">

                    <div class="taggroup-212">
                        <ul class="courses-list">

                            <li class="hasChildren parent_tag">
                                <a href="/courses/Subjects">Subjects</a>
                            </li>

                            <ul class="courses-list-sub">

                                <li class="">
                                    <a href="/courses/New+Courses">New Courses</a>

                                    <ul class="courses-list-sub2">

                                    </ul>
                                </li>

                                <li class="hasChildren">
                                    <a href="/courses/art-classes-sydney">Arts</a>

                                    <ul class="courses-list-sub2">

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/Adobe-Courses-learn-Abobe-Sydney">Adobe-Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/writing-creative-writing-courses-classes-sydney">Creative
                                                Writing Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/Disability-Programs">DisAbility</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/Drama-Acting-Courses">Drama Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/drawing-classes-courses-sydney">Drawing
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/film-video-courses-classes-sydney">Film
                                                & Video Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/floral-design-courses-classes-sydney">Floral
                                                Art Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/glass-art-classes-courses-sydney">Glass
                                                Art Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/graphic-design-classes-courses-sydney">Graphic
                                                Design Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/handcrafts-handicrafts-classes-courses-sydney">Handcrafts
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/jewellery-making-silver-jewellery-courses-classes-workshops-sydney">Jewellery
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/music-singing-choir-courses-classes-sydney">Music
                                                & Singing Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/millinery-classes-hatmaking-classes-sydney">Millinery
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/painting-classes-sydney-courses-workshops">Painting
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/digital-photography-DSLR-camera-courses-classes-sydney">Photography
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/Printing+Classes">Printing Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/sculpture-sydney-course-workshops-classes">Sculpture
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/sewing-courses-courses-sydney">Sewing
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/Silversmithing-classes-sydney">Silversmithing
                                                classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/painting-jewellery-drawing-classes-workshops">Studio
                                                Workshops</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/sewing-knitting-fabric-millinery-classes-courses-sydney">Textiles
                                                & Fashion Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/drawing-painting-printing-sculpture-courses-beginners-classes-sydney">Visual
                                                Arts Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/art-classes-sydney/Learn-wordpress-training-courses-sydney">Wordpress-Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                    </ul>
                                </li>

                                <li class="hasChildren">
                                    <a href="/courses/business-classes-sydney">Business</a>

                                    <ul class="courses-list-sub2">

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Accounting">Accounting</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Adobe+Training">Adobe Training</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Business-Management+">Business-Management </a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Career+Development">Career
                                                Development</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/eCommerce">eCommerce</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="hasChildren">
                                            <a href="/courses/business-classes-sydney/Information+Technology">Information
                                                Technology</a>

                                            <ul class="courses-list-sub2">

                                                <li class="">
                                                    <a href="/courses/business-classes-sydney/Information+Technology/Basic+Computing">Basic
                                                        Computing</a>

                                                    <ul class="courses-list-sub2">

                                                    </ul>
                                                </li>

                                                <li class="">
                                                    <a href="/courses/business-classes-sydney/Information+Technology/Coding">Coding</a>

                                                    <ul class="courses-list-sub2">

                                                    </ul>
                                                </li>

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Job+Ready">Job Ready</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Marketing-Communications">Marketing-Communications</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Personal+Investment+Classes">Personal
                                                Investment Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/learn-maths-classes-beginners-revision-sydney">Mathematics
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Online+Courses">Online Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Search+Engine+Optimisation">Search
                                                Engine Optimisation</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Social+Media+Classes">Social Media
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/TAE+-+Training+and+Assessment">TAE
                                                - Training and Assessment</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/Training+Qualifications">Training
                                                Qualifications</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/business-classes-sydney/learn-wordpress-classes-sydney-courses-web-design">Wordpress-Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                    </ul>
                                </li>

                                <li class="hasChildren">
                                    <a href="/courses/learn-languages-sydney-beginners-classes">Languages</a>

                                    <ul class="courses-list-sub2">

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/learn-languages-sydney-beginners-classes-Learn-Arabic">Arabic
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/beginners-chinese-classes-sydney">Cantonese
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/beginners-english-classes-sydney">English
                                                Classes - Beginners, Intermediate and Advanced</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/beginners-french-classes-sydney">French
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/beginners-german-classes">German
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/beginners-greek-classes-sydney">Greek
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/beginners-Italian-Classes-sydney">Italian
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/beginners-Indonesian-Classes-sydney">Indonesian
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/learn-Japanese-beginners-Classes-sydney">Japanese
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/learn-Korean-Classes-for-beginners-sydney">Korean
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/learn-Mandarin-Classes-beginners-sydney">Mandarin
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/learn-Spanish-for-beginners-Classes-sydney">Spanish
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/learn-Sign-Language-Classes-sydney">Sign
                                                Language Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/thai-for-beginners-classes-sydney">Thai
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/learn-languages-sydney-beginners-classes/learn-Vietnamese-for-beginners-Classes-sydney">Vietnamese
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                    </ul>
                                </li>

                                <li class="hasChildren">
                                    <a href="/courses/training-courses-sydney">Training</a>

                                    <ul class="courses-list-sub2">

                                        <li class="">
                                            <a href="/courses/training-courses-sydney/Funded+Programs">Funded
                                                Programs</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                    </ul>
                                </li>

                                <li class="hasChildren">
                                    <a href="/courses/lifestyle-classes-house-and-garden-sydney">Lifestyle</a>

                                    <ul class="courses-list-sub2">

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Cooking">Cooking
                                                Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Disability-Courses">DisAbility</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="hasChildren">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/House-Renovation-Garden-Design">House
                                                & Garden</a>

                                            <ul class="courses-list-sub2">

                                                <li class="">
                                                    <a href="/courses/lifestyle-classes-house-and-garden-sydney/House-Renovation-Garden-Design/DIY+Classes">DIY
                                                        Classes</a>

                                                    <ul class="courses-list-sub2">

                                                    </ul>
                                                </li>

                                                <li class="">
                                                    <a href="/courses/lifestyle-classes-house-and-garden-sydney/House-Renovation-Garden-Design/Interior-Design-Decoration-Course">Interior
                                                        Decoration</a>

                                                    <ul class="courses-list-sub2">

                                                    </ul>
                                                </li>

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Psychology-Short-Course">Psychology</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Philosophy-Short-Course">Philosophy</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/NLP-Neuro-Linguistic-Programming-Training">NLP
                                                Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Makeup-Dress-Fashion-Course">Makeup
                                                & Personal Presentation</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Swedish-Massage-Course">Swedish
                                                Massage</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Personalised-Gift-Vouchers-Cards-Sydney">Gift
                                                Vouchers</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Family+Tree">Family
                                                Tree</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/lifestyle-classes-house-and-garden-sydney/Cake-Baking-Decorating-Course">Cake
                                                Baking & Decorating Classes</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                    </ul>
                                </li>

                                <li class="hasChildren parent_tag">
                                    <a href="/courses/sport-and-fitness-classes-sydney">Sport</a>

                                    <ul class="courses-list-sub2">

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Archery">Archery</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Boating">Boating</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Dance">Dance</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/DisAbility">DisAbility</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="active_tag">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Fitness">Fitness</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Golf">Golf</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Tai-Chi-Yoga-Pilates-Health-Course">Health
                                                & Wellbeing</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Horse-Riding">Horse
                                                Riding</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Indoor-Climbing">Indoor
                                                Climbing</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="active_tag">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Recreation-Leisure-Activities">Recreation</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Swimming">Swimming</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="hasChildren">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Tennis-Coaching-Programs-lessons-Sydney">Tennis
                                                Coaching</a>

                                            <ul class="courses-list-sub2">

                                                <li class="">
                                                    <a href="/courses/sport-and-fitness-classes-sydney/Tennis-Coaching-Programs-lessons-Sydney/Tennis-Coaching">Adults
                                                        Program</a>

                                                    <ul class="courses-list-sub2">

                                                    </ul>
                                                </li>

                                                <li class="">
                                                    <a href="/courses/sport-and-fitness-classes-sydney/Tennis-Coaching-Programs-lessons-Sydney/Children-Tennis-Lessons">Kids
                                                        Program</a>

                                                    <ul class="courses-list-sub2">

                                                    </ul>
                                                </li>

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Childrens-Tennis">Tennis
                                                for Children</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/sport-and-fitness-classes-sydney/Yoga-Pilates-Meditation-Tai-Chi-Rozelle">Yoga
                                                & Pilates</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                    </ul>
                                </li>

                                <li class="hasChildren">
                                    <a href="/courses/Other">Other</a>

                                    <ul class="courses-list-sub2">

                                        <li class="">
                                            <a href="/courses/Other/Daytime+Courses">Daytime Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/Other/Evening+Courses">Evening Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                        <li class="">
                                            <a href="/courses/Other/Weekend+Courses">Weekend Courses</a>

                                            <ul class="courses-list-sub2">

                                            </ul>
                                        </li>

                                    </ul>
                                </li>


                            </ul>
                        </ul>
                    </div>
                </div>


            </aside>
            <article id="content" class="content">


                <div class="confirmOrderDialog dialogContainer">
                    <p class="confirm-message">
                        <strong class="confirm-txt">Thanks for adding:</strong>
                    <div class="className"></div>
                    <div class="classDate"></div>
                    </p>
                    <p class="confirm-proseed">
                        <a class="button" href="/enrol/">Proceed to Checkout</a>
                    </p>
                    <p class="confirm-close-wrapper">
                        <a class="button closeButton" href="#">Continue browsing</a>
                    </p>
                    <div class="closeButton">X</div>
                </div>


                <div class="clearfix vevent courseItem tag-Fitness tag-Recreation" itemscope="itemscope"
                     itemtype="http://schema.org/Product">

                    <div class="clearfix course-image-banner">
                        <div class="clearfix course-banner">
                            <div class="hero-image-banner">


                                <img height="183" width="275"
                                     src="https://s3-ap-southeast-2.amazonaws.com/ish-oncourse-scc/e2588c93-c893-420c-851c-f4086da1f339"/>


                            </div>

                            <h1 class="summary page-title">Summer Bushwalking Adventure Series</h1>
                        </div>

                        <div class="clearfix course-details-actions-wrapper">
                            <div class="clearfix course-details-actions">
                                <ul>
                                    <!--<li><a href="/enrol/">Enrol Now</a></li>-->
                                    <li><a href="#course-class">Classes</a></li>
                                    <li><a href="#course-description">Course Description</a></li>
                                    <!--<li><a href="#imagegallery">Image Gallery</a></li>-->
                                    <li><a href="/group-bookings">Group Bookings</a></li>
                                </ul>

                                <div class="addthis">
                                    <div class="addthis-title">Share this course</div>

                                    <div class="addthis_toolbox addthis_default_style ">
                                        <a class="addthis_button_email"></a>
                                        <a class="addthis_button_preferred_1"></a>
                                        <a class="addthis_button_preferred_2"></a>
                                        <a class="addthis_button_preferred_3"></a>
                                        <a class="addthis_button_compact"></a>
                                    </div>
                                    <script src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-4f0fc23723d20460"
                                            type="text/javascript"></script>

                                </div>

                            </div>
                        </div>
                    </div>


                    <div class="clearboth"></div>


                    <div class="clearfix courseItem-sub-wrapper">


                        <div class="clearfix classes">
                            <a id="course-class" name="course-class"></a>
                            <h3 class="course-sub-title">Classes</h3>


                            <div itemscope="itemscope" itemtype="http://schema.org/EducationEvent"
                                 data-sku="bushwalking.sydney-1" data-classid="5132142" class="classItem vevent ">

                                <!--hcalendar implementation-->
                                <div class="class-item-info">
                                    <div class="class-item-info-l">

                                        <div class="date">
                                            <a class="timeline date-time date-header"
                                               href="/Timeline/sessions?ids=5132142" rel="session" timezone="10">
                                                <abbr class="dtstart" itemprop="startDate">
                                                    Sun 15 Jan
                                                </abbr>


                                            </a>
                                        </div>
                                        <div class="date sessions-time">
                                            <div class="left cicon icon-sessions">
                                                <a rel="session" class="timeline" itemprop="duration"
                                                   href="/Timeline/sessions?ids=5132142" timezone="10">1 session, 7
                                                    hours total</a>
                                            </div>
                                            <div class="right timeline date-time cicon icon-session-time">
                                                <a rel="session" class="timeline date-time"
                                                   href="/Timeline/sessions?ids=5132142" timezone="10">
                                                    <abbr class="dtstart" itemprop="startDate">
                                                        8am
                                                    </abbr> - <abbr class="dtend" itemprop="endDate">
                                                        3pm
                                                    </abbr>

                                                </a>
                                            </div>
                                        </div>


                                        <div class="summary" itemprop="name">Summer Bushwalking Adventure Series</div>
                                    </div>
                                    <div class="class-item-info-r">

                                        <div class="left cicon icon-location location" itemprop="location">


                                            The Royal National Park


                                        </div>


                                        <div class="right tutor cicon icon-person">

                                            <a title="View tutor profile" href="/tutor/16347" itemprop="performers"
                                               class="nyromodal">TheAdventureProject
                                            </a>


                                        </div>

                                    </div>
                                </div>
                                <div class="classAction">

                                    ${enrolButton(5038511)}

                                    <div class="classStatus">


                                        There
                                        is one place
                                        available


                                    </div>


                                    <!-- div class="courseEmail">
                                     <a href="">Email to a friend</a>
                                 </div-->
                                </div>


                                <div class="classDescription class-note-important">
                                    Summer Bushwalking Adventure Series #1 – Royal National Park – Coastal Track
                                </div>


                                <div class="bubbleInfo">
                                    <div class="tooltip_popup enrol_tooltip">
                                        <div class="arrow">
                                            <span></span>
                                        </div>
                                        <div class="bubble_top bubble_content">
					<span class="timing-display">

                            <span class="timing-weekday timing-Monday match-8 match-1 timing-no">Mo</span>

                            <span class="timing-weekday timing-Tuesday match-8 match-1 timing-no">Tu</span>

                            <span class="timing-weekday timing-Wednesday match-8 match-1 timing-no">We</span>

                            <span class="timing-weekday timing-Thursday match-8 match-1 timing-no">Th</span>

                            <span class="timing-weekday timing-Friday match-8 match-1 timing-no">Fr</span>

                            <span class="timing-weekend timing-Saturday match-9 match-6 timing-no">Sa</span>

                            <span class="timing-weekend timing-Sunday match-9 match-6 timing-yes">Su</span>

					</span>
                                            <span class="timing-display">
						<span class="timing-daytime match-10 timing-daytime-yes">
							<img src="/s/img/blank.png"/>
						</span>
						<span class="timing-evening match-10 timing-evening-no">
							<img src="/s/img/blank.png"/>
						</span>
					</span>
                                        </div>
                                        <div class="bubble_middle bubble_content">
                                            <div class="class-link">
                                                <a itemprop="url" href="/class/bushwalking.sydney-1"> View this
                                                    class... </a>
                                            </div>
                                            <div class="course-link">
                                                <a href="/course/bushwalking.sydney"> More about <em>Summer Bushwalking
                                                        Adventure Series</em>...
                                                </a>
                                            </div>
                                        </div>
                                        <div class="bubble_bottom"></div>
                                    </div>
                                </div>


                                <div id="sessions_for_class_5132142" class="sessions_for_class hidden classSessions">

                                    <table class="session-table">
                                        <thead>
                                        <tr>

                                            <th>When</th>

                                            <th>Time</th>

                                            <th>Where</th>

                                            <th>Session Notes</th>

                                        </tr>
                                        </thead>

                                        <tr class="tr-even">
                                            <td>
                                                Sun 15 Jan 2017
                                            </td>
                                            <td>
                                                8am
                                                - 3pm (UTC+11:00)

                                            </td>
                                            <td>


                                                <a href="/room/13052">


                                                    Garie Beach


                                                    - The Royal National Park


                                                </a>


                                            </td>
                                            <td>

                                            </td>
                                        </tr>

                                    </table>

                                </div>


                            </div>


                            <div itemscope="itemscope" itemtype="http://schema.org/EducationEvent"
                                 data-sku="bushwalking.sydney-2" data-classid="5132173" class="classItem vevent ">

                                <!--hcalendar implementation-->
                                <div class="class-item-info">
                                    <div class="class-item-info-l">

                                        <div class="date">
                                            <a class="timeline date-time date-header"
                                               href="/Timeline/sessions?ids=5132173" rel="session" timezone="10">
                                                <abbr class="dtstart" itemprop="startDate">
                                                    Sun 12 Feb
                                                </abbr>


                                            </a>
                                        </div>
                                        <div class="date sessions-time">
                                            <div class="left cicon icon-sessions">
                                                <a rel="session" class="timeline" itemprop="duration"
                                                   href="/Timeline/sessions?ids=5132173" timezone="10">1 session, 7
                                                    hours total</a>
                                            </div>
                                            <div class="right timeline date-time cicon icon-session-time">
                                                <a rel="session" class="timeline date-time"
                                                   href="/Timeline/sessions?ids=5132173" timezone="10">
                                                    <abbr class="dtstart" itemprop="startDate">
                                                        8am
                                                    </abbr> - <abbr class="dtend" itemprop="endDate">
                                                        3pm
                                                    </abbr>

                                                </a>
                                            </div>
                                        </div>


                                        <div class="summary" itemprop="name">Summer Bushwalking Adventure Series</div>
                                    </div>
                                    <div class="class-item-info-r">

                                        <div class="left cicon icon-location location" itemprop="location">


                                            Ku-ring-ai-Chase National Park


                                        </div>


                                        <div class="right tutor cicon icon-person">

                                            <a title="View tutor profile" href="/tutor/16347" itemprop="performers"
                                               class="nyromodal">TheAdventureProject
                                            </a>


                                        </div>

                                    </div>
                                </div>
                                <div class="classAction">

                                    <div class="price">


                                        $139


                                        <span class="gst">
				 inc <span title="Goods and Services Tax">GST</span>
			</span>


                                        <span class="discount-price">/</span>
                                        <acronym title="Registered Pension Holders - Call office"
                                                 class="discount-price">
                                            $111.2
                                        </acronym>


                                    </div>

                                    ${enrolButton(5038575)}

                                    <div class="classStatus">


                                        There are places available


                                    </div>


                                    <!-- div class="courseEmail">
                                     <a href="">Email to a friend</a>
                                 </div-->
                                </div>


                                <div class="classDescription class-note-important">
                                    Summer Bushwalking Adventure Series #2 – Ku-ring-ai-Chase National Park – Jerusalem
                                    Bay Track
                                </div>


                                <div class="bubbleInfo">
                                    <div class="tooltip_popup enrol_tooltip">
                                        <div class="arrow">
                                            <span></span>
                                        </div>
                                        <div class="bubble_top bubble_content">
					<span class="timing-display">

                            <span class="timing-weekday timing-Monday match-8 match-1 timing-no">Mo</span>

                            <span class="timing-weekday timing-Tuesday match-8 match-1 timing-no">Tu</span>

                            <span class="timing-weekday timing-Wednesday match-8 match-1 timing-no">We</span>

                            <span class="timing-weekday timing-Thursday match-8 match-1 timing-no">Th</span>

                            <span class="timing-weekday timing-Friday match-8 match-1 timing-no">Fr</span>

                            <span class="timing-weekend timing-Saturday match-9 match-6 timing-no">Sa</span>

                            <span class="timing-weekend timing-Sunday match-9 match-6 timing-yes">Su</span>

					</span>
                                            <span class="timing-display">
						<span class="timing-daytime match-10 timing-daytime-yes">
							<img src="/s/img/blank.png"/>
						</span>
						<span class="timing-evening match-10 timing-evening-no">
							<img src="/s/img/blank.png"/>
						</span>
					</span>
                                        </div>
                                        <div class="bubble_middle bubble_content">
                                            <div class="class-link">
                                                <a itemprop="url" href="/class/bushwalking.sydney-2"> View this
                                                    class... </a>
                                            </div>
                                            <div class="course-link">
                                                <a href="/course/bushwalking.sydney"> More about <em>Summer Bushwalking
                                                        Adventure Series</em>...
                                                </a>
                                            </div>
                                        </div>
                                        <div class="bubble_bottom"></div>
                                    </div>
                                </div>


                                <div id="sessions_for_class_5132173" class="sessions_for_class hidden classSessions">

                                    <table class="session-table">
                                        <thead>
                                        <tr>

                                            <th>When</th>

                                            <th>Time</th>

                                            <th>Where</th>

                                            <th>Session Notes</th>

                                        </tr>
                                        </thead>

                                        <tr class="tr-even">
                                            <td>
                                                Sun 12 Feb 2017
                                            </td>
                                            <td>
                                                8am
                                                - 3pm (UTC+11:00)

                                            </td>
                                            <td>


                                                <a href="/room/13054">


                                                    Jerusalem Bay Track


                                                    - Ku-ring-ai-Chase National Park


                                                </a>


                                            </td>
                                            <td>

                                            </td>
                                        </tr>

                                    </table>

                                </div>


                            </div>


                            <div itemscope="itemscope" itemtype="http://schema.org/EducationEvent"
                                 data-sku="bushwalking.sydney-3" data-classid="5132177" class="classItem vevent ">

                                <!--hcalendar implementation-->
                                <div class="class-item-info">
                                    <div class="class-item-info-l">

                                        <div class="date">
                                            <a class="timeline date-time date-header"
                                               href="/Timeline/sessions?ids=5132177" rel="session" timezone="10">
                                                <abbr class="dtstart" itemprop="startDate">
                                                    Sun 12 Mar
                                                </abbr>


                                            </a>
                                        </div>
                                        <div class="date sessions-time">
                                            <div class="left cicon icon-sessions">
                                                <a rel="session" class="timeline" itemprop="duration"
                                                   href="/Timeline/sessions?ids=5132177" timezone="10">1 session, 7
                                                    hours total</a>
                                            </div>
                                            <div class="right timeline date-time cicon icon-session-time">
                                                <a rel="session" class="timeline date-time"
                                                   href="/Timeline/sessions?ids=5132177" timezone="10">
                                                    <abbr class="dtstart" itemprop="startDate">
                                                        8am
                                                    </abbr> - <abbr class="dtend" itemprop="endDate">
                                                        3pm
                                                    </abbr>

                                                </a>
                                            </div>
                                        </div>


                                        <div class="summary" itemprop="name">Summer Bushwalking Adventure Series</div>
                                    </div>
                                    <div class="class-item-info-r">

                                        <div class="left cicon icon-location location" itemprop="location">


                                            The Royal National Park


                                        </div>


                                        <div class="right tutor cicon icon-person">

                                            <a title="View tutor profile" href="/tutor/16347" itemprop="performers"
                                               class="nyromodal">TheAdventureProject
                                            </a>


                                        </div>

                                    </div>
                                </div>
                                <div class="classAction">

                                    <div class="price">


                                        $139


                                        <span class="gst">
				 inc <span title="Goods and Services Tax">GST</span>
			</span>


                                        <span class="discount-price">/</span>
                                        <acronym title="Registered Pension Holders - Call office"
                                                 class="discount-price">
                                            $111.2
                                        </acronym>


                                    </div>

                                    ${enrolButton(5037328)}


                                    <div class="classStatus">


                                        There are places available


                                    </div>


                                    <!-- div class="courseEmail">
                                     <a href="">Email to a friend</a>
                                 </div-->
                                </div>


                                <div class="classDescription class-note-important">
                                    Summer Bushwalking Adventure Series #3 – Royal National Park – Coastal Walk Part 2
                                </div>


                                <div class="bubbleInfo">
                                    <div class="tooltip_popup enrol_tooltip">
                                        <div class="arrow">
                                            <span></span>
                                        </div>
                                        <div class="bubble_top bubble_content">
					<span class="timing-display">

                            <span class="timing-weekday timing-Monday match-8 match-1 timing-no">Mo</span>

                            <span class="timing-weekday timing-Tuesday match-8 match-1 timing-no">Tu</span>

                            <span class="timing-weekday timing-Wednesday match-8 match-1 timing-no">We</span>

                            <span class="timing-weekday timing-Thursday match-8 match-1 timing-no">Th</span>

                            <span class="timing-weekday timing-Friday match-8 match-1 timing-no">Fr</span>

                            <span class="timing-weekend timing-Saturday match-9 match-6 timing-no">Sa</span>

                            <span class="timing-weekend timing-Sunday match-9 match-6 timing-yes">Su</span>

					</span>
                                            <span class="timing-display">
						<span class="timing-daytime match-10 timing-daytime-yes">
							<img src="/s/img/blank.png"/>
						</span>
						<span class="timing-evening match-10 timing-evening-no">
							<img src="/s/img/blank.png"/>
						</span>
					</span>
                                        </div>
                                        <div class="bubble_middle bubble_content">
                                            <div class="class-link">
                                                <a itemprop="url" href="/class/bushwalking.sydney-3"> View this
                                                    class... </a>
                                            </div>
                                            <div class="course-link">
                                                <a href="/course/bushwalking.sydney"> More about <em>Summer Bushwalking
                                                        Adventure Series</em>...
                                                </a>
                                            </div>
                                        </div>
                                        <div class="bubble_bottom"></div>
                                    </div>
                                </div>


                                <div id="sessions_for_class_5132177" class="sessions_for_class hidden classSessions">

                                    <table class="session-table">
                                        <thead>
                                        <tr>

                                            <th>When</th>

                                            <th>Time</th>

                                            <th>Where</th>

                                            <th>Session Notes</th>

                                        </tr>
                                        </thead>

                                        <tr class="tr-even">
                                            <td>
                                                Sun 12 Mar 2017
                                            </td>
                                            <td>
                                                8am
                                                - 3pm (UTC+11:00)

                                            </td>
                                            <td>


                                                <a href="/room/13053">


                                                    Otford Station


                                                    - The Royal National Park


                                                </a>


                                            </td>
                                            <td>

                                            </td>
                                        </tr>

                                    </table>

                                </div>


                            </div>


                            <div itemscope="itemscope" itemtype="http://schema.org/EducationEvent"
                                 data-sku="bushwalking.sydney-4" data-classid="5132178" class="classItem vevent ">

                                <!--hcalendar implementation-->
                                <div class="class-item-info">
                                    <div class="class-item-info-l">

                                        <div class="date">
                                            <a class="timeline date-time date-header"
                                               href="/Timeline/sessions?ids=5132178" rel="session" timezone="10">
                                                <abbr class="dtstart" itemprop="startDate">
                                                    Sun 09 Apr
                                                </abbr>


                                            </a>
                                        </div>
                                        <div class="date sessions-time">
                                            <div class="left cicon icon-sessions">
                                                <a rel="session" class="timeline" itemprop="duration"
                                                   href="/Timeline/sessions?ids=5132178" timezone="10">1 session, 7
                                                    hours total</a>
                                            </div>
                                            <div class="right timeline date-time cicon icon-session-time">
                                                <a rel="session" class="timeline date-time"
                                                   href="/Timeline/sessions?ids=5132178" timezone="10">
                                                    <abbr class="dtstart" itemprop="startDate">
                                                        8am
                                                    </abbr> - <abbr class="dtend" itemprop="endDate">
                                                        3pm
                                                    </abbr>

                                                </a>
                                            </div>
                                        </div>


                                        <div class="summary" itemprop="name">Summer Bushwalking Adventure Series</div>
                                    </div>
                                    <div class="class-item-info-r">

                                        <div class="left cicon icon-location location" itemprop="location">


                                            Ku-ring-ai-Chase National Park


                                        </div>


                                        <div class="right tutor cicon icon-person">

                                            <a title="View tutor profile" href="/tutor/16347" itemprop="performers"
                                               class="nyromodal">TheAdventureProject
                                            </a>


                                        </div>

                                    </div>
                                </div>
                                <div class="classAction">

                                    <div class="price">


                                        $139


                                        <span class="gst">
				 inc <span title="Goods and Services Tax">GST</span>
			</span>


                                        <span class="discount-price">/</span>
                                        <acronym title="Registered Pension Holders - Call office"
                                                 class="discount-price">
                                            $111.2
                                        </acronym>


                                    </div>

                                    ${enrolButton(5038512)}

                                    <div class="classStatus">


                                        There are places available


                                    </div>


                                    <!-- div class="courseEmail">
                                     <a href="">Email to a friend</a>
                                 </div-->
                                </div>


                                <div class="classDescription class-note-important">
                                    Summer Bush Walking Adventure Series #4 – Bobbin Head & Apple Tree Bay Kuring-gai
                                    Chase National Park – Sphinx to Mount Kuring-gai
                                </div>


                                <div class="bubbleInfo">
                                    <div class="tooltip_popup enrol_tooltip">
                                        <div class="arrow">
                                            <span></span>
                                        </div>
                                        <div class="bubble_top bubble_content">
					<span class="timing-display">

                            <span class="timing-weekday timing-Monday match-8 match-1 timing-no">Mo</span>

                            <span class="timing-weekday timing-Tuesday match-8 match-1 timing-no">Tu</span>

                            <span class="timing-weekday timing-Wednesday match-8 match-1 timing-no">We</span>

                            <span class="timing-weekday timing-Thursday match-8 match-1 timing-no">Th</span>

                            <span class="timing-weekday timing-Friday match-8 match-1 timing-no">Fr</span>

                            <span class="timing-weekend timing-Saturday match-9 match-6 timing-no">Sa</span>

                            <span class="timing-weekend timing-Sunday match-9 match-6 timing-yes">Su</span>

					</span>
                                            <span class="timing-display">
						<span class="timing-daytime match-10 timing-daytime-yes">
							<img src="/s/img/blank.png"/>
						</span>
						<span class="timing-evening match-10 timing-evening-no">
							<img src="/s/img/blank.png"/>
						</span>
					</span>
                                        </div>
                                        <div class="bubble_middle bubble_content">
                                            <div class="class-link">
                                                <a itemprop="url" href="/class/bushwalking.sydney-4"> View this
                                                    class... </a>
                                            </div>
                                            <div class="course-link">
                                                <a href="/course/bushwalking.sydney"> More about <em>Summer Bushwalking
                                                        Adventure Series</em>...
                                                </a>
                                            </div>
                                        </div>
                                        <div class="bubble_bottom"></div>
                                    </div>
                                </div>


                                <div id="sessions_for_class_5132178" class="sessions_for_class hidden classSessions">

                                    <table class="session-table">
                                        <thead>
                                        <tr>

                                            <th>When</th>

                                            <th>Time</th>

                                            <th>Where</th>

                                            <th>Session Notes</th>

                                        </tr>
                                        </thead>

                                        <tr class="tr-even">
                                            <td>
                                                Sun 09 Apr 2017
                                            </td>
                                            <td>
                                                8am
                                                - 3pm (UTC+10:00)

                                            </td>
                                            <td>


                                                <a href="/room/13055">


                                                    Bobbin Head &amp; Apple Tree Bay


                                                    - Ku-ring-ai-Chase National Park


                                                </a>


                                            </td>
                                            <td>

                                            </td>
                                        </tr>

                                    </table>

                                </div>


                            </div>


                        </div>


                        <p id="wl5021252" class="waiting-list-title">
                            <a href="/enrol/waitinglistform/5021252" class="actionLink">

                                If there isn't a class to suit you, please


                                join our waitlist and we will notify you when we have places available.</a>
                            <a class="abtn beta" href="/enrol/waitinglistform/5021252">Join Waitlist</a>
                        </p>


                        <div class="clearfix courseDescription">
                            <a id="course-description" name="course-description"></a>
                            <h3 class="course-sub-title">Course description</h3>
                            <h5 id="SummerSydneyBushwalkingAdventureSeries">Summer Sydney Bushwalking Adventure
                                Series</h5>
                            <p>Are you looking to get out of the city to stretch your legs, spend time in a stunning
                                national park with like-minded souls? If you are too busy to organise or even want to
                                think about the logistics the this Summer Bushwalking Adventure Series is your answer.
                                This course is created so that each trip takes you to an area of outstanding scenery
                                that is still within the outer suburbs of Sydney. We can guarantee these are all
                                challenging, inspiring and highly rewarding walks.</p>
                            <p>Each bush walking trip holds an individual experience as we are exploring not just the
                                beauty of the outdoors but discovering our own capabilities and inner strength. We make
                                sure our experiences are inclusive, with enough challenge to make you want to come back
                                for more. </p><h5 id="SummerBushwalkingAdventureSeries1RoyalNationalParkCoastalTrack">
                                Summer Bushwalking Adventure Series #1 – Royal National Park – Coastal Track </h5>
                            <p>*<strong>Date </strong>- Sunday 15th January <br/>*<strong>Destination</strong> - Royal
                                National Park – Coastal Track <br/>*<strong>Activity</strong> – Day Trek <br/>*<strong>Grade </strong>–
                                Moderate/strenuous</p>
                            <p>Starting from Garie Beach, we shall walk along the stunning coastal track approximately
                                15km to Bundeena. This is one of the most inspiring walks in NSW. The Royal National
                                Park is the oldest national park in Australia and a very special region. We shall make
                                time to have plenty of rest stops and photo opportunities along the way including
                                Wattamolla picnic area, a great spot for a refreshing swim. We can also pass by the epic
                                Wedding Cake Rock. When we reach Bundeena we shall catch the scenic ferry across to
                                Cronulla where we shall be collected for our trip back to Sydney. This walk is not to be
                                missed.</p><h5 id="SummerBushwalkingAdventureSeries2ExploringKuringaiChase">Summer
                                Bushwalking Adventure Series #2 – Exploring Ku-ring-ai Chase </h5>
                            <p>*<strong>Date</strong> - Sunday 12th February <br/>*<strong>Destination</strong> –
                                Ku-ring-ai-Chase National Park – Jerusalem Bay Track <br/>*<strong>Activity</strong> –
                                Day Trek <br/>*<strong>Grade</strong> - Moderate </p>
                            <p>This beautiful stretch of the Great North Walk is just over 13km starting from Cowan
                                station and finishing down at Brooklyn. The trail heads down to picturesque Jerusalem
                                Bay then up over a couple of ridges before circumnavigating Brooklyn Dam before heading
                                to the finish at Brooklyn at the Hawkesbury River where refreshments are available at
                                the local pub or café.</p><h5
                                    id="SummerBushwalkingAdventureSeries3RoyalNationalParkCoastalWalkPart2">Summer
                                Bushwalking Adventure Series #3 – Royal National Park – Coastal Walk Part 2 </h5>
                            <p>*<strong>Date</strong> – Sunday 12th March <br/>*<strong>Destination</strong> – Royal
                                National Park – Coastal Walk <br/>*<strong>Activity</strong> – Day Trek <br/>*<strong>Grade</strong>
                                - Moderate/strenuous </p>
                            <p>This is such a stunning coastal and national park it deserves to be included more than
                                once on our Summer schedule. We shall start at Otford for this 16.5km walk and meander
                                through and under the forest canopy before we emerge out and be greeted by expansive
                                coastal views. This walk passes by Garie Beach and continues to our destination of
                                Wattamolla Picnic Ground. The perfect spot for a swim, relax and chat about the day
                                before our pick up and return back to Sydney.</p><h5
                                    id="SummerBushWalkingAdventureSeries4BobbinHeadAppleTreeBay">Summer Bush Walking
                                Adventure Series #4 – Bobbin Head & Apple Tree Bay </h5>
                            <p>*<strong>Date</strong> – Sunday 9th April <br/>*<strong>Destination</strong> – Kuring-gai
                                Chase National Park – Sphinx to Mount Kuring-gai <br/>*<strong>Activity</strong> – Day
                                Trek <br/>*<strong>Grade</strong> – Moderate </p>
                            <p>Discover the beautiful areas within Kuring-gai Chase including Cowan Creek and Bobbin
                                Head over this 10.5km walk. Starting from the Sphinx memorial we wind our way down to
                                the water and through Bobbin Head and Apple Tree Bay. Plenty of time to stop, savour the
                                peace and quiet before the track winds back up the hill to Mt Kuring-gai station. </p>
                            <h5 id="Importantinformation">Important information:</h5>
                            <p>1. Please complete the Physical Activity Readiness Self Assessment Form attached below to
                                determine your own suitablity for these walks. If you have any questions, please chat
                                with us through the website or call 8752 7555 during business hours.</p>
                            <p>2. Participants will be sent a kit list of mandatory kit they shall need to bring on the
                                walks, e.g. including items such as a Basic First Aid Kit, suitable outdoor clothing and
                                equipment, sufficient water for the day etc. A kit check shall be carried out at the
                                start of each walk. Participants shall be required to bring some money for refreshments
                                if extra refreshments are required and if they are available on some of the walks. </p>
                            <p>3. Any public transport costs associated with the walks will be at the participants own
                                expense.</p>
                        </div>


                    </div>
                </div>


                <ul class="attachedImages">

                </ul>


                <h3>Additional information</h3>

                <ul class="attachments">

                    <li><a name="PAR-Q.2015"
                           href="https://s3-ap-southeast-2.amazonaws.com/ish-oncourse-scc/890cf9aa-804c-4571-b2a2-da08b17b0311">PAR-Q.2015</a>
                        <span class="attachmentSize">28 kB</span> <span class="attachmentType">pdf</span></li>

                </ul>


            </article>
            <aside id="sidebarRight" class="sidebar-right">


            </aside>
        </section>
        <footer id="footer" class="site-footer linear-border">


            <section class="container">

                <div class="footer-container footer-text">
                    <div class="clearfix">
                        <div class="footer-info">
                            <div class="copyrights">
                                © 2017 Sydney Community College
                            </div>
                            <ul class="social-links">
                                <li><a href="https://www.facebook.com/Sydney.Community.College/"><i
                                                class="sicon icon-facebook"></i></a></li>
                                <li><a href="https://twitter.com/SydneyCollege"><i class="sicon icon-twitter"></i></a>
                                </li>
                            </ul>
                        </div>

                        <div class="right-block">
                            <ul class="block-links">
                                <li><a href="/privacy">Privacy Policy</a></li>
                                <li><a href="/terms">Terms &amp; Conditions</a></li>
                            </ul>
                            <div class="footer-logo">
                                <a title="ish Oncourse" target="_blank" class="popup"
                                   href="http://www.ish.com.au/oncourse">
                                    <img alt="powered by ish onCourse" height="9" width="102"
                                         src="/s/img/poweredby.png"/>
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="footer-logo">
                        <a title="ish Oncourse" target="_blank" class="popup" href="http://www.ish.com.au/oncourse">
                            <img alt="powered by ish onCourse" height="9" width="102" src="/s/img/poweredby.png"/>
                        </a>
                    </div>
                </div>

            </section>
        </footer>
    </section>
    <div style="display: none; height: 500px" id="overlay"></div>
    <div style="visibility: hidden;" id="timeline-wrap">
        <div id="timeline"></div>
    </div>
</section>
</body>
</html>
`);
  console.log("courses.html generated");
}

