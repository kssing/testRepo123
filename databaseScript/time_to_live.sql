ALTER TABLE `dms`.`uploaded_file_info` 
ADD COLUMN `time_to_live` INT NOT NULL DEFAULT 0 AFTER `deleted_at`;
