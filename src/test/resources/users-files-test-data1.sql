INSERT INTO users
(username,
 password,
 role)
VALUES
    ('login1',
     'password1',
     'ROLE_USER');
SET @user_id1 = LAST_INSERT_ID();
INSERT INTO files
(filename,
 original_filename,
 content_type,
 size,
 file,
 user_id)
VALUES
    ('file name1',
     'original file name1',
     'content type',
     10,
     'test1',
     @user_id1);
INSERT INTO files
(filename,
 original_filename,
 content_type,
 size,
 file,
 user_id)
VALUES
    ('file name2',
     'original file name2',
     'content type',
     20,
     'test2',
     @user_id1);