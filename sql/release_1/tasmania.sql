UPDATE WebContent set content = 'h1. Courses open for enrolment

LearnXpress offers a range of course in leisure and lifestyle activities as well as courses to learn new skills -
for work and for life - for all Tasmanians.

Discover your hidden talents, meet new people, unwind, get fit, be creative or pursue a passion by enrolling in one of 
our popular courses today!

{tags hideTopLevel:"true" maxLevels:"2"}';

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
