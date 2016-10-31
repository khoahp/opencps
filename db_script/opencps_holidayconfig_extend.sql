/*
Navicat MySQL Data Transfer

Source Server         : localhost_mariadb
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : super_demo_qa

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2016-10-27 10:52:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for opencps_holidayconfig_extend
-- ----------------------------
DROP TABLE IF EXISTS `opencps_holidayconfig_extend`;
CREATE TABLE `opencps_holidayconfig_extend` (
  `holidayExtendId` bigint(20) NOT NULL,
  `key_` varchar(75) DEFAULT NULL,
  `description` varchar(75) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `companyId` bigint(20) DEFAULT NULL,
  `groupId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`holidayExtendId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of opencps_holidayconfig_extend
-- ----------------------------
INSERT INTO `opencps_holidayconfig_extend` VALUES ('1', 'SATURDAY', null, '1', null, null, null);
INSERT INTO `opencps_holidayconfig_extend` VALUES ('2', 'SUNDAY', '', '1', '0', '0', '0');
