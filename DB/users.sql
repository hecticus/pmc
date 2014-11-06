CREATE TABLE `security_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `active` tinyint(1) DEFAULT '0',
  `email_validated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `user_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `linked_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `provider_user_id` varchar(255) DEFAULT NULL,
  `provider_key` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_linked_account_user_7` (`user_id`),
  CONSTRAINT `fk_linked_account_user_7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `token_action` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) DEFAULT NULL,
  `target_user_id` bigint(20) DEFAULT NULL,
  `type` varchar(2) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `expires` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_token_action_token` (`token`),
  KEY `ix_token_action_targetUser_16` (`target_user_id`),
  CONSTRAINT `fk_token_action_targetUser_16` FOREIGN KEY (`target_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `users_security_role` (
  `users_id` bigint(20) NOT NULL,
  `security_role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`users_id`,`security_role_id`),
  KEY `fk_users_security_role_security_role_02` (`security_role_id`),
  CONSTRAINT `fk_users_security_role_security_role_02` FOREIGN KEY (`security_role_id`) REFERENCES `security_role` (`id`),
  CONSTRAINT `fk_users_security_role_users_01` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `users_user_permission` (
  `users_id` bigint(20) NOT NULL,
  `user_permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`users_id`,`user_permission_id`),
  KEY `fk_users_user_permission_user_permission_02` (`user_permission_id`),
  CONSTRAINT `fk_users_user_permission_users_01` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_users_user_permission_user_permission_02` FOREIGN KEY (`user_permission_id`) REFERENCES `user_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE `pmc`.`configs` CHANGE COLUMN `key` `config_key` VARCHAR(50) NULL DEFAULT '' ;

CREATE TABLE `pmc`.`pushed_events` (
  `id_pushed_event` INT(11) NOT NULL,
  `id_app` INT(11) NOT NULL,
  `message` VARCHAR(100) NOT NULL,
  `time` BIGINT(20) NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`id_pushed_event`),
  INDEX `fk_app_idx` (`id_app` ASC),
  CONSTRAINT `fk_app`
    FOREIGN KEY (`id_app`)
    REFERENCES `pmc`.`app` (`id_app`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

ALTER TABLE `pmc`.`pushed_events` 
CHANGE COLUMN `id_pushed_event` `id_pushed_event` INT(11) NOT NULL AUTO_INCREMENT ;