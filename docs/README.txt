Creating Database:

Execute following commands in order:
	cd ~
	mkdir Database
	cd Database
	/usr/lib/postgresql/9.5/bin/initdb -D dbis

In "dbis/postgresql.conf",
	1) uncomment "#port = 5432"
	2) Change #unix_socket_directories = '/var/run/postgresql' to unix_socket_directories = '/xxx/Database' where xxx is the full path of your home directory

Using pgadmin3, populate the database:
1.To create tables, execute 'DDL_Tables.sql' (Use 'DDL_drop.sql' if you already have the Database schema tables present)
2.To insert tuples into relations, execute 'DDL_insert.sql'
