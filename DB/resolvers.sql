CREATE TABLE `pmc`.`resolvers` (
  `id_resolver` INT NOT NULL AUTO_INCREMENT,
  `class_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_resolver`))
ENGINE = InnoDB;


ALTER TABLE `pmc`.`app` 
ADD COLUMN `id_resolver` INT NULL AFTER `debug`,
ADD INDEX `fk_id_resolver_idx` (`id_resolver` ASC);
ALTER TABLE `pmc`.`app` 
ADD CONSTRAINT `fk_id_resolver`
  FOREIGN KEY (`id_resolver`)
  REFERENCES `pmc`.`resolvers` (`id_resolver`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
