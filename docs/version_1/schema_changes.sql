CREATE TABLE `WebNodeContent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `angelId` bigint(20) DEFAULT NULL,
  `webNodeId` bigint(20) DEFAULT NULL,
  `regionKey` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `created` datetime DEFAULT NULL,
  `isDeleted` int(11) DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `content` mediumtext COLLATE utf8_unicode_ci,
  `content_textile` mediumtext COLLATE utf8_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `websiteid_layoutkey_uniq_idx` (`webNodeId`,`regionKey`),
  CONSTRAINT `WebNodeContent_ibfk_1` FOREIGN KEY (`webNodeId`) REFERENCES `WebNode` (`id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `WebNodeContentWebBlock` (
  `webNodeContentId` bigint(20) NOT NULL,
  `webBlockId` bigint(20) NOT NULL,
  PRIMARY KEY (`webNodeContentId`,`webBlockId`),
  CONSTRAINT `WebNodeContentWebBlock_ibfk_1` FOREIGN KEY (`webNodeContentId`) REFERENCES `WebNodeContent` (`id`),
  CONSTRAINT `WebNodeContentWebBlock_ibfk_2` FOREIGN KEY (`webBlockId`) REFERENCES `WebBlock` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


ALTER TABLE `College` ADD (`collegeKey` VARCHAR(25));
ALTER TABLE `WebSite` ADD (`siteKey` VARCHAR(25));
ALTER TABLE `WebNodeType` ADD (`webSiteId` BIGINT);
ALTER TABLE `WebNodeType` ADD (`templateKey` VARCHAR(50));
ALTER TABLE `WebNodeType` ADD (`numberOfRegions` int(11));

ALTER TABLE `WebNodeType` ADD CONSTRAINT `WebNodeType_ibfk_2` FOREIGN KEY (`webSiteId`) REFERENCES `WebSite` (`id`);