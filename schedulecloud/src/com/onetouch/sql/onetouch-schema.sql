DROP TABLE IF EXISTS `company`;
CREATE TABLE  `company` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address1` varchar(200) NOT NULL,
  `address2` varchar(200) DEFAULT NULL,
  `city` varchar(200) NOT NULL,
  `state` varchar(200) NOT NULL,
  `statecode` char(2) NOT NULL,
  `zipcode` varchar(5) NOT NULL,
  `email` varchar(200) NOT NULL,
  `business_phone` varchar(200) NOT NULL,
  `fax` varchar(200) NOT NULL,
  `code` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `companysetting`;
CREATE TABLE  `companysetting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `logoUrl` text,
  `logoutUrl` text,
  `theme` varchar(50) DEFAULT NULL,
  `policyFileUrl` text,
  `tutorialUrl` text,
  `company_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_CompanySetting_1` (`company_id`),
  CONSTRAINT `FK_CompanySetting_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `employee`;
CREATE TABLE  `employee` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `firstname` varchar(100) DEFAULT NULL,
  `lastname` varchar(100) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `address1` varchar(100) DEFAULT NULL,
  `address2` varchar(100) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `statecode` char(2) DEFAULT NULL,
  `zipcode` varchar(10) DEFAULT NULL,
  `homephone` varchar(10) DEFAULT NULL,
  `cellphone` varchar(10) DEFAULT NULL,
  `fax` varchar(10) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `getemail` char(1) DEFAULT NULL,
  `getsms` char(1) DEFAULT NULL,
  `photo_location` varchar(200) DEFAULT NULL,
  `temporary_password` varchar(100) DEFAULT NULL,
  `hourly_rate` decimal(10,2) DEFAULT NULL,
  `adminnotes` varchar(200) DEFAULT NULL,
  `createtime` datetime NOT NULL,
  `company_id` int(10) unsigned NOT NULL,
  `accept_policy` char(1) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_employee_1` (`company_id`),
  CONSTRAINT `FK_employee_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `location`;
CREATE TABLE  `location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address1` varchar(200) NOT NULL,
  `address2` varchar(200) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `statecode` char(2) NOT NULL,
  `location_type` varchar(10) NOT NULL,
  `contact_name` varchar(45) NOT NULL,
  `primary_phone` varchar(10) DEFAULT NULL,
  `alternate_phone` varchar(10) DEFAULT NULL,
  `fax` varchar(10) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `parking_direction` text,
  `company_id` int(10) unsigned NOT NULL,
  `zipcode` varchar(5) DEFAULT NULL,
  `timezone` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_location_1` (`company_id`),
  CONSTRAINT `FK_location_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB ;


DROP TABLE IF EXISTS `event`;
CREATE TABLE  `event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `owner_id` int(10) unsigned DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `dress_code` varchar(100) DEFAULT NULL,
  `location_id` int(11) DEFAULT NULL,
  `company_id` int(10) unsigned NOT NULL,
  `Notes` text,
  `coordinator_id` int(10) unsigned DEFAULT NULL,
  `description` text,
  `host` varchar(50) DEFAULT NULL,
  `eventday` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_event_1` (`location_id`),
  KEY `FK_event_2` (`company_id`),
  CONSTRAINT `FK_event_1` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FK_event_2` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `position`;
CREATE TABLE  `position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `position_color` varchar(50) DEFAULT NULL,
  `notes` text,
  `event_type` varchar(15) DEFAULT 'all',
  `company_id` int(11) unsigned NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_position_1` (`company_id`),
  CONSTRAINT `FK_position_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;


DROP TABLE IF EXISTS `shift`;
CREATE TABLE  `shift` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `shift_color` varchar(45) DEFAULT NULL,
  `notes` varchar(100) DEFAULT NULL,
  `company_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_shift_1` (`company_id`),
  CONSTRAINT `FK_shift_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `availability`;
CREATE TABLE  `availability` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `starttime` datetime DEFAULT NULL,
  `endtime` datetime DEFAULT NULL,
  `avail_allday` tinyint(1) DEFAULT NULL,
  `notes` text,
  `employee_id` int(10) unsigned DEFAULT NULL,
  `company_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `schedule`;
CREATE TABLE  `schedule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` int(10) DEFAULT NULL,
  `employee_id` int(10) DEFAULT NULL,
  `shift_id` int(10) DEFAULT NULL,
  `HoursOfWork` int(10) DEFAULT NULL,
  `company_id` int(10) DEFAULT NULL,
  `position_id` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `schedule_key` (`event_id`,`employee_id`,`shift_id`,`company_id`) USING BTREE
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `shift_position`;
CREATE TABLE  `shift_position` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` int(11) DEFAULT NULL,
  `position_id` int(11) DEFAULT NULL,
  `number` int(10) unsigned DEFAULT NULL,
  `shift_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Event_Position_1` (`event_id`),
  KEY `FK_Event_Position_2` (`position_id`),
  CONSTRAINT `FK_Event_Position_1` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_Event_Position_2` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `employee_position`;
CREATE TABLE  `employee_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(10) unsigned NOT NULL,
  `position_id` int(10) unsigned NOT NULL,
  `company_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `users`;
CREATE TABLE  `users` (
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `tenant_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `FK_users_1` (`tenant_id`),
  CONSTRAINT `FK_users_1` FOREIGN KEY (`tenant_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `groups`;
CREATE TABLE  `groups` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `group_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `group_authorities`;
CREATE TABLE  `group_authorities` (
  `group_id` bigint(20) unsigned NOT NULL,
  `authority` varchar(100) NOT NULL,
  KEY `FK_group_authorities_1` (`group_id`),
  CONSTRAINT `FK_group_authorities_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `group_members`;
CREATE TABLE  `group_members` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `group_id` bigint(20) unsigned NOT NULL,
  `tenant_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_group_members_1` (`group_id`),
  KEY `FK_group_members_2` (`tenant_id`),
  CONSTRAINT `FK_group_members_2` FOREIGN KEY (`tenant_id`) REFERENCES `company` (`id`),
  CONSTRAINT `FK_group_members_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB ;

DROP TABLE IF EXISTS `states`;
CREATE TABLE  `states` (
  `StateCode` char(2) NOT NULL DEFAULT '',
  `StateName` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`StateCode`)
) ENGINE=InnoDB ;