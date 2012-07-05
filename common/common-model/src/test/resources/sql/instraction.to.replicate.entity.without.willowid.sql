select 'INSERT INTO Instruction( collegeId, created, modified, message )
VALUES ( '+REQUIRED_COLLEGE_ID+', now( ) , now( ) , ''queue:'+ENTITY_NAME+':',id,''');'
FROM ENTITY_TABLE_NAME where willowid is null;