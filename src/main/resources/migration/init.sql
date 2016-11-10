CREATE DATABASE IF NOT EXISTS `librarian` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `librarian`;

CREATE TABLE IF NOT EXISTS `User` (
  `id` bigint(20) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` char(32) DEFAULT NULL,
  `status` varchar(50) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `avatarPath` varchar(255) DEFAULT NULL,
  `age` tinyint(3) unsigned DEFAULT NULL,
  `major` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `remarks` text,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `UserRole` (
  `userId` bigint(20) NOT NULL,
  `role` varchar(50) NOT NULL,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`,`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `Book` (
  `isbn` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` varchar(50) NOT NULL,
  `publisher` varchar(50) DEFAULT NULL,
  `authors` varchar(1023) DEFAULT NULL,
  `imagePath` varchar(255) DEFAULT NULL,
  `desc` text,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`isbn`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `BookTrace` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `isbn` varchar(20) NOT NULL,
  `status` varchar(50) NOT NULL,
  `location` varchar(255) DEFAULT '',
  `loanId` bigint(20) unsigned NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_isbn_status` (`isbn`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `Loan` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `traceId` bigint(20) unsigned NOT NULL,
  `status` varchar(50) NOT NULL,
  `isReservation` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `isLate` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `renewCount` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `appointedDuration` int(10) unsigned DEFAULT NULL,
  `expiredTime` timestamp NULL DEFAULT NULL,
  `activeTime` timestamp NULL DEFAULT NULL,
  `appointedTime` timestamp NULL DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_record` (`userId`,`traceId`),
  KEY `idx_trace` (`traceId`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `Record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT NULL,
  `traceId` bigint(20) unsigned DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  `payload` text,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_time` (`time`),
  KEY `idx_record` (`userId`,`traceId`,`action`),
  KEY `idx_traceId` (`traceId`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;