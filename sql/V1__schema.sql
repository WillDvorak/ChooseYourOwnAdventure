-- V1__schema.sql
-- Core schema for the text-based adventure game


USE textquest;

-- =========================
-- SCENES (story nodes)
-- =========================
CREATE TABLE IF NOT EXISTS scenes (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  code         VARCHAR(64)     NOT NULL,              -- human-friendly stable key
  title        VARCHAR(128)    NOT NULL,
  body         TEXT            NOT NULL,
  is_terminal  TINYINT(1)      NOT NULL DEFAULT 0,    -- marks end scenes
  created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_scene_code (code)
) ENGINE=InnoDB;

-- =========================
-- CHOICES (edges between scenes)
-- =========================
CREATE TABLE IF NOT EXISTS choices (
  id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  scene_id          BIGINT UNSIGNED NOT NULL,         -- from this scene
  label             VARCHAR(255)    NOT NULL,         -- text shown to player
  target_scene_code VARCHAR(64)     NOT NULL,         -- points to scenes.code
  requires_flag     VARCHAR(64)     NULL,             -- optional gating flag
  sets_flag         VARCHAR(64)     NULL,             -- optional state change
  created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_choices_scene_id (scene_id),
  KEY idx_choices_target_code (target_scene_code),
  CONSTRAINT fk_choices_scene
    FOREIGN KEY (scene_id) REFERENCES scenes(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================
-- ITEMS (game items)
-- =========================
CREATE TABLE IF NOT EXISTS items (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  code                VARCHAR(64)     NOT NULL,       -- unique item identifier
  name                VARCHAR(128)    NOT NULL,       -- display name
  description         TEXT            NULL,            -- item description
  item_type           VARCHAR(64)     NOT NULL,       -- weapon, consumable, key, etc.
  effects_json        JSON            NULL,           -- effects: {"hp_change": 10, "max_hp_change": 0}
  is_consumable       TINYINT(1)      NOT NULL DEFAULT 0, -- can be consumed/used
  created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_item_code (code),
  KEY idx_item_type (item_type)
) ENGINE=InnoDB;

-- =========================
-- GAME SESSIONS (player progress)
-- =========================
CREATE TABLE IF NOT EXISTS game_sessions (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  player_name         VARCHAR(64)     NOT NULL,
  current_scene_code  VARCHAR(64)     NOT NULL,       -- scenes.code
  flags_json          JSON            NOT NULL,       -- stores inventory/flags
  hp                  INT             NOT NULL DEFAULT 100,
  max_hp              INT             NOT NULL DEFAULT 100,
  created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_session_scene_code (current_scene_code),
  CHECK (JSON_VALID(flags_json))
) ENGINE=InnoDB;

-- =========================
-- PLAYER INVENTORY (junction table)
-- =========================
CREATE TABLE IF NOT EXISTS player_inventory (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  session_id          BIGINT UNSIGNED NOT NULL,       -- game_sessions.id
  item_id             BIGINT UNSIGNED NOT NULL,       -- items.id
  quantity            INT             NOT NULL DEFAULT 1,
  created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_session_item (session_id, item_id),
  KEY idx_inventory_session (session_id),
  KEY idx_inventory_item (item_id),
  CONSTRAINT fk_inventory_session
    FOREIGN KEY (session_id) REFERENCES game_sessions(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_inventory_item
    FOREIGN KEY (item_id) REFERENCES items(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;
