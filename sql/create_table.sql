-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: lefriend
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `tag_name` varchar(256) DEFAULT NULL COMMENT '标签名称',
  `user_id` bigint DEFAULT NULL COMMENT '提交者 id',
  `parent_id` bigint DEFAULT NULL COMMENT '父标签 id',
  `is_parent` tinyint DEFAULT NULL COMMENT '0 - 不是，1 - 父标签',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0，1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tagName__uniqueIndex` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (2,'Java',1,0,0,'2024-06-12 20:10:00','2024-06-13 10:54:03',0),(3,'Python',1,0,0,'2024-06-12 20:10:39','2024-06-13 10:54:03',0),(4,'C++',1,0,0,'2024-06-12 20:10:39','2024-06-13 10:54:03',0),(6,'男',1,0,0,'2024-06-12 20:11:07','2024-06-13 10:54:03',0),(7,'女',1,0,0,'2024-06-12 20:11:07','2024-06-13 10:54:03',0);
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '队伍名称',
  `user_id` bigint DEFAULT NULL COMMENT '创建人',
  `password` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加入密码',
  `description` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '队伍描述',
  `status` int NOT NULL DEFAULT '0' COMMENT '队伍可见：0-公开，1-私有，2-加密',
  `max_num` int NOT NULL DEFAULT '5' COMMENT '队伍最大人数',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0，1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='队伍';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team`
--

LOCK TABLES `team` WRITE;
/*!40000 ALTER TABLE `team` DISABLE KEYS */;
INSERT INTO `team` VALUES (15,'学生会',4,NULL,'卡塞尔学院学生团体，在前任主席恺撒·加图索的带领下崛起，并能与卡塞尔学院最传统的兄弟会“狮心会”相抗衡，连续赢得两次“自由一日” [1]。学生会是个奉行精英主义的社团',0,4,'2024-08-19 16:30:19','2024-05-08 17:47:02','2024-06-11 22:30:38',0),(21,'狮心会',1,'','秘党内最古老的组织，很久以前是秘党的核心机构，但在“夏之哀悼”事件后几乎全灭，仅有昂热幸存。',0,6,'2024-07-31 16:00:00','2024-06-11 23:52:47','2024-06-22 13:48:28',0),(22,'湮没之井',6,'','卡塞尔学院仓库“冰窖”的最底层，是一个被几千万年的流水侵蚀出来的地下岩洞，也是湮没一切的地方。',0,11,'2024-07-31 16:00:00','2024-06-11 23:55:51','2024-06-22 13:58:07',0),(23,'蛇岐八家',1,'123456','在二战结束后由昂热前往日本签订了合作协议，并一手组建日本分部执行局，在“龙渊计划”后短暂叛变，“红井之战”中重新与秘党合作，共同击败刚刚复苏的白王。',2,6,'2024-07-31 16:00:00','2024-06-12 00:12:36','2024-06-13 18:00:11',0);
/*!40000 ALTER TABLE `team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(256) DEFAULT NULL COMMENT '用户名',
  `user_account` varchar(256) DEFAULT NULL COMMENT '登录账号',
  `user_password` varchar(256) DEFAULT NULL COMMENT '登录密码',
  `avatar_url` varchar(1024) DEFAULT NULL COMMENT '用户头像链接',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0 - 男，1 - 女',
  `phone` varchar(256) DEFAULT NULL COMMENT '电话号码',
  `email` varchar(256) DEFAULT NULL COMMENT '用户邮箱',
  `user_status` int NOT NULL DEFAULT '0' COMMENT '账号状态：0 - 正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0，1',
  `user_role` int NOT NULL DEFAULT '0' COMMENT '用户身份：0 - 成员，1 - 管理员',
  `planet_code` varchar(128) DEFAULT NULL COMMENT '星球编号',
  `tags` varchar(1024) DEFAULT NULL COMMENT '用户标签列表json',
  `profile` varchar(512) DEFAULT NULL COMMENT '个人简介',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_userAccount_index` (`user_account`)
) ENGINE=InnoDB AUTO_INCREMENT=10010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'李嘉图','admin','94944c48331237ad2885a97f0d0d72a0','http://localhost:3000/images/1.png',-1,'13712481921','1234918240@aba.com',0,'2023-04-16 09:52:20','2024-06-22 14:00:32',0,1,'1','[\"男\",\"java\",\"python\",\"c++\"]','我是一个好人'),(4,'楚子航','chuzihang','94944c48331237ad2885a97f0d0d72a0','http://localhost:3000/images/2.png',0,'13712481921','1234918240@aba.com',0,'2023-04-17 18:47:01','2024-06-22 13:45:13',0,0,'3','[\"男\",\"java\"]','我是一个好人'),(5,'凯撒','kaisa','94944c48331237ad2885a97f0d0d72a0','http://localhost:3000/images/3.png',0,'13712481921','1234918240@aba.com',0,'2023-04-17 20:19:04','2024-06-22 13:45:13',0,0,'4','[\"男\",\"c++\"]','我是一个好人'),(6,'诺诺','nuonuo','94944c48331237ad2885a97f0d0d72a0','http://localhost:3000/images/4.png',1,'13712481921','1234918240@aba.com',0,'2023-04-18 17:03:26','2024-06-22 13:45:13',0,0,'5','[\"女\",\"python\"]','我是一个好人'),(9,'古德里安','goodlian','94944c48331237ad2885a97f0d0d72a0','http://localhost:3000/images/5.png',0,'13712481921','1234918240@aba.com',0,'2023-04-19 13:14:03','2024-06-22 13:45:13',0,0,'2','[\"kotlin\"]','我是一个好人');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_team`
--

DROP TABLE IF EXISTS `user_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_team` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `team_id` bigint DEFAULT NULL COMMENT '队伍id',
  `join_time` datetime DEFAULT NULL COMMENT '加入时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0，1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=269 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户队伍关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_team`
--

LOCK TABLES `user_team` WRITE;
/*!40000 ALTER TABLE `user_team` DISABLE KEYS */;
INSERT INTO `user_team` VALUES (12,4,15,'2024-05-20 13:58:10','2024-05-20 13:58:10','2024-05-23 19:55:38',0),(27,1,21,'2024-06-11 23:52:48','2024-06-11 23:52:47','2024-06-22 13:48:53',0),(28,1,22,'2024-06-11 23:55:51','2024-06-11 23:55:51','2024-06-22 13:58:07',1),(29,1,23,'2024-06-12 00:12:36','2024-06-12 00:12:36','2024-06-12 00:12:36',0),(265,6,22,'2024-06-22 13:57:11','2024-06-22 13:57:10','2024-06-22 13:57:10',0),(266,6,21,'2024-06-22 13:57:12','2024-06-22 13:57:11','2024-06-22 13:57:11',0),(267,6,15,'2024-06-22 13:57:12','2024-06-22 13:57:12','2024-06-22 13:57:12',0),(268,1,15,'2024-06-22 13:58:22','2024-06-22 13:58:21','2024-06-22 13:58:21',0);
/*!40000 ALTER TABLE `user_team` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-22 14:04:46
