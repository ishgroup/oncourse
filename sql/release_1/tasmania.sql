UPDATE WebContent set content = '<ul class="formating-option"><li><a href="#"><img src="/s/img/font_size.png" /></a></li><li><a href="#"><img src="/s/img/print.png" /></a></li></ul>
<h1>Courses open for enrolment or some such text</h1><p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p><div id="nav">{tags name:"/Subjects/Leisure" maxLevels:"2"}</div>' WHERE id = 1298 AND webSiteId = 15;

UPDATE WebContent set content_textile = content where id = 1298 AND webSiteId = 15;

UPDATE WebContentVisibility set regionKey = 'content' WHERE WebContentId = 1298;

DELETE FROM WebContentVisibility WHERE WebContentId = 46 and regionKey = 'footer';
DELETE FROM WebContentVisibility WHERE WebContentId = 94 and regionKey = 'header';
DELETE FROM WebContentVisibility WHERE WebContentId = 122 and regionKey = 'left';
DELETE FROM WebContentVisibility WHERE WebContentId = 154 and regionKey = 'left';
DELETE FROM WebContentVisibility WHERE WebContentId = 638 and regionKey = 'content';
DELETE FROM WebContentVisibility WHERE WebContentId = 639 and regionKey = 'right';
DELETE FROM WebContentVisibility WHERE WebContentId = 645 and regionKey = 'header';


INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES (54,654,0,'left');
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES (54,652,1,'left');
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES (54,658,2,'left');
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES (54,653,3,'left');
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES (54,657,4,'left');
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES (54,655,5,'left');
