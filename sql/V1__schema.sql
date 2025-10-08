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
-- GAME SESSIONS (player progress)
-- =========================
CREATE TABLE IF NOT EXISTS game_sessions (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  player_name         VARCHAR(64)     NOT NULL,
  current_scene_code  VARCHAR(64)     NOT NULL,       -- scenes.code
  flags_json          JSON            NOT NULL,       -- stores inventory/flags
  created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_session_scene_code (current_scene_code),
  CHECK (JSON_VALID(flags_json))
) ENGINE=InnoDB;
