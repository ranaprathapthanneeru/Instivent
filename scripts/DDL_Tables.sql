create table student(
	id char(9),
	first_name varchar(15),
	last_name varchar(15),
	primary key (id)
);

create table password(
	id char(9),
	password varchar(64),
	primary key (id),
	foreign key (id) references student
);

create table club(
	id varchar(5),
	name varchar(20),
	focus varchar(20),
	img_url varchar(300),
	primary key (id)
);

create table member
(
s_id  char(9),
c_id varchar(5),
role char(1),
primary key(s_id,c_id),
foreign key(s_id) references student,
foreign key(c_id) references club
);

create table event(
	id varchar(6),
	name varchar(64),
	image_url varchar(300),
	event_date timestamp,
	num_participants integer,
	created_time timestamp,
	primary key(id)
);


create table organises(
	ev_id varchar(6),
	c_id varchar(5),
	primary key (c_id,ev_id),
	foreign key (c_id) references club,
	foreign key (ev_id) references event
);

create table likes(
	s_id varchar(9),
	ev_id varchar(6),
	primary key (s_id,ev_id),
	foreign key (s_id) references student,
	foreign key (ev_id) references event
);

create table comments(
	comment_id integer,
	s_id varchar(9),
	ev_id varchar(6),
	comment varchar(64),
	comment_time timestamp,
	primary key (comment_id),
	foreign key (s_id) references student,
	foreign key (ev_id) references event
);


		create table groups(
			g_id varchar(6),
			ev_id varchar(6),
			name varchar(20),
			primary key(g_id),
			foreign key (ev_id) references event
		);

		create table participates(
			s_id	varchar(9),
			g_id	varchar(6),
			primary key (s_id,g_id),
			foreign key (s_id) references student,
			foreign key (g_id) references groups
		);


create table venue(
	v_id varchar(6),
	room varchar(20),
	primary key (v_id)
);

create table event_venue(
	ev_id varchar(6),
	v_id varchar(6),
	primary key (ev_id,v_id),
	foreign key (ev_id) references event,
	foreign key (v_id) references venue
);