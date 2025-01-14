-- MySQL dump 10.13  Distrib 5.7.9, for osx10.9 (x86_64)
--
-- Host: 192.168.1.25    Database: dms
-- ------------------------------------------------------
-- Server version	5.6.33-0ubuntu0.14.04.1

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
-- Dumping data for table `accessible_to`
--

LOCK TABLES `accessible_to` WRITE;
/*!40000 ALTER TABLE `accessible_to` DISABLE KEYS */;
INSERT INTO `accessible_to` VALUES (1,1,1),(2,1,2),(3,1,3),(8,1,4),(9,1,5),(10,1,6),(11,1,7),(12,1,8),(13,1,9),(14,1,10),(15,1,11),(16,1,12),(17,1,13),(18,1,14),(19,1,15),(20,1,16),(21,1,17),(31,1,18),(32,1,19),(33,1,20),(34,1,21),(35,1,22),(36,1,23),(37,1,24),(38,1,25),(39,1,26);
/*!40000 ALTER TABLE `accessible_to` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `category_detail`
--

LOCK TABLES `category_detail` WRITE;
/*!40000 ALTER TABLE `category_detail` DISABLE KEYS */;
INSERT INTO `category_detail` VALUES (1,'Bank Statements','2017-07-24 13:22:41','2017-07-24 09:22:40',NULL),(2,'Financials','2017-07-24 13:55:12','2017-07-24 09:22:40',NULL),(3,'KYC Company','2017-07-24 14:52:40','2017-07-24 09:22:40',NULL),(4,'KYC Promotors','2017-07-24 14:52:40','2017-07-24 09:22:40',NULL),(5,'Reference Check','2017-07-24 14:52:40','2017-07-24 09:22:40',NULL),(6,'Sanctioned Loan docs','2017-07-24 14:52:40','2017-07-24 09:22:40',NULL),(7,'Supporting documents','2017-07-24 14:52:40','2017-07-24 09:22:40',NULL);
/*!40000 ALTER TABLE `category_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `digital_info_mapping`
--

LOCK TABLES `digital_info_mapping` WRITE;
/*!40000 ALTER TABLE `digital_info_mapping` DISABLE KEYS */;
INSERT INTO `digital_info_mapping` VALUES (1,'From Date','date',1),(2,'To Date','date',1),(3,'Year','Text',2),(4,'From Date','date',5),(5,'To Date','date',5),(6,'From Date','date',6),(7,'To Date','date',6),(8,'From Date','date',7),(9,'To Date','date',7),(10,'From Date','date',8),(11,'To Date','date',8),(12,'From Date','date',9),(13,'To Date','date',9),(14,'From Date','date',10),(15,'To Date','date',10),(16,'Personal PAN Number','Text',11),(17,'Address','Text',12),(18,'Year','Text',1),(19,'Company PAN Number','Text',18),(20,'Year','Text',19),(21,'Aadhar Number','Text',20);
/*!40000 ALTER TABLE `digital_info_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `document_type`
--

LOCK TABLES `document_type` WRITE;
/*!40000 ALTER TABLE `document_type` DISABLE KEYS */;
INSERT INTO `document_type` VALUES (1,'Audited Balance sheet for last 2 years - All schedules',NULL,2),(2,'Auditors report',NULL,2),(3,'Form 3CD',NULL,2),(4,'Form 3CB',NULL,2),(5,'Annual report',NULL,2),(6,'P&L',NULL,2),(7,'Audited Balance sheet',NULL,2),(8,'Monthly sales record',NULL,2),(9,'ITR',NULL,2),(10,'VAT returns',NULL,2),(11,'Personal PAN card',NULL,3),(12,'Address proof',NULL,3),(13,'Premise picture',NULL,3),(14,'MOA',NULL,3),(15,'VAT registration certificate',NULL,3),(16,'Partnership deed',NULL,4),(17,'Loan sanction letter',NULL,4),(18,'Company PAN Card','2017-08-11 11:36:17',3),(19,'Business Vintage','2017-08-11 11:36:17',3),(20,'Aadhar Card','2017-08-11 11:36:17',3),(21,'Profile Picture','2017-08-11 11:36:17',3),(22,'Premise Selfie of KAM','2017-08-11 11:36:17',3),(23,'Premise Warehouse Image','2017-08-11 11:36:18',3),(24,'Premise Machinery & Plant','2017-08-11 11:36:18',3),(25,'Customer Onboarding Form','2017-08-11 12:09:52',3),(26,'Premise Headoffice Image','2017-08-11 12:13:29',3);
/*!40000 ALTER TABLE `document_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `system_detail`
--

LOCK TABLES `system_detail` WRITE;
/*!40000 ALTER TABLE `system_detail` DISABLE KEYS */;
INSERT INTO `system_detail` VALUES (1,'fsme','fsme','fsme','2017-07-17 14:53:18','2017-07-17 09:23:18');
/*!40000 ALTER TABLE `system_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-16 16:02:31
