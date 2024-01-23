
--
-- Structure for table knowledge_bot
--

DROP TABLE IF EXISTS knowledge_bot;
CREATE TABLE knowledge_bot (
id_bots int AUTO_INCREMENT,
name varchar(255) default '' NOT NULL,
description long varchar,
story long varchar,
dataset_id int default '0',
toolset_id int default '0',
model_id varchar(255) default '' NOT NULL,
type_id long varchar NOT NULL,
PRIMARY KEY (id_bots)
);

--
-- Structure for table knowledge_dataset
--

DROP TABLE IF EXISTS knowledge_dataset;
CREATE TABLE knowledge_dataset (
id_dataset int AUTO_INCREMENT,
name varchar(255) default '' NOT NULL,
description long varchar,
record_max_tokens int default '0' NOT NULL,
search_max_record int default '0' NOT NULL,
search_max_tokens int default '0' NOT NULL,
match_instruction long varchar NOT NULL,
mismatch_instruction long varchar NOT NULL,
PRIMARY KEY (id_dataset)
);

--
-- Structure for table knowledge_dataset_file
--

DROP TABLE IF EXISTS knowledge_dataset_file;
CREATE TABLE knowledge_dataset_file (
id_dataset_files int AUTO_INCREMENT,
name long varchar NOT NULL,
description long varchar,
dataset_id int default '0',
file_key long varchar NOT NULL,
PRIMARY KEY (id_dataset_files)
);

--
-- Structure for table knowledge_toolset
--

DROP TABLE IF EXISTS knowledge_toolset;
CREATE TABLE knowledge_toolset (
id_tool_set int AUTO_INCREMENT,
name long varchar NOT NULL,
description long varchar,
PRIMARY KEY (id_tool_set)
);

--
-- Structure for table knowledge_toolset_ability
--

DROP TABLE IF EXISTS knowledge_toolset_ability;
CREATE TABLE knowledge_toolset_ability (
id_tool_set_ability int AUTO_INCREMENT,
name long varchar NOT NULL,
description long varchar,
instruction long varchar NOT NULL,
toolset_id int default '0' NOT NULL,
PRIMARY KEY (id_tool_set_ability)
);

--
-- Structure for table knowledge_bot_session
--

DROP TABLE IF EXISTS knowledge_bot_session;
CREATE TABLE knowledge_bot_session (
id_bot_session int AUTO_INCREMENT,
user_id int default '0' NOT NULL,
creation_date date NOT NULL,
content long varchar NOT NULL,
bot_id int default '0' NOT NULL,
user_access_code long varchar NOT NULL,
session_id long varchar NOT NULL,
PRIMARY KEY (id_bot_session)
);
