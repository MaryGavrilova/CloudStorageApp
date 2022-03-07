INSERT INTO users
(username,
 password,
 role)
VALUES
    ('login5',
     'password5',
     'ROLE_USER');
SET @user_id5 = LAST_INSERT_ID();
INSERT INTO files
(filename,
 original_filename,
 content_type,
 size,
 file,
 user_id)
VALUES
    ('file name',
     'original file name',
     'content type',
     10,
     'test',
     @user_id5);