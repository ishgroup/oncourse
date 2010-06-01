select 'College before';
select
      c.id
    , c.collegeKey
from College c;

update College c
    left join WebSite s on s.collegeId = c.id
    set c.collegeKey = s.code;

select 'College after';
select
      c.id
    , c.collegeKey
from College c;



select 'WebSite before';
select
      s.id
    , s.siteKey
from WebSite s;

update WebSite s
    left join CollegeDomain cd on cd.webSiteID = s.id
    set s.siteKey = cd.subsiteCode;

select 'WebSite after';
select
      s.id
    , s.siteKey
from WebSite s;



select 'WebNodeType before';
select
      w.id
    , w.templateKey
from WebNodeType w;

update WebNodeType wnt
    left join WebTheme wt on wt.id = wnt.defaultWebThemeID 
    set wnt.templateKey = wt.themeKey;

select 'WebNodeType after';
select
      w.id
    , w.templateKey
from WebNodeType w;



insert into WebNodeContent (
      angelId
    , webNodeId
    , regionKey
    , created
    , isDeleted
    , modified
    , content
    , content_textile)
select
      wb.angelId
    , wb.id
    , 'content'
    , wb.created
    , wb.isDeleted
    , wb.modified
    , wb.content
    , wb.content_textile
from WebNode wb