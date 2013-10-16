fk_check_input_values(0);
-- dbdump: dumping datasource "localhost:1111", username=dba
-- tablequalifier=NULL  tableowner=NULL  tablename=is given, one or more  tabletype=NULL
-- Connected to datasource "OpenLink Virtuoso", Driver v. 06.01.3127 OpenLink Virtuoso ODBC Driver.
-- get_all_tables: tablepattern="db.ODCS.%",11
-- Definitions of 27 tables were read in.
-- SELECT * FROM DB.ODCS.DPU_INSTANCE
FOREACH HEXADECIMAL BLOB INSERT INTO DB.ODCS.DPU_INSTANCE(id,name,use_dpu_description,description,tool_tip,configuration,dpu_id) VALUES(1,'SPARQL Extractor',0,'','',?,1);
3C6F626A6563742D73747265616D3E0A20203C637A2E63756E692E6D66662E78
72672E6F6463732E657874726163746F722E7264662E52444645787472616374
6F72436F6E6669673E0A202020203C53504152514C5F5F656E64706F696E743E
687474703A2F2F646270656469612E6F72672F73706172716C3C2F5350415251
4C5F5F656E64706F696E743E0A202020203C486F73745F5F6E616D653E3C2F48
6F73745F5F6E616D653E0A202020203C50617373776F72643E3C2F5061737377
6F72643E0A202020203C47726170687355726920636C6173733D226C696E6B65
642D6C697374223E0A2020202020203C737472696E673E687474703A2F2F6462
70656469612E6F72672F3C2F737472696E673E0A202020203C2F477261706873
5572693E0A202020203C53504152514C5F5F71756572793E434F4E5354525543
54207B266C743B687474703A2F2F646270656469612E6F72672F7265736F7572
63652F5072616775652667743B203F70203F6F7D207768657265207B266C743B
687474703A2F2F646270656469612E6F72672F7265736F757263652F50726167
75652667743B203F70203F6F207D204C494D4954203130303C2F53504152514C
5F5F71756572793E0A202020203C457874726163744661696C3E66616C73653C
2F457874726163744661696C3E0A202020203C55736553746174697374696361
6C48616E646C65723E66616C73653C2F557365537461746973746963616C4861
6E646C65723E0A20203C2F637A2E63756E692E6D66662E7872672E6F6463732E
657874726163746F722E7264662E524446457874726163746F72436F6E666967
3E0A3C2F6F626A6563742D73747265616D3E
END
FOREACH HEXADECIMAL BLOB INSERT INTO DB.ODCS.DPU_INSTANCE(id,name,use_dpu_description,description,tool_tip,configuration,dpu_id) VALUES(2,'RDF File Loader',0,'','',?,5);
3C6F626A6563742D73747265616D3E0A20203C637A2E63756E692E6D66662E78
72672E6F6463732E6C6F616465722E66696C652E46696C654C6F61646572436F
6E6669673E0A202020203C46696C65506174683E2F746D702F64627065646961
2E7264663C2F46696C65506174683E0A202020203C52444646696C65466F726D
61743E524446584D4C3C2F52444646696C65466F726D61743E0A202020203C44
6966664E616D653E66616C73653C2F446966664E616D653E0A20203C2F637A2E
63756E692E6D66662E7872672E6F6463732E6C6F616465722E66696C652E4669
6C654C6F61646572436F6E6669673E0A3C2F6F626A6563742D73747265616D3E
END



