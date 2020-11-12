#Some lines of SQL to prevent replication from happening
#This is likely not effective after the release of Dingo - onCourse 2.0
UPDATE Preference SET valuestring=NULL where name LIKE 'services.username'
UPDATE Preference SET valuestring=NULL where name LIKE 'services.password'
UPDATE Preference SET valuestring='false' where name LIKE 'services.authenticated'