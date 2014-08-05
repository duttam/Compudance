insert into company values (1,'dfyoung','1235 WestLakes Drive',null,'Berwyn','Pennsylvania','PA','19312','manas.dutta@dfyoung.com','6105702620','6107250570','dfyoung');
insert into company values (2,'superior technology solutions','150 south pearl street',null,'Pearl River','New York','NY','10965','manas.dutta@superiortechnology.com','8457325445','8457325445','superior');

insert into USERS values ('john','d561e82aa3e27d12223218304d34a673bf7315c5',1,1);
insert into USERS values ('user','7a2de0049909e5a4fe814cb5cdb85819babfa175',1,2);

insert into GROUPS values (1,'ROLE_ADMIN');
insert into GROUPS values (2,'ROLE_USER');

insert into GROUP_AUTHORITIES values (1,'ROLE_ACCESS_ADMIN');
insert into GROUP_AUTHORITIES values (1,'ROLE_ACCESS_SECURED');
insert into GROUP_AUTHORITIES values (2,'ROLE_ACCESS_SECURED');

insert into GROUP_MEMBERS values (1,'john',1,1);
insert into GROUP_MEMBERS values (2,'john',2,1);
insert into GROUP_MEMBERS values (3,'user',2,2);