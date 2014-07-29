CREATE DATABASE  IF NOT EXISTS `pmc` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `pmc`;
-- MySQL dump 10.13  Distrib 5.6.13, for osx10.6 (i386)
--
-- Host: 10.0.3.2    Database: pmc
-- ------------------------------------------------------
-- Server version	5.1.63-0ubuntu0.10.04.1-log

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
-- Table structure for table `app`
--

DROP TABLE IF EXISTS `app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app` (
  `id_app` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `active` int(11) NOT NULL DEFAULT '1',
  `single_client_url` varchar(100) NOT NULL,
  `batch_clients_url` varchar(100) NOT NULL,
  `clean_device_url` varchar(100) NOT NULL,
  `ios_push_apns_cert_production` varchar(100) NOT NULL,
  `ios_push_apns_cert_sandbox` varchar(100) NOT NULL,
  `ios_push_apns_passphrase` varchar(45) NOT NULL,
  `ios_sandbox` int(11) NOT NULL DEFAULT '0',
  `google_api_key` varchar(45) NOT NULL,
  `mailgun_apikey` varchar(45) NOT NULL,
  `mailgun_from` varchar(45) NOT NULL,
  `mailgun_to` varchar(45) NOT NULL,
  `mailgun_apiurl` varchar(45) NOT NULL,
  `title` varchar(45) NOT NULL,
  `sound` varchar(45) NOT NULL,
  `debug` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_app`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app`
--

LOCK TABLES `app` WRITE;
/*!40000 ALTER TABLE `app` DISABLE KEYS */;
INSERT INTO `app` VALUES (1,'TVMax',1,'http://a-wing:9010/KrakenSocialClients/v1/client/devices','http://a-wing:9010/KrakenSocialClients/v1/batchIDs','http://a-wing:9010/KrakenSocialClients/v1/devices/clean','/Users/plesse/Documents/projects/PMC/certificate/TVMaxDeportes/TVMaxPushProduction.p12','/Users/plesse/Documents/projects/PMC/certificate/TVMaxDeportes/TVMaxPushProduction.p12','CdNnKVl2U5Y=',0,'AIzaSyDqQZmIWLiQ2IPYg1rgPi5lsn7LwEfb5Fk','key-1nzg4-6clfqjv57ys5pifwiyzspr8b65','postmaster@polla.tvmax-9.com','inaki@hecticus.com','https://api.mailgun.net/v2/polla.tvmax-9.com/','TVMax Mundial','',1),(2,'secondPushableApp',0,'','','','','','',0,'','','','','','','',1);
/*!40000 ALTER TABLE `app` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_device`
--

DROP TABLE IF EXISTS `app_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_device` (
  `id_app_device` int(11) NOT NULL AUTO_INCREMENT,
  `id_app` int(11) NOT NULL,
  `id_device` int(11) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_app_device`),
  KEY `fk_id_app_idx` (`id_app`),
  KEY `fk_id_device_idx` (`id_device`),
  CONSTRAINT `fk_id_app` FOREIGN KEY (`id_app`) REFERENCES `app` (`id_app`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_id_device` FOREIGN KEY (`id_device`) REFERENCES `device` (`id_device`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_device`
--

LOCK TABLES `app_device` WRITE;
/*!40000 ALTER TABLE `app_device` DISABLE KEYS */;
INSERT INTO `app_device` VALUES (1,1,1,1),(2,1,2,1),(3,1,3,0);
/*!40000 ALTER TABLE `app_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configs`
--

DROP TABLE IF EXISTS `configs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configs` (
  `id_config` bigint(30) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) DEFAULT '',
  `value` text,
  `description` text,
  PRIMARY KEY (`id_config`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configs`
--

LOCK TABLES `configs` WRITE;
/*!40000 ALTER TABLE `configs` DISABLE KEYS */;
INSERT INTO `configs` VALUES (1,'alarm-send-millis','300000','Tiempo entre envios en milisegundos'),(2,'alarm-smtp-server','smtp.gmail.com','Servidor SMTP'),(3,'alarm-sender-address','alarma.rk@hecticus.com','Direccion de email de las alarmas'),(4,'alarm-sender-pw','alarma12345','Password del email de las alarmas'),(6,'config-read-millis','10000','Tiempo de refresh del estas variables de config'),(5,'core-query-limit','5000','Tamano maximo del query utilizado en varios sitios'),(16,'support-level-1','inaki@hecticus.com ','Correos separados por ;'),(7,'app-name','PMC_INAKI','Pais o aplicacion de la instacia'),(8,'jobs-keep-alive-allowed','3600000','Tiempo en milisegundos para determinar timeout del job. Default 3600000'),(9,'pmc-url','a-wing:9015','revan:9000'),(10,'caches-update-delay','300000','Tiempo en que un cache vence y tiene que buscar nuevamente a traves del WS apropiado. Tiempo en millisegundos (default 300000)'),(17,'pushConsumers','1','...'),(11,'guava-caches-update-delay','10','Tiempo en que un cache vence y tiene que buscar nuevamente a traves del WS apropiado. Tiempo en minutos'),(12,'guava-vars-cache-update-delay','10','Delay para actualizar cache de placeholders'),(13,'rackspace-username','hctcsproddfw','Usuario de rackspace '),(14,'rackspace-apiKey','276ef48143b9cd81d3bef7ad9fbe4e06','clave de rackspacew'),(15,'rackspace-provider','cloudfiles-us','Nombre del proveedor de rackspace'),(30,'kraken-play-url','http://10.0.3.2:9010/','Url para conectarse con los web services desarrollados en Play'),(18,'eventConsumers','1',',,,'),(19,'rabbit-mq-host','10.0.3.30','rabbit host'),(20,'rabbit-mq-user','admin','user'),(21,'rabbit-mq-password','abc123','pwd'),(22,'rabbit-mq-event-queue','EVENTS','cola de eventos'),(23,'packages-size','500','pack size'),(24,'push-size','10','push-size'),(25,'rabbit-mq-push-queue','PUSH','push'),(26,'android-push-url','https://android.googleapis.com/gcm/send','url de push de google'),(29,'ws-timeout-millis','1000','timeout ws'),(27,'rabbit-mq-result-queue','PUSH_RESULT','cola de resultados'),(28,'cache-loader-sleep','60','tiempo que duerme el cache loader en segundos'),(31,'resultAnalyzers','0',',');
/*!40000 ALTER TABLE `configs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `device` (
  `id_device` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id_device`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (1,'droid'),(2,'ios'),(3,'web'),(4,'msisdn');
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timezones`
--

DROP TABLE IF EXISTS `timezones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `timezones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=486 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timezones`
--

LOCK TABLES `timezones` WRITE;
/*!40000 ALTER TABLE `timezones` DISABLE KEYS */;
INSERT INTO `timezones` VALUES (1,'Africa/Abidjan'),(2,'Africa/Accra'),(3,'Africa/Addis_Ababa'),(4,'Africa/Algiers'),(5,'Africa/Asmara'),(6,'Africa/Asmera'),(7,'Africa/Bamako'),(8,'Africa/Bangui'),(9,'Africa/Banjul'),(10,'Africa/Bissau'),(11,'Africa/Blantyre'),(12,'Africa/Brazzaville'),(13,'Africa/Bujumbura'),(14,'Africa/Cairo'),(15,'Africa/Casablanca'),(16,'Africa/Ceuta'),(17,'Africa/Conakry'),(18,'Africa/Dakar'),(19,'Africa/Dar_es_Salaam'),(20,'Africa/Djibouti'),(21,'Africa/Douala'),(22,'Africa/El_Aaiun'),(23,'Africa/Freetown'),(24,'Africa/Gaborone'),(25,'Africa/Harare'),(26,'Africa/Johannesburg'),(27,'Africa/Kampala'),(28,'Africa/Khartoum'),(29,'Africa/Kigali'),(30,'Africa/Kinshasa'),(31,'Africa/Lagos'),(32,'Africa/Libreville'),(33,'Africa/Lome'),(34,'Africa/Luanda'),(35,'Africa/Lubumbashi'),(36,'Africa/Lusaka'),(37,'Africa/Malabo'),(38,'Africa/Maputo'),(39,'Africa/Maseru'),(40,'Africa/Mbabane'),(41,'Africa/Mogadishu'),(42,'Africa/Monrovia'),(43,'Africa/Nairobi'),(44,'Africa/Ndjamena'),(45,'Africa/Niamey'),(46,'Africa/Nouakchott'),(47,'Africa/Ouagadougou'),(48,'Africa/Porto-Novo'),(49,'Africa/Sao_Tome'),(50,'Africa/Timbuktu'),(51,'Africa/Tripoli'),(52,'Africa/Tunis'),(53,'Africa/Windhoek'),(54,'America/Adak'),(55,'America/Anchorage'),(56,'America/Anguilla'),(57,'America/Antigua'),(58,'America/Araguaina'),(59,'America/Argentina/Buenos_Aires'),(60,'America/Argentina/Catamarca'),(61,'America/Argentina/ComodRivadavia'),(62,'America/Argentina/Cordoba'),(63,'America/Argentina/Jujuy'),(64,'America/Argentina/La_Rioja'),(65,'America/Argentina/Mendoza'),(66,'America/Argentina/Rio_Gallegos'),(67,'America/Argentina/Salta'),(68,'America/Argentina/San_Juan'),(69,'America/Argentina/San_Luis'),(70,'America/Argentina/Tucuman'),(71,'America/Argentina/Ushuaia'),(72,'America/Aruba'),(73,'America/Asuncion'),(74,'America/Atikokan'),(75,'America/Atka'),(76,'America/Bahia'),(77,'America/Barbados'),(78,'America/Belem'),(79,'America/Belize'),(80,'America/Blanc-Sablon'),(81,'America/Boa_Vista'),(82,'America/Bogota'),(83,'America/Boise'),(84,'America/Buenos_Aires'),(85,'America/Cambridge_Bay'),(86,'America/Campo_Grande'),(87,'America/Cancun'),(88,'America/Caracas'),(89,'America/Catamarca'),(90,'America/Cayenne'),(91,'America/Cayman'),(92,'America/Chicago'),(93,'America/Chihuahua'),(94,'America/Coral_Harbour'),(95,'America/Cordoba'),(96,'America/Costa_Rica'),(97,'America/Cuiaba'),(98,'America/Curacao'),(99,'America/Danmarkshavn'),(100,'America/Dawson'),(101,'America/Dawson_Creek'),(102,'America/Denver'),(103,'America/Detroit'),(104,'America/Dominica'),(105,'America/Edmonton'),(106,'America/Eirunepe'),(107,'America/El_Salvador'),(108,'America/Ensenada'),(109,'America/Fortaleza'),(110,'America/Fort_Wayne'),(111,'America/Glace_Bay'),(112,'America/Godthab'),(113,'America/Goose_Bay'),(114,'America/Grand_Turk'),(115,'America/Grenada'),(116,'America/Guadeloupe'),(117,'America/Guatemala'),(118,'America/Guayaquil'),(119,'America/Guyana'),(120,'America/Halifax'),(121,'America/Havana'),(122,'America/Hermosillo'),(123,'America/Indiana/Indianapolis'),(124,'America/Indiana/Knox'),(125,'America/Indiana/Marengo'),(126,'America/Indiana/Petersburg'),(127,'America/Indianapolis'),(128,'America/Indiana/Tell_City'),(129,'America/Indiana/Vevay'),(130,'America/Indiana/Vincennes'),(131,'America/Indiana/Winamac'),(132,'America/Inuvik'),(133,'America/Iqaluit'),(134,'America/Jamaica'),(135,'America/Jujuy'),(136,'America/Juneau'),(137,'America/Kentucky/Louisville'),(138,'America/Kentucky/Monticello'),(139,'America/Knox_IN'),(140,'America/La_Paz'),(141,'America/Lima'),(142,'America/Los_Angeles'),(143,'America/Louisville'),(144,'America/Maceio'),(145,'America/Managua'),(146,'America/Manaus'),(147,'America/Marigot'),(148,'America/Martinique'),(149,'America/Mazatlan'),(150,'America/Mendoza'),(151,'America/Menominee'),(152,'America/Merida'),(153,'America/Mexico_City'),(154,'America/Miquelon'),(155,'America/Moncton'),(156,'America/Monterrey'),(157,'America/Montevideo'),(158,'America/Montreal'),(159,'America/Montserrat'),(160,'America/Nassau'),(161,'America/New_York'),(162,'America/Nipigon'),(163,'America/Nome'),(164,'America/Noronha'),(165,'America/North_Dakota/Center'),(166,'America/North_Dakota/New_Salem'),(167,'America/Panama'),(168,'America/Pangnirtung'),(169,'America/Paramaribo'),(170,'America/Phoenix'),(171,'America/Port-au-Prince'),(172,'America/Porto_Acre'),(173,'America/Port_of_Spain'),(174,'America/Porto_Velho'),(175,'America/Puerto_Rico'),(176,'America/Rainy_River'),(177,'America/Rankin_Inlet'),(178,'America/Recife'),(179,'America/Regina'),(180,'America/Resolute'),(181,'America/Rio_Branco'),(182,'America/Rosario'),(183,'America/Santarem'),(184,'America/Santiago'),(185,'America/Santo_Domingo'),(186,'America/Sao_Paulo'),(187,'America/Scoresbysund'),(188,'America/Shiprock'),(189,'America/St_Barthelemy'),(190,'America/St_Johns'),(191,'America/St_Kitts'),(192,'America/St_Lucia'),(193,'America/St_Thomas'),(194,'America/St_Vincent'),(195,'America/Swift_Current'),(196,'America/Tegucigalpa'),(197,'America/Thule'),(198,'America/Thunder_Bay'),(199,'America/Tijuana'),(200,'America/Toronto'),(201,'America/Tortola'),(202,'America/Vancouver'),(203,'America/Virgin'),(204,'America/Whitehorse'),(205,'America/Winnipeg'),(206,'America/Yakutat'),(207,'America/Yellowknife'),(208,'Antarctica/Casey'),(209,'Antarctica/Davis'),(210,'Antarctica/DumontDUrville'),(211,'Antarctica/Mawson'),(212,'Antarctica/McMurdo'),(213,'Antarctica/Palmer'),(214,'Antarctica/Rothera'),(215,'Antarctica/South_Pole'),(216,'Antarctica/Syowa'),(217,'Antarctica/Vostok'),(218,'Arctic/Longyearbyen'),(219,'Asia/Aden'),(220,'Asia/Almaty'),(221,'Asia/Amman'),(222,'Asia/Anadyr'),(223,'Asia/Aqtau'),(224,'Asia/Aqtobe'),(225,'Asia/Ashgabat'),(226,'Asia/Ashkhabad'),(227,'Asia/Baghdad'),(228,'Asia/Bahrain'),(229,'Asia/Baku'),(230,'Asia/Bangkok'),(231,'Asia/Beirut'),(232,'Asia/Bishkek'),(233,'Asia/Brunei'),(234,'Asia/Calcutta'),(235,'Asia/Choibalsan'),(236,'Asia/Chongqing'),(237,'Asia/Chungking'),(238,'Asia/Colombo'),(239,'Asia/Dacca'),(240,'Asia/Damascus'),(241,'Asia/Dhaka'),(242,'Asia/Dili'),(243,'Asia/Dubai'),(244,'Asia/Dushanbe'),(245,'Asia/Gaza'),(246,'Asia/Harbin'),(247,'Asia/Ho_Chi_Minh'),(248,'Asia/Hong_Kong'),(249,'Asia/Hovd'),(250,'Asia/Irkutsk'),(251,'Asia/Istanbul'),(252,'Asia/Jakarta'),(253,'Asia/Jayapura'),(254,'Asia/Jerusalem'),(255,'Asia/Kabul'),(256,'Asia/Kamchatka'),(257,'Asia/Karachi'),(258,'Asia/Kashgar'),(259,'Asia/Katmandu'),(260,'Asia/Kolkata'),(261,'Asia/Krasnoyarsk'),(262,'Asia/Kuala_Lumpur'),(263,'Asia/Kuching'),(264,'Asia/Kuwait'),(265,'Asia/Macao'),(266,'Asia/Macau'),(267,'Asia/Magadan'),(268,'Asia/Makassar'),(269,'Asia/Manila'),(270,'Asia/Muscat'),(271,'Asia/Nicosia'),(272,'Asia/Novosibirsk'),(273,'Asia/Omsk'),(274,'Asia/Oral'),(275,'Asia/Phnom_Penh'),(276,'Asia/Pontianak'),(277,'Asia/Pyongyang'),(278,'Asia/Qatar'),(279,'Asia/Qyzylorda'),(280,'Asia/Rangoon'),(281,'Asia/Riyadh'),(282,'Asia/Riyadh87'),(283,'Asia/Riyadh88'),(284,'Asia/Riyadh89'),(285,'Asia/Saigon'),(286,'Asia/Sakhalin'),(287,'Asia/Samarkand'),(288,'Asia/Seoul'),(289,'Asia/Shanghai'),(290,'Asia/Singapore'),(291,'Asia/Taipei'),(292,'Asia/Tashkent'),(293,'Asia/Tbilisi'),(294,'Asia/Tehran'),(295,'Asia/Tel_Aviv'),(296,'Asia/Thimbu'),(297,'Asia/Thimphu'),(298,'Asia/Tokyo'),(299,'Asia/Ujung_Pandang'),(300,'Asia/Ulaanbaatar'),(301,'Asia/Ulan_Bator'),(302,'Asia/Urumqi'),(303,'Asia/Vientiane'),(304,'Asia/Vladivostok'),(305,'Asia/Yakutsk'),(306,'Asia/Yekaterinburg'),(307,'Asia/Yerevan'),(308,'Atlantic/Azores'),(309,'Atlantic/Bermuda'),(310,'Atlantic/Canary'),(311,'Atlantic/Cape_Verde'),(312,'Atlantic/Faeroe'),(313,'Atlantic/Faroe'),(314,'Atlantic/Jan_Mayen'),(315,'Atlantic/Madeira'),(316,'Atlantic/Reykjavik'),(317,'Atlantic/South_Georgia'),(318,'Atlantic/Stanley'),(319,'Atlantic/St_Helena'),(320,'Australia/ACT'),(321,'Australia/Adelaide'),(322,'Australia/Brisbane'),(323,'Australia/Broken_Hill'),(324,'Australia/Canberra'),(325,'Australia/Currie'),(326,'Australia/Darwin'),(327,'Australia/Eucla'),(328,'Australia/Hobart'),(329,'Australia/LHI'),(330,'Australia/Lindeman'),(331,'Australia/Lord_Howe'),(332,'Australia/Melbourne'),(333,'Australia/North'),(334,'Australia/NSW'),(335,'Australia/Perth'),(336,'Australia/Queensland'),(337,'Australia/South'),(338,'Australia/Sydney'),(339,'Australia/Tasmania'),(340,'Australia/Victoria'),(341,'Australia/West'),(342,'Australia/Yancowinna'),(343,'Brazil/Acre'),(344,'Brazil/DeNoronha'),(345,'Brazil/East'),(346,'Brazil/West'),(347,'Canada/Atlantic'),(348,'Canada/Central'),(349,'Canada/Eastern'),(350,'Canada/East-Saskatchewan'),(351,'Canada/Mountain'),(352,'Canada/Newfoundland'),(353,'Canada/Pacific'),(354,'Canada/Saskatchewan'),(355,'Canada/Yukon'),(356,'Chile/Continental'),(357,'Chile/EasterIsland'),(358,'Europe/Amsterdam'),(359,'Europe/Andorra'),(360,'Europe/Athens'),(361,'Europe/Belfast'),(362,'Europe/Belgrade'),(363,'Europe/Berlin'),(364,'Europe/Bratislava'),(365,'Europe/Brussels'),(366,'Europe/Bucharest'),(367,'Europe/Budapest'),(368,'Europe/Chisinau'),(369,'Europe/Copenhagen'),(370,'Europe/Dublin'),(371,'Europe/Gibraltar'),(372,'Europe/Guernsey'),(373,'Europe/Helsinki'),(374,'Europe/Isle_of_Man'),(375,'Europe/Istanbul'),(376,'Europe/Jersey'),(377,'Europe/Kaliningrad'),(378,'Europe/Kiev'),(379,'Europe/Lisbon'),(380,'Europe/Ljubljana'),(381,'Europe/London'),(382,'Europe/Luxembourg'),(383,'Europe/Madrid'),(384,'Europe/Malta'),(385,'Europe/Mariehamn'),(386,'Europe/Minsk'),(387,'Europe/Monaco'),(388,'Europe/Moscow'),(389,'Europe/Nicosia'),(390,'Europe/Oslo'),(391,'Europe/Paris'),(392,'Europe/Podgorica'),(393,'Europe/Prague'),(394,'Europe/Riga'),(395,'Europe/Rome'),(396,'Europe/Samara'),(397,'Europe/San_Marino'),(398,'Europe/Sarajevo'),(399,'Europe/Simferopol'),(400,'Europe/Skopje'),(401,'Europe/Sofia'),(402,'Europe/Stockholm'),(403,'Europe/Tallinn'),(404,'Europe/Tirane'),(405,'Europe/Tiraspol'),(406,'Europe/Uzhgorod'),(407,'Europe/Vaduz'),(408,'Europe/Vatican'),(409,'Europe/Vienna'),(410,'Europe/Vilnius'),(411,'Europe/Volgograd'),(412,'Europe/Warsaw'),(413,'Europe/Zagreb'),(414,'Europe/Zaporozhye'),(415,'Europe/Zurich'),(416,'Indian/Antananarivo'),(417,'Indian/Chagos'),(418,'Indian/Christmas'),(419,'Indian/Cocos'),(420,'Indian/Comoro'),(421,'Indian/Kerguelen'),(422,'Indian/Mahe'),(423,'Indian/Maldives'),(424,'Indian/Mauritius'),(425,'Indian/Mayotte'),(426,'Indian/Reunion'),(427,'Mexico/BajaNorte'),(428,'Mexico/BajaSur'),(429,'Mexico/General'),(430,'Mideast/Riyadh87'),(431,'Mideast/Riyadh88'),(432,'Mideast/Riyadh89'),(433,'Pacific/Apia'),(434,'Pacific/Auckland'),(435,'Pacific/Chatham'),(436,'Pacific/Easter'),(437,'Pacific/Efate'),(438,'Pacific/Enderbury'),(439,'Pacific/Fakaofo'),(440,'Pacific/Fiji'),(441,'Pacific/Funafuti'),(442,'Pacific/Galapagos'),(443,'Pacific/Gambier'),(444,'Pacific/Guadalcanal'),(445,'Pacific/Guam'),(446,'Pacific/Honolulu'),(447,'Pacific/Johnston'),(448,'Pacific/Kiritimati'),(449,'Pacific/Kosrae'),(450,'Pacific/Kwajalein'),(451,'Pacific/Majuro'),(452,'Pacific/Marquesas'),(453,'Pacific/Midway'),(454,'Pacific/Nauru'),(455,'Pacific/Niue'),(456,'Pacific/Norfolk'),(457,'Pacific/Noumea'),(458,'Pacific/Pago_Pago'),(459,'Pacific/Palau'),(460,'Pacific/Pitcairn'),(461,'Pacific/Ponape'),(462,'Pacific/Port_Moresby'),(463,'Pacific/Rarotonga'),(464,'Pacific/Saipan'),(465,'Pacific/Samoa'),(466,'Pacific/Tahiti'),(467,'Pacific/Tarawa'),(468,'Pacific/Tongatapu'),(469,'Pacific/Truk'),(470,'Pacific/Wake'),(471,'Pacific/Wallis'),(472,'Pacific/Yap'),(473,'US/Alaska'),(474,'US/Aleutian'),(475,'US/Arizona'),(476,'US/Central'),(477,'US/Eastern'),(478,'US/East-Indiana'),(479,'US/Hawaii'),(480,'US/Indiana-Starke'),(481,'US/Michigan'),(482,'US/Mountain'),(483,'US/Pacific'),(484,'US/Pacific-New'),(485,'US/Samoa');
/*!40000 ALTER TABLE `timezones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `u01_users`
--

DROP TABLE IF EXISTS `u01_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `u01_users` (
  `u01_id` bigint(30) unsigned NOT NULL AUTO_INCREMENT,
  `u01_login` varchar(50) DEFAULT '',
  `u01_password` varchar(50) DEFAULT '',
  `u01_email` varchar(50) DEFAULT '',
  `u02_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`u01_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `u01_users`
--

LOCK TABLES `u01_users` WRITE;
/*!40000 ALTER TABLE `u01_users` DISABLE KEYS */;
INSERT INTO `u01_users` VALUES (1,'admin','MDFrNHBwMTIz','contact@hecticus.com',0),(2,'christian','Y2hyaXJvZDk5OSo=','christian@hecticus.com',NULL),(3,'r1','azRwcHcxZjFyMXRoNA==','juan@hecticus.com',NULL),(4,'alfonso','c3BsaW50ZXJDZWxsNDY=','alfonso@hecticus.com',NULL),(5,'jose','ajBzM2tyNGszbg==','jose.ortegano@hecticus.com',NULL),(6,'inaki','JVozciQ3MDNyM24hYWRtaW4=','inaki@hecticus.com',NULL);
/*!40000 ALTER TABLE `u01_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `u01u02_has`
--

DROP TABLE IF EXISTS `u01u02_has`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `u01u02_has` (
  `u01_id` bigint(20) NOT NULL,
  `u02_id` bigint(20) NOT NULL,
  PRIMARY KEY (`u01_id`,`u02_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `u01u02_has`
--

LOCK TABLES `u01u02_has` WRITE;
/*!40000 ALTER TABLE `u01u02_has` DISABLE KEYS */;
INSERT INTO `u01u02_has` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1);
/*!40000 ALTER TABLE `u01u02_has` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `u02_profiles`
--

DROP TABLE IF EXISTS `u02_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `u02_profiles` (
  `u02_id` bigint(30) unsigned NOT NULL AUTO_INCREMENT,
  `u02_name` varchar(50) DEFAULT '',
  `u03_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`u02_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `u02_profiles`
--

LOCK TABLES `u02_profiles` WRITE;
/*!40000 ALTER TABLE `u02_profiles` DISABLE KEYS */;
INSERT INTO `u02_profiles` VALUES (1,'Administrator',0),(2,'User',0);
/*!40000 ALTER TABLE `u02_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `u02u03_has`
--

DROP TABLE IF EXISTS `u02u03_has`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `u02u03_has` (
  `u02_id` bigint(20) NOT NULL DEFAULT '0',
  `u03_id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`u02_id`,`u03_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `u02u03_has`
--

LOCK TABLES `u02u03_has` WRITE;
/*!40000 ALTER TABLE `u02u03_has` DISABLE KEYS */;
INSERT INTO `u02u03_has` VALUES (1,1),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32),(1,33),(1,34),(1,35),(1,36),(1,37),(1,38),(1,39),(1,40),(1,41);
/*!40000 ALTER TABLE `u02u03_has` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `u03_permissions`
--

DROP TABLE IF EXISTS `u03_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `u03_permissions` (
  `u03_id` bigint(30) unsigned NOT NULL AUTO_INCREMENT,
  `u03_name` varchar(50) DEFAULT '',
  `u04_id` bigint(50) unsigned DEFAULT NULL,
  `u03_action` varchar(50) DEFAULT '',
  `u02_id` bigint(30) unsigned NOT NULL,
  PRIMARY KEY (`u03_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `u03_permissions`
--

LOCK TABLES `u03_permissions` WRITE;
/*!40000 ALTER TABLE `u03_permissions` DISABLE KEYS */;
INSERT INTO `u03_permissions` VALUES (1,'Login',1,'LOGIN',1),(6,'View Users',2,'VIEW',0),(7,'Modify Users',2,'MOD',0),(8,'Delete Users',2,'DEL',0),(9,'Add Users',2,'ADD',0),(10,'Add Profiles',3,'ADD',0),(11,'Add Permissions',4,'ADD',0),(12,'Add Sections',5,'ADD',0),(13,'Modify Profiles',3,'MOD',0),(14,'Modify Permissions',4,'MOD',0),(15,'Modify Sections',5,'MOD',0),(16,'Delete Profiles',3,'DEL',0),(17,'Delete Permissions',4,'DEL',0),(18,'Delete Sections',5,'DEL',0),(19,'View Profiles',3,'VIEW',0),(20,'View Permissions',4,'VIEW',0),(21,'View Sections',5,'VIEW',0),(22,'View on Config',7,'VIEW',1),(23,'Add on Config',7,'ADD',1),(24,'Modify on Config',7,'MOD',1),(25,'Delete on Config',7,'DEL',1),(26,'View on WorkersQueue',8,'VIEW',1),(27,'Add on WorkersQueue',8,'ADD',1),(28,'Modify on WorkersQueue',8,'MOD',1),(29,'Delete on WorkersQueue',8,'DEL',1),(30,'View on Job',9,'VIEW',1),(31,'Add on Job',9,'ADD',1),(32,'Modify on Job',9,'MOD',1),(33,'Delete on Job',9,'DEL',1),(34,'View on FailMsg',10,'VIEW',1),(35,'Add on FailMsg',10,'ADD',1),(36,'Modify on FailMsg',10,'MOD',1),(37,'Delete on FailMsg',10,'DEL',1),(38,'View on Timezone',11,'VIEW',1),(39,'Add on Timezone',11,'ADD',1),(40,'Modify on Timezone',11,'MOD',1),(41,'Delete on Timezone',11,'DEL',1);
/*!40000 ALTER TABLE `u03_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `u04_sections`
--

DROP TABLE IF EXISTS `u04_sections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `u04_sections` (
  `u04_id` bigint(30) unsigned NOT NULL AUTO_INCREMENT,
  `u04_name` varchar(50) DEFAULT '',
  PRIMARY KEY (`u04_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `u04_sections`
--

LOCK TABLES `u04_sections` WRITE;
/*!40000 ALTER TABLE `u04_sections` DISABLE KEYS */;
INSERT INTO `u04_sections` VALUES (1,'Admin'),(2,'Users'),(3,'Profiles'),(4,'Permissions'),(5,'Sections'),(7,'Config'),(8,'WorkersQueue'),(9,'Job'),(10,'FailMsg'),(11,'Timezone');
/*!40000 ALTER TABLE `u04_sections` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-07-29 14:35:18
