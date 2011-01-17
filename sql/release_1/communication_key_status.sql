alter table College add column communication_key_status VARCHAR(20);
update College set communication_key_status='VALID';