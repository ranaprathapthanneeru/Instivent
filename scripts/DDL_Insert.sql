insert into student values('140050051','Jhon','Cena');
insert into student values('140050055','Jesus','Christ');


insert into password values('140050051','140050051');
insert into password values('140050055','140050055');


insert into club values('1','Electronics Club','Electrical','cb_1.jpg');
insert into club values('2','Robotics Club','Mechanical','cb_2.jpg');
insert into club values('3','WnCC','Computer Science','cb_3.jpg');
insert into club values('4','Aeromodelling Club ','Aerospace','cb_4.jpg');
insert into club values('5','Photography Club','Photos','cb_5.jpg');
insert into club values('6','Media Cell','Research','cb_6.jpg');
insert into club values('7','InSync','Dance','cb_7.jpg');
insert into club values('8','Maths nd Physics','Subjects','cb_8.jpg');


insert into member  values('140050051','1','A');
insert into member  values('140050051','2','U');
insert into member  values('140050051','3','U');
insert into member  values('140050051','4','U');
insert into member  values('140050051','5','U');
insert into member  values('140050051','6','A');
insert into member  values('140050051','7','U');
insert into member  values('140050051','8','U');


insert into member  values('140050055','1','U');
insert into member  values('140050055','2','A');
insert into member  values('140050055','3','U');
insert into member  values('140050055','4','U');
insert into member  values('140050055','5','U');
insert into member  values('140050055','6','U');
insert into member  values('140050055','7','U');
insert into member  values('140050055','8','A');



insert into event values('1','event1','ev_1.jpg',now()+'2 days'+'2 hours',2,now());
insert into event values('2','event2','ev_2.jpg',now()+'4 days'+'3 hours',2,now());
insert into event values('3','event3','ev_3.jpg',now()+'3 days'+'6 hours',2,now());
insert into event values('4','event4','ev_4.jpg',now()+'2 days'+'2 hours',2,now());
insert into event values('5','event5','ev_5.jpg',now()+'4 days'+'3 hours',2,now());
insert into event values('6','event6','ev_6.jpg',now()+'3 days'+'6 hours',2,now());
insert into event values('7','event7','ev_7.jpg',now()+'2 days'+'2 hours',2,now());



insert into organises values ('1','1');
insert into organises values ('2','1');
insert into organises values ('3','1');
insert into organises values ('4','2');
insert into organises values ('5','2');
insert into organises values ('6','2');
insert into organises values ('7','2');



insert into venue  values ('1','LA-101');
insert into venue  values ('2','LA-102');
insert into venue  values ('3','LC-101');
insert into venue  values ('4','LH-101');

insert into event_venue  values ('1','4');
insert into event_venue  values ('2','1');
insert into event_venue  values ('3','3');
insert into event_venue  values ('4','4');
insert into event_venue  values ('5','1');
insert into event_venue  values ('6','3');
insert into event_venue  values ('7','4');




insert into comments values (1,'140050051','1','cool event bro',now());
insert into comments values (2,'140050055','1','winter is coming',now()+'3 hours');



insert into groups values ('1','1','group1');
insert into groups values ('2','2','group2');
insert into groups values ('3','3','group3');


insert into participates values ('140050051','1');
insert into participates values ('140050051','2');
insert into participates values ('140050051','3');
insert into participates values ('140050055','1');


