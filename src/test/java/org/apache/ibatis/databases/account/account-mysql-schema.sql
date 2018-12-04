-- # 1.创建数据库
CREATE
DATABASE
IF
NOT
EXISTS
`test_db`
DEFAULT
CHARACTER
SET
utf8mb4
COLLATE
utf8mb4_unicode_ci;
-- # 2.创建数据表
DROP TABLE IF EXISTS `test_table`;
CREATE TABLE test_table
(
  tableId    BIGINT(20) NOT NULL AUTO_INCREMENT,
  name       varchar(500) NOT NULL DEFAULT '0',
  blance     BIGINT(20) NOT NULL DEFAULT '0',
  lastUpdate TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (tableId),
  KEY        idx_type_serial ( name)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
