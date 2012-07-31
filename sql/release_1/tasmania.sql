-- Home Page content
UPDATE WebContent set content_textile = 'h1. Courses open for enrolment

LearnXpress offers a range of course in leisure and lifestyle activities as well as courses to learn new skills -for work and for life - for all Tasmanians.

Discover your hidden talents, meet new people, unwind, get fit, be creative or pursue a passion by enrolling in one of our popular courses today!

{tags hideTopLevel:"true" maxLevels:"2"}' WHERE id = 1298 AND webSiteId = 15;

UPDATE WebContent set content = '<h1 id="Coursesopenforenrolment">Courses open for enrolment</h1>
<p>LearnXpress offers a range of course in leisure and lifestyle activities as well as courses to learn new 
skills - for work and for life &ndash; for all Tasmanians.</p><p>Discover your hidden talents, meet new people, unwind, get fit, be creative or pursue a passion by 
enrolling in one of our popular courses today!</p>
{tags hideTopLevel:"true" maxLevels:"2"}'
where id = 1298 AND webSiteId = 15;

UPDATE WebContentVisibility set regionKey = 'content' WHERE WebContentId = 1298;

-- Content not needed
DELETE FROM WebContentVisibility WHERE WebContentId = 46 and regionKey = 'footer';
DELETE FROM WebContentVisibility WHERE WebContentId = 94 and regionKey = 'header';
DELETE FROM WebContentVisibility WHERE WebContentId = 122 and regionKey = 'left';
DELETE FROM WebContentVisibility WHERE WebContentId = 154 and regionKey = 'left';
DELETE FROM WebContentVisibility WHERE WebContentId = 638 and regionKey = 'content';
DELETE FROM WebContentVisibility WHERE WebContentId = 639 and regionKey = 'right';
DELETE FROM WebContentVisibility WHERE WebContentId = 645 and regionKey = 'header';

-- Layout for the page theme
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES SELECT id, '654',0,'left' FROM WebNodeType where name = 'page' AND webSiteId = 15;
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES SELECT id, '652',1,'left' FROM WebNodeType where name = 'page' AND webSiteId = 15;
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES SELECT id, '658',2,'left' FROM WebNodeType where name = 'page' AND webSiteId = 15;
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES SELECT id, '653',3,'left' FROM WebNodeType where name = 'page' AND webSiteId = 15;
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES SELECT id, '657',4,'left' FROM WebNodeType where name = 'page' AND webSiteId = 15;
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES SELECT id, '655',5,'left' FROM WebNodeType where name = 'page' AND webSiteId = 15;

-- Page theme footer
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) VALUES SELECT id, '660',0,'footer' FROM WebNodeType where name = 'page' AND webSiteId = 15;

-- This should create the new theme and add the homepage to it.

-- Creating the new theme HomePage
INSERT into WebNodeType (webSiteId, name, layoutKey, created, modified) VALUES (15, 'HomePage', 'default',CURDATE(), CURDATE());

-- Left position 
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '654',0,'left' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '652',1,'left' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '658',2,'left' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '653',3,'left' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '657',4,'left' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '655',5,'left' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

-- Header position
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '659',0,'header' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

-- Footer position
INSERT INTO WebContentVisibility (WebNodeTypeId,WebContentId,weight,regionKey) SELECT id, '660',0,'footer' FROM WebNodeType where name = 'HomePage' AND webSiteId = 15;

-- Changing HomePage theme the easy way
UPDATE WebNode SET webNodeTypeId = (SELECT id from WebNodeType WHERE webSiteId = 15 AND name = 'HomePage') WHERE name = 'Home Page' AND webSiteId = 15;
