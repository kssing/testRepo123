use dms;

LOCK TABLES `document_type` WRITE;
INSERT INTO `document_type` (doc_type_id, doc_type_name, category_id, max_documents) VALUES (31,'Bank Statements',1,32767),(32,'Bank Loan Statements',2,32767),(33,'Financial Statements',2,32767);
UNLOCK TABLES;



LOCK TABLES `accessible_to` WRITE;
INSERT INTO `accessible_to` (system_id, doc_type_id) VALUES (1,31),(1,32),(1,33);
UNLOCK TABLES;



DELETE FROM `dms`.`digital_info_mapping` WHERE `info_id`='4';
DELETE FROM `dms`.`digital_info_mapping` WHERE `info_id`='5';



use dms;
INSERT INTO `digital_info_mapping` (`key`, `type`, `doc_type_id`) VALUES ('From Year','year',33),('To Year','year',33),('From Date','date',31),('To Date','date',31),('Institute Name','Text',31),('Password','Text',31),('Institute Id','Number',31);

