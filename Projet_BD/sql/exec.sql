set autocommit off;
set transaction isolation level serializable;
@delete_all.sql
@chessGame.sql
@init_data.sql
--@test1.sql
commit;
