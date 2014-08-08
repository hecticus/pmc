CREATE TABLE `pmc`.`instances` (
  `id_instance` INT NOT NULL AUTO_INCREMENT,
  `ip` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `running` INT NOT NULL DEFAULT 0,
  `test` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_instance`))
ENGINE = InnoDB;
