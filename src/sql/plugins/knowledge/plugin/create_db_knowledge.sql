-- Structure for table knowledge_model
DROP TABLE IF EXISTS knowledge_model;
CREATE TABLE knowledge_model (
  id_model int AUTO_INCREMENT,
  api_key longtext NOT NULL,
  url varchar(255) DEFAULT '' NOT NULL,
  PRIMARY KEY (id_model)
);

-- Structure for table knowledge_project
DROP TABLE IF EXISTS knowledge_project;
CREATE TABLE knowledge_project (
  id_project int AUTO_INCREMENT,
  title longtext NOT NULL,
  description varchar(255) DEFAULT '' NOT NULL,
  model_id int DEFAULT '0' NOT NULL,
  prompt_system_id int DEFAULT '0' NOT NULL,
  PRIMARY KEY (id_project)
);

-- Structure for table knowledge_document
DROP TABLE IF EXISTS knowledge_document;
CREATE TABLE knowledge_document (
  id int AUTO_INCREMENT,
  document_name longtext NOT NULL,
  document_data longtext NOT NULL,
  project_id int DEFAULT '0' NOT NULL,
  is_embedding boolean DEFAULT '0' NOT NULL,
  PRIMARY KEY (id)
);

-- Structure for table knowledge_tag
DROP TABLE IF EXISTS knowledge_tag;
CREATE TABLE knowledge_tag (
  id_tags int AUTO_INCREMENT,
  tag_name varchar(255) DEFAULT '' NOT NULL,
  PRIMARY KEY (id_tags)
);

-- Structure for table knowledge_embedding
DROP TABLE IF EXISTS knowledge_embedding;
CREATE TABLE knowledge_embedding (
  id_embedding int AUTO_INCREMENT,
  project_id int DEFAULT '0' NOT NULL,
  vectors longtext NOT NULL,
  metadata longtext,
  text_segment longtext NOT NULL,
  file_id longtext NOT NULL,
  PRIMARY KEY (id_embedding)
);

-- Structure for table knowledge_fine_tuning
DROP TABLE IF EXISTS knowledge_fine_tuning;
CREATE TABLE knowledge_fine_tuning (
  id_fine_tuning int AUTO_INCREMENT,
  project_id int DEFAULT '0' NOT NULL,
  role longtext NOT NULL,
  content longtext NOT NULL,
  `order` int DEFAULT '0' NOT NULL,
  conversation_id int DEFAULT '0' NOT NULL,
  PRIMARY KEY (id_fine_tuning)
);
