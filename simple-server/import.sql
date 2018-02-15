CREATE TABLE accounts(
    id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email VARCHAR(320) NOT NULL UNIQUE,
    password VARCHAR(32) NOT NULL,
    successes MEDIUMINT UNSIGNED DEFAULT 0 COMMENT 'Количество всего успешных установок приложений этим аккаунтом',
    failures MEDIUMINT UNSIGNED DEFAULT 0 COMMENT 'Количество ошибок со входом в аккаунт',
    PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=UTF8 COMMENT 'Таблица с аккаунтами';

CREATE TABLE devices(
    id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(16) NOT NULL UNIQUE COMMENT 'Уникальное имя устройства',
    battery TINYINT UNSIGNED DEFAULT NULL,
    ram TINYINT UNSIGNED DEFAULT NULL COMMENT 'Процент свободной оперативной памяти',
    online DATETIME DEFAULT NULL COMMENT 'Время последнего обращения на сервер',
    PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=UTF8 COMMENT 'Таблица для мониторинга состояния устройств';

CREATE TABLE tasks(
    id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    package VARCHAR(320) NOT NULL COMMENT 'Не ссылка, а имя пакета приложения',
    downloads MEDIUMINT UNSIGNED NOT NULL COMMENT 'Требуемое количество установок приложения',
    successes MEDIUMINT UNSIGNED DEFAULT 0 COMMENT 'Количество успешных установок приложения',
    failures MEDIUMINT UNSIGNED DEFAULT 0 COMMENT 'Количество ошибок при скачивании приложения',
    suspend BIT DEFAULT 0,
    time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=UTF8 COMMENT 'Основная рабочая таблица';

CREATE TABLE downloads_executable(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    device SMALLINT UNSIGNED NOT NULL,
    task MEDIUMINT UNSIGNED NOT NULL,
    account MEDIUMINT UNSIGNED NOT NULL,
    filename VARCHAR(8) NOT NULL COMMENT 'Файл в папке приложения с логами',
    time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (device) REFERENCES devices(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (task) REFERENCES tasks(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (account) REFERENCES accounts(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB CHARACTER SET=UTF8 COMMENT 'Техническая таблица. Выполняемые установки';


CREATE TABLE downloads_successful(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    device SMALLINT UNSIGNED NOT NULL,
    task MEDIUMINT UNSIGNED NOT NULL,
    account MEDIUMINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (device) REFERENCES devices(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (task) REFERENCES tasks(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (account) REFERENCES accounts(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB CHARACTER SET=UTF8 COMMENT 'Техническая таблица. Успешные установки';


CREATE TABLE downloads_failed(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    device SMALLINT UNSIGNED NOT NULL,
    task MEDIUMINT UNSIGNED DEFAULT NULL,
    account MEDIUMINT UNSIGNED DEFAULT NULL,
    filename VARCHAR(8) NOT NULL COMMENT 'Файл в папке приложения с логами',
    PRIMARY KEY (id),
    FOREIGN KEY (device) REFERENCES devices(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (task) REFERENCES tasks(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (account) REFERENCES accounts(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB CHARACTER SET=UTF8 COMMENT 'Техническая таблица. Неудачные установки по причине ошибок в имени пакета приложения или правильности данных аккаунта';
