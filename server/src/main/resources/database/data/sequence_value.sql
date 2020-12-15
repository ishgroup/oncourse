CREATE PROCEDURE sequence_value (IN tname VARCHAR(255), IN batchsize INT)
BEGIN
    DECLARE exit HANDLER FOR sqlexception
        BEGIN
            ROLLBACK;
        END;
    DECLARE exit HANDLER FOR sqlwarning
        BEGIN
            ROLLBACK;
        END;
    START TRANSACTION;
    -- UPDATE will lock tname record util transaction end
    UPDATE SequenceSupport SET nextId = nextId + batchsize WHERE tableName = tname;
    SELECT nextId FROM SequenceSupport WHERE tableName = tname;
    COMMIT;
END