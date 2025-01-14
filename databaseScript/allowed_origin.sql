CREATE TABLE `dms`.`lut_allowed_origin` (
  `id` INT NOT NULL COMMENT 'primary key of the table',
  `allowed_origin` VARCHAR(45) NOT NULL COMMENT 'origin allowed by cors in dms',
  `created_by` VARCHAR(45) NULL DEFAULT 'Admin' COMMENT 'created by user',
  `created_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'row created date',
  `updated_by` VARCHAR(45) NULL DEFAULT 'Admin' COMMENT 'updated by user',
  `updated_date` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'row updated date',
  PRIMARY KEY (`id`))
COMMENT = 'list of origins allowed by cors';

INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (1, 'http://localhost:4200');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (2, 'http://localhost:8080');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (3, 'https://dev.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (4, 'https://dev.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (5, 'https://dev-employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (6, 'https://dev-myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (7, 'https://dev.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (8, 'https://dev-bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (9, 'https://dev-finansme.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (10, 'https://uat.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (11, 'https://uat.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (12, 'https://uat-employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (13, 'https://uat-myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (14, 'https://uat.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (15, 'https://uat-bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (16, 'https://uat-finansme.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (17, 'https://ebf.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (18, 'https://ebf.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (19, 'https://ebf-employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (20, 'https://ebf-myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (21, 'https://ebf.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (22, 'https://ebf-bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (23, 'https://ebf-finansme.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (24, 'https://www.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (25, 'https://www.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (26, 'https://employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (27, 'https://myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (28, 'https://www.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (29, 'https://bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (30, 'https://finansme.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (31, 'http://dev.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (32, 'http://dev.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (33, 'http://dev-employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (34, 'http://dev-myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (35, 'http://dev.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (36, 'http://dev-bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (37, 'http://dev-finansme.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (38, 'http://uat.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (39, 'http://uat.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (40, 'http://uat-employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (41, 'http://uat-myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (42, 'http://uat.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (43, 'http://uat-bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (44, 'http://uat-finansme.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (45, 'http://ebf.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (46, 'http://ebf.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (47, 'http://ebf-employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (48, 'http://ebf-myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (49, 'http://ebf.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (50, 'http://ebf-bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (51, 'http://ebf-finansme.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (52, 'http://www.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (53, 'http://www.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (54, 'http://employee.finansme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (55, 'http://myworkspace.power2sme.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (56, 'http://www.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (57, 'http://bizfunds.fundskraft.com');
INSERT INTO `dms`.`lut_allowed_origin` (`id`, `allowed_origin`) VALUES (58, 'http://finansme.fundskraft.com');


