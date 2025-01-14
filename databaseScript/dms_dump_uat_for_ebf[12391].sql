-- MySQL dump 10.13  Distrib 5.7.9, for osx10.9 (x86_64)
--
-- Host: 192.168.1.142    Database: dms
-- ------------------------------------------------------
-- Server version	5.6.27-0ubuntu0.14.04.1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accessible_to`
--

CREATE SCHEMA `dms` ;

use `dms`;


--
-- Table structure for table `system_detail`
--

DROP TABLE IF EXISTS `system_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_detail` (
  `system_id` int(11) NOT NULL AUTO_INCREMENT,
  `system_name` varchar(100) DEFAULT NULL,
  `userid` varchar(65) DEFAULT NULL,
  `password` varchar(65) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modified_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`system_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `document_type`
--

DROP TABLE IF EXISTS `document_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_type` (
  `doc_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_type_name` varchar(100) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `category_id` int(11) NOT NULL,
  `max_documents` int(10) DEFAULT NULL,
  PRIMARY KEY (`doc_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS `accessible_to`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accessible_to` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `system_id` int(11) DEFAULT NULL,
  `doc_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `doc_type_id_idx` (`doc_type_id`),
  KEY `system_id_idx` (`system_id`),
  CONSTRAINT `fk_system_id` FOREIGN KEY (`system_id`) REFERENCES `system_detail` (`system_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fky_doctype_id` FOREIGN KEY (`doc_type_id`) REFERENCES `document_type` (`doc_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `applicable_to`
--

DROP TABLE IF EXISTS `applicable_to`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `applicable_to` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companytype` varchar(45) DEFAULT NULL,
  `doc_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_doc_type_id_idx` (`doc_type_id`),
  KEY `doc_type_id_idx` (`doc_type_id`),
  CONSTRAINT `doc_type_id` FOREIGN KEY (`doc_type_id`) REFERENCES `document_type` (`doc_type_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `documents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_id` int(11) NOT NULL,
  `sme_id` varchar(45) DEFAULT NULL,
  `system_id` int(11) DEFAULT NULL,
  `doc_type_id` int(11) NOT NULL,
  `version_no` int(11) DEFAULT NULL,
  `valid_till` date DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `modified_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `doc_type_id_idx` (`doc_type_id`),
  KEY `doc_id` (`doc_id`),
  CONSTRAINT `fkey_doc_type_id` FOREIGN KEY (`doc_type_id`) REFERENCES `document_type` (`doc_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=475 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `category_detail`
--

DROP TABLE IF EXISTS `category_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(145) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `modified_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `parent_category` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_idx` (`parent_category`),
  CONSTRAINT `id` FOREIGN KEY (`parent_category`) REFERENCES `category_detail` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sme_id` varchar(100) DEFAULT NULL,
  `doc_type_id` int(11) NOT NULL,
  `document_id` int(11) NOT NULL,
  `system_id` int(11) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `commented_by` varchar(100) DEFAULT NULL,
  `on_action` varchar(45) DEFAULT NULL,
  `on_version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_doc_id_idx` (`document_id`),
  KEY `fk_doc_id_idx2` (`document_id`),
  CONSTRAINT `fk_docId` FOREIGN KEY (`document_id`) REFERENCES `documents` (`doc_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `digital_info`
--

DROP TABLE IF EXISTS `digital_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `digital_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL,
  `key` varchar(100) DEFAULT NULL,
  `value` text,
  `type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_aid_idx` (`parent_id`),
  CONSTRAINT `fk_aid` FOREIGN KEY (`parent_id`) REFERENCES `documents` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=253 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `digital_info_mapping`
--

DROP TABLE IF EXISTS `digital_info_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `digital_info_mapping` (
  `info_id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(45) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `doc_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`info_id`),
  KEY `fk_digital_info_mapings_document_type1_idx` (`doc_type_id`),
  CONSTRAINT `fk_doc_type_id` FOREIGN KEY (`doc_type_id`) REFERENCES `document_type` (`doc_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL,
  `doc_id` int(11) NOT NULL,
  `file_id` varchar(145) NOT NULL,
  `file_name` varchar(145) DEFAULT NULL,
  `file_size` int(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_file_document1_idx` (`parent_id`),
  CONSTRAINT `fk_id` FOREIGN KEY (`parent_id`) REFERENCES `documents` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `metalogging`
--

DROP TABLE IF EXISTS `metalogging`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metalogging` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `api_name` varchar(255) DEFAULT NULL,
  `complete_url` mediumtext,
  `method_type` varchar(45) DEFAULT NULL,
  `response_time_millis` int(11) DEFAULT NULL,
  `success` tinyint(1) DEFAULT NULL,
  `http_status_code` int(11) DEFAULT NULL,
  `requested_time` datetime DEFAULT NULL,
  `respond_time` datetime DEFAULT NULL,
  `request_payload` longtext,
  `response_payload` longtext,
  `headers` mediumtext,
  `client_ip` varchar(45) DEFAULT NULL,
  `server_ip` varchar(45) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `sessionid` varchar(255) DEFAULT NULL,
  `username_in_session` varchar(255) DEFAULT NULL,
  `apikey` varchar(255) DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2851 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `uploaded_file_info`
--

DROP TABLE IF EXISTS `uploaded_file_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uploaded_file_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_id` varchar(150) DEFAULT NULL,
  `file_name` varchar(150) DEFAULT NULL,
  `file_size` int(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `verification`
--

DROP TABLE IF EXISTS `verification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `system_id` int(11) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `updated_by` varchar(45) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `doc_id` int(11) NOT NULL,
  `parent_id` int(11) NOT NULL,
  PRIMARY KEY (`id`,`parent_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `id_idx` (`parent_id`),
  CONSTRAINT `id_fk` FOREIGN KEY (`parent_id`) REFERENCES `documents` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;


LOCK TABLES `system_detail` WRITE;
/*!40000 ALTER TABLE `system_detail` DISABLE KEYS */;
INSERT INTO `system_detail` (`system_id`, `system_name`, `userid`, `password`) VALUES (1,'fsme','fsme','fsme');
/*!40000 ALTER TABLE `system_detail` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `category_detail` WRITE;
/*!40000 ALTER TABLE `category_detail` DISABLE KEYS */;
INSERT INTO `category_detail` (`id`, `cat_name`) VALUES (1,'Bank Statements'),(2,'Financials'),(3,'KYC Company'),(4,'KYC Promotors'),(5,'Reference Check'),(6,'Sanctioned Loan docs'),(7,'Supporting documents');
/*!40000 ALTER TABLE `category_detail` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `document_type` WRITE;
/*!40000 ALTER TABLE `document_type` DISABLE KEYS */;
INSERT INTO `document_type` (`doc_type_id`,`doc_type_name`,`category_id`,`max_documents`) VALUES (1,'Audited Balance sheet for last 2 years - All schedules',2,32767),(2,'Auditors report',2,32767),(3,'Form 3CD',2,32767),(4,'Form 3CB',2,32767),(5,'Annual report',2,32767),(6,'P&L',2,32767),(7,'Audited Balance sheet',2,32767),(8,'Monthly sales record',2,32767),(9,'ITR',2,32767),(10,'VAT returns',2,32767),(11,'Personal PAN card',3,1),(12,'Address proof',3,32767),(13,'Premise picture',3,32767),(14,'MOA',3,32767),(15,'VAT registration certificate',3,32767),(16,'Partnership deed',4,32767),(17,'Loan sanction letter',4,32767),(18,'Company PAN Card',3,1),(19,'Business Vintage',3,1),(20,'Aadhar Card',3,1),(21,'Profile Picture',3,1),(22,'Premise Selfie of KAM',3,32767),(23,'Premise Warehouse Image',3,32767),(24,'Premise Machinery & Plant',3,32767),(25,'Customer Onboarding Form',3,32767),(26,'Premise Headoffice Image',3,32767),(27, 'Director Pan Card',4,32767),(28, 'Latest of Directors & Shareholding pattern', 2,32767),(29,'Monthly sales figures',2 , 32767),(30, 'Top three customer and supplier ( Name, Years of association and Annual Business volume )', 2, 32767);
/*!40000 ALTER TABLE `document_type` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `accessible_to` WRITE;
/*!40000 ALTER TABLE `accessible_to` DISABLE KEYS */;
INSERT INTO `accessible_to` (`system_id`, `doc_type_id`) VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30);
/*!40000 ALTER TABLE `accessible_to` ENABLE KEYS */;
UNLOCK TABLES;



LOCK TABLES `digital_info_mapping` WRITE;
/*!40000 ALTER TABLE `digital_info_mapping` DISABLE KEYS */;
INSERT INTO `digital_info_mapping` VALUES (1,'From Date','date',1),(2,'To Date','date',1),(3,'Year','Text',2),(4,'From Date','date',5),(5,'To Date','date',5),(6,'From Date','date',6),(7,'To Date','date',6),(8,'From Date','date',7),(9,'To Date','date',7),(10,'From Date','date',8),(11,'To Date','date',8),(12,'From Date','date',9),(13,'To Date','date',9),(14,'From Date','date',10),(15,'To Date','date',10),(16,'Personal PAN Number','Text',11),(17,'Address','Text',12),(18,'Year','Text',1),(19,'Company PAN Number','Text',18),(20,'Year','Text',19),(21,'Aadhar Number','Text',20), (22,'Director PAN Number', 'Text', 27),(23,'From Date','date',29),(24,'To Date','date',29);
/*!40000 ALTER TABLE `digital_info_mapping` ENABLE KEYS */;
UNLOCK TABLES;






-- Dump completed on 2017-09-07 10:44:17


