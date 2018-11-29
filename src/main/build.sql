create database if not exists   spectrum;

use spectrum;

CREATE TABLE  if not exists  `source_data_node`(
   `target_table_name` string,
   `target_table_comment` string,
   `target_column_name` string,
   `target_column_comment` string,
   `source_table_name` string,
   `source_table_comment` string,
   `source_column_name` string,
   `source_column_comment` string,
   `store_procedure` string)
 ROW FORMAT SERDE
   'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
 STORED AS INPUTFORMAT
   'org.apache.hadoop.mapred.TextInputFormat'
 OUTPUTFORMAT
   'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat' ;

 CREATE TABLE if not exists  `process_relation`(
   `table_name` string,
   `store_procedure` string,
   `comment` string,
   `columns` string,
   `source_tables` string,
   `after_tables` string,
   `map_json` string)
 ROW FORMAT SERDE
   'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
 STORED AS INPUTFORMAT
   'org.apache.hadoop.mapred.TextInputFormat'
 OUTPUTFORMAT
   'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat' ;