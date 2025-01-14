ALTER TABLE `dms`.`document_type` 
ADD COLUMN `max_documents` INT(10) NULL AFTER `category_id`;

UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='1';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='2';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='3';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='4';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='5';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='6';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='7';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='8';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='9';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='10';
UPDATE `dms`.`document_type` SET `max_documents`='1' WHERE `doc_type_id`='11';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='12';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='13';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='14';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='15';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='16';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='17';
UPDATE `dms`.`document_type` SET `max_documents`='1' WHERE `doc_type_id`='18';
UPDATE `dms`.`document_type` SET `max_documents`='1' WHERE `doc_type_id`='19';
UPDATE `dms`.`document_type` SET `max_documents`='1' WHERE `doc_type_id`='20';
UPDATE `dms`.`document_type` SET `max_documents`='1' WHERE `doc_type_id`='21';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='22';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='23';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='24';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='25';
UPDATE `dms`.`document_type` SET `max_documents`='32767' WHERE `doc_type_id`='26';

