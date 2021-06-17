CREATE TABLE `rooms`(
 `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
 `r_name` VARCHAR(255) NOT NULL UNIQUE COLLATE 'utf8_hungarian_ci',
 `r_length` DOUBLE NOT NULL,
 `r_width` DOUBLE NOT NULL,
PRIMARY KEY (`id`) USING BTREE)
COLLATE='utf8_hungarian_ci' ENGINE=InnoDB;

CREATE TABLE meetings (
 m_id BIGINT(20) NOT NULL AUTO_INCREMENT,
 room_id BIGINT(20) NOT NULL,
 owner varchar(255),
 start_time timestamp NOT NULL,
 duration int NOT NULL,
 primary key (m_id) USING BTREE,
 foreign key(room_id)
references employees.rooms (id) ON DELETE CASCADE ON UPDATE CASCADE)
COLLATE='utf8_hungarian_ci' ENGINE=InnoDB;