-- Table DB.ODCS.DPU_INSTANCE 2 rows output.
-- SELECT * FROM DB.ODCS.DPU_TEMPLATE
INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(1,'SPARQL Extractor',0,'','<object-stream> <cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig> <SPARQL__endpoint>http://</SPARQL__endpoint> <Host__name></Host__name> <Password></Password> <GraphsUri class="linked-list"> <string></string> </GraphsUri> <SPARQL__query></SPARQL__query> <ExtractFail>true</ExtractFail> <UseStatisticalHandler>true</UseStatisticalHandler> </cz.cuni.mff.xrg.odcs.extractor.rdf.RDFExtractorConfig> </object-stream>',NULL,1,1,0,'RDF_extractor','RDF_extractor-1.0.0.jar','Extracts RDF data.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(2,'RDF File Extractor',0,'','<object-stream> <cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig> <Path></Path> <FileSuffix></FileSuffix> <RDFFormatValue>AUTO</RDFFormatValue> <fileExtractType>PATH_TO_FILE</fileExtractType> <OnlyThisSuffix>false</OnlyThisSuffix> <UseStatisticalHandler>false</UseStatisticalHandler> </cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig> </object-stream>',NULL,1,1,0,'File_extractor','File_extractor-1.0.0.jar','Extracts RDF data from a file.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(3,'SPARQL Transformer',0,'','<object-stream> <cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig> <SPARQL__Update__Query></SPARQL__Update__Query> <isConstructType>false</isConstructType> </cz.cuni.mff.xrg.odcs.transformer.SPARQL.SPARQLTransformerConfig> </object-stream>',NULL,1,1,1,'SPARQL_transformer','SPARQL_transformer-1.0.0.jar','SPARQL Transformer.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(4,'SPARQL Loader',0,'',' <object-stream> <cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig> <SPARQL__endpoint>http://</SPARQL__endpoint> <Host__name></Host__name> <Password></Password> <GraphsUri class="linked-list"> <string></string> </GraphsUri> <graphOption>OVERRIDE</graphOption> <insertOption>STOP_WHEN_BAD_PART</insertOption> <chunkSize>100</chunkSize> </cz.cuni.mff.xrg.odcs.loader.rdf.RDFLoaderConfig> </object-stream>',NULL,1,1,2,'RDF_loader','RDF_loader-1.0.0.jar','Loads RDF data.');

INSERT INTO DB.ODCS.DPU_TEMPLATE(id,name,use_dpu_description,description,configuration,parent_id,user_id,visibility,type,jar_directory,jar_name,jar_description) VALUES(5,'RDF File Loader',0,'','<object-stream> <cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig> <FilePath></FilePath> <RDFFileFormat>AUTO</RDFFileFormat> <DiffName>false</DiffName> </cz.cuni.mff.xrg.odcs.loader.file.FileLoaderConfig> </object-stream>',NULL,1,1,2,'File_loader','File_loader-1.0.0.jar','Loads RDF data into file.');

-- Table DB.ODCS.DPU_TEMPLATE 5 rows output.
-- SELECT * FROM DB.ODCS.EXEC_CONTEXT_DPU
-- Table DB.ODCS.EXEC_CONTEXT_DPU 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_CONTEXT_PIPELINE
-- Table DB.ODCS.EXEC_CONTEXT_PIPELINE 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_CONTEXT_PROCCONTEXT
-- Table DB.ODCS.EXEC_CONTEXT_PROCCONTEXT 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_DATAUNIT_INFO
-- Table DB.ODCS.EXEC_DATAUNIT_INFO 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_PIPELINE
-- Table DB.ODCS.EXEC_PIPELINE 0 rows output.
-- Table DB.ODCS.EXEC_RECORD has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- SELECT * FROM DB.ODCS.EXEC_RECORD
-- Table DB.ODCS.EXEC_RECORD has more than one blob column.
-- The column full_message of type LONG VARCHAR might not get properly inserted.
-- Table DB.ODCS.EXEC_RECORD 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_SCHEDULE
-- Table DB.ODCS.EXEC_SCHEDULE 0 rows output.
-- SELECT * FROM DB.ODCS.EXEC_SCHEDULE_AFTER
-- Table DB.ODCS.EXEC_SCHEDULE_AFTER 0 rows output.
-- SELECT * FROM DB.ODCS.LOGGING_EVENT
-- Table DB.ODCS.LOGGING_EVENT 0 rows output.
-- SELECT * FROM DB.ODCS.LOGGING_EVENT_EXCEPTION
-- Table DB.ODCS.LOGGING_EVENT_EXCEPTION 0 rows output.
-- SELECT * FROM DB.ODCS.LOGGING_EVENT_PROPERTY
-- Table DB.ODCS.LOGGING_EVENT_PROPERTY 0 rows output.
-- SELECT * FROM DB.ODCS.PPL_EDGE
INSERT INTO DB.ODCS.PPL_EDGE(id,graph_id,node_from_id,node_to_id,data_unit_name) VALUES(2,1,1,2,'output -> input;');
-- Table DB.ODCS.PPL_EDGE 1 rows output.
-- SELECT * FROM DB.ODCS.PPL_GRAPH
INSERT INTO DB.ODCS.PPL_GRAPH(id,pipeline_id) VALUES(1,1);
-- Table DB.ODCS.PPL_GRAPH 1 rows output.
-- SELECT * FROM DB.ODCS.PPL_MODEL
INSERT INTO DB.ODCS.PPL_MODEL(id,name,description,user_id) VALUES(1,'DBpedia','Loads 100 triples from DBpedia.',2);
-- Table DB.ODCS.PPL_MODEL 1 rows output.
-- SELECT * FROM DB.ODCS.PPL_NODE
INSERT INTO DB.ODCS.PPL_NODE(id,graph_id,instance_id,position_id) VALUES(1,1,1,1);
INSERT INTO DB.ODCS.PPL_NODE(id,graph_id,instance_id,position_id) VALUES(2,1,2,2);
-- Table DB.ODCS.PPL_NODE 2 rows output.
-- SELECT * FROM DB.ODCS.PPL_POSITION
INSERT INTO DB.ODCS.PPL_POSITION(id,pos_x,pos_y) VALUES(1,138,52);
INSERT INTO DB.ODCS.PPL_POSITION(id,pos_x,pos_y) VALUES(2,487,132);
-- Table DB.ODCS.PPL_POSITION 2 rows output.
-- SELECT * FROM DB.ODCS.PPL_PPL_CONFLICTS
-- Table DB.ODCS.PPL_PPL_CONFLICTS 0 rows output.
-- SELECT * FROM DB.ODCS.RDF_PREFIX
-- Table DB.ODCS.RDF_PREFIX 0 rows output.
-- SELECT * FROM DB.ODCS.SCH_EMAIL
INSERT INTO DB.ODCS.SCH_EMAIL(id,e_user,e_domain) VALUES(1,'admin','example.com');
INSERT INTO DB.ODCS.SCH_EMAIL(id,e_user,e_domain) VALUES(2,'user','example.com');
-- Table DB.ODCS.SCH_EMAIL 2 rows output.
-- SELECT * FROM DB.ODCS.SCH_SCH_NOTIFICATION
-- Table DB.ODCS.SCH_SCH_NOTIFICATION 0 rows output.
-- SELECT * FROM DB.ODCS.SCH_SCH_NOTIFICATION_EMAIL
-- Table DB.ODCS.SCH_SCH_NOTIFICATION_EMAIL 0 rows output.
-- SELECT * FROM DB.ODCS.SCH_USR_NOTIFICATION
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION(id,user_id,type_success,type_error) VALUES(1,1,1,1);
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION(id,user_id,type_success,type_error) VALUES(2,2,1,1);
-- Table DB.ODCS.SCH_USR_NOTIFICATION 2 rows output.
-- SELECT * FROM DB.ODCS.SCH_USR_NOTIFICATION_EMAIL
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id) VALUES(1,1);
INSERT INTO DB.ODCS.SCH_USR_NOTIFICATION_EMAIL(notification_id,email_id) VALUES(2,2);
-- Table DB.ODCS.SCH_USR_NOTIFICATION_EMAIL 2 rows output.
-- SELECT * FROM DB.ODCS.USR_USER
INSERT INTO DB.ODCS.USR_USER(id,username,email_id,u_password,full_name) VALUES(1,'admin',1,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','John Admin');
INSERT INTO DB.ODCS.USR_USER(id,username,email_id,u_password,full_name) VALUES(2,'user',2,'10:34dbe217a123a1501be647832c77571bd0af1c8b584be30404157da1111499b9:f09771bb5a73b35d6d8cd8b5dfb0cf26bf58a71f6d3f4c1a8c92e33fb263aaff','John User');
-- Table DB.ODCS.USR_USER 2 rows output.
-- SELECT * FROM DB.ODCS.USR_USER_ROLE
INSERT INTO DB.ODCS.USR_USER_ROLE(user_id,role_id) VALUES(1,0);
INSERT INTO DB.ODCS.USR_USER_ROLE(user_id,role_id) VALUES(1,1);
INSERT INTO DB.ODCS.USR_USER_ROLE(user_id,role_id) VALUES(2,0);
-- Table DB.ODCS.USR_USER_ROLE 3 rows output.


fk_check_input_values(1);
