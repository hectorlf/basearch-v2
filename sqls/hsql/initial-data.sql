-- Table: languages
insert into languages values (1,'es','ES',null);
insert into languages values (2,'en','UK',null);
insert into languages values (3,'en','US',null);

-- Table: users
insert into users values (1,'admin','$2a$10$fdCTnC8xIL5HhvFKJIGDiecG91buB6Lj9IB/US2xqxFiKhCcMCXBq', true, 1);
insert into users values (2,'user','$2a$10$4qzMEbvFWnBPqxWLzFLGs.Mir//KKkvoorx6g47ogxxxpprvxMnse', true, 2);
insert into users values (3,'disabled','$2a$10$W/D92o1yKHHkOvWsLo50jeNOPT2bCaZq3H3V.4gZ1B4.4ROyvSIjm', false, 3);
insert into users values (4,'test','test', true, 1);

-- Table: authorities
insert into authorities values (1,'admin','ROLE_ADMIN');
insert into authorities values (2,'user','ROLE_USER');
insert into authorities values (3,'disabled','ROLE_ADMIN');
