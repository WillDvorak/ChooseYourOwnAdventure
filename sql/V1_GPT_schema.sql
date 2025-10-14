-- ============================================================
-- ECHOES OF THE SHATTERED REALMS — COMPLETE DATABASE SCHEMA
-- PostgreSQL DDL (ready to run)
-- ============================================================

-- Drop existing objects (for reruns)
DROP SCHEMA IF EXISTS echoes_of_the_shattered_realms CASCADE;
CREATE SCHEMA echoes_of_the_shattered_realms;
SET search_path TO echoes_of_the_shattered_realms;

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE pt_status AS ENUM ('in_progress', 'completed', 'failed', 'abandoned');
CREATE TYPE ending_type AS ENUM ('true_ending', 'power_ending', 'bittersweet', 'loop');

-- ============================================================
-- CORE WORLD TABLES
-- ============================================================

CREATE TABLE environments (
    id              SERIAL PRIMARY KEY,
    slug            TEXT NOT NULL UNIQUE,
    name            TEXT NOT NULL,
    description     TEXT NOT NULL,
    display_order   INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_environments_order ON environments(display_order);

CREATE TABLE relics (
    id              SERIAL PRIMARY KEY,
    slug            TEXT NOT NULL UNIQUE,
    name            TEXT NOT NULL,
    description     TEXT NOT NULL,
    rarity          TEXT,
    metadata        JSONB DEFAULT '{}'::jsonb,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- ACTIONS & SCENARIOS
-- ============================================================

CREATE TABLE actions (
    id              SERIAL PRIMARY KEY,
    environment_id  INT NOT NULL REFERENCES environments(id) ON DELETE CASCADE,
    code            TEXT NOT NULL,
    title           TEXT NOT NULL,
    description     TEXT NOT NULL,
    ui_hint         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(environment_id, code)
);

CREATE INDEX idx_actions_env ON actions(environment_id);

CREATE TABLE scenarios (
    id              SERIAL PRIMARY KEY,
    name            TEXT NOT NULL,
    environment_id  INT REFERENCES environments(id) ON DELETE SET NULL,
    description     TEXT NOT NULL,
    consequence     JSONB DEFAULT '{}'::jsonb,
    is_good         BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_scenarios_env ON scenarios(environment_id);

CREATE TABLE action_scenarios (
    id              SERIAL PRIMARY KEY,
    action_id       INT NOT NULL REFERENCES actions(id) ON DELETE CASCADE,
    scenario_id     INT NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
    weight          NUMERIC NOT NULL DEFAULT 1.0,
    condition       JSONB DEFAULT '{}'::jsonb,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(action_id, scenario_id)
);

CREATE INDEX idx_action_scenarios_action ON action_scenarios(action_id);

-- ============================================================
-- ENDINGS
-- ============================================================

CREATE TABLE endings (
    id              SERIAL PRIMARY KEY,
    code            TEXT NOT NULL UNIQUE,
    title           TEXT NOT NULL,
    description     TEXT NOT NULL,
    ending_type     ending_type NOT NULL,
    required_conditions JSONB DEFAULT '{}'::jsonb
);

-- ============================================================
-- ALLIANCES (optional relationships)
-- ============================================================

CREATE TABLE alliances (
    id              SERIAL PRIMARY KEY,
    slug            TEXT NOT NULL UNIQUE,
    name            TEXT NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- PLAYERS & PLAYTHROUGHS
-- ============================================================

CREATE TABLE players (
    id              BIGSERIAL PRIMARY KEY,
    username        TEXT NOT NULL UNIQUE,
    display_name    TEXT,
    email           TEXT,
    metadata        JSONB DEFAULT '{}'::jsonb,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE playthroughs (
    id                  BIGSERIAL PRIMARY KEY,
    player_id           BIGINT REFERENCES players(id) ON DELETE SET NULL,
    started_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at         TIMESTAMPTZ,
    status              pt_status NOT NULL DEFAULT 'in_progress',
    current_environment INT REFERENCES environments(id),
    current_step        INTEGER NOT NULL DEFAULT 0,
    flags               JSONB DEFAULT '{}'::jsonb,
    metadata            JSONB DEFAULT '{}'::jsonb,
    ending_id           INT REFERENCES endings(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_playthroughs_player ON playthroughs(player_id);
CREATE INDEX idx_playthroughs_status ON playthroughs(status);

CREATE TABLE playthrough_alliances (
    id              SERIAL PRIMARY KEY,
    playthrough_id  BIGINT NOT NULL REFERENCES playthroughs(id) ON DELETE CASCADE,
    alliance_id     INT NOT NULL REFERENCES alliances(id) ON DELETE CASCADE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(playthrough_id, alliance_id)
);

-- ============================================================
-- PLAYTHROUGH HISTORY: CHOICES, INVENTORY, EVENTS
-- ============================================================

CREATE TABLE playthrough_choices (
    id                  BIGSERIAL PRIMARY KEY,
    playthrough_id      BIGINT NOT NULL REFERENCES playthroughs(id) ON DELETE CASCADE,
    step_number         INT NOT NULL,
    timestamp           TIMESTAMPTZ NOT NULL DEFAULT now(),
    environment_id      INT REFERENCES environments(id),
    action_id           INT REFERENCES actions(id),
    scenario_id         INT REFERENCES scenarios(id),
    outcome_consequence JSONB DEFAULT '{}'::jsonb,
    notes               TEXT,
    UNIQUE(playthrough_id, step_number)
);

CREATE INDEX idx_choices_playthrough ON playthrough_choices(playthrough_id);
CREATE INDEX idx_choices_env ON playthrough_choices(environment_id);

CREATE TABLE playthrough_inventory (
    id                  BIGSERIAL PRIMARY KEY,
    playthrough_id      BIGINT NOT NULL REFERENCES playthroughs(id) ON DELETE CASCADE,
    relic_id            INT NOT NULL REFERENCES relics(id) ON DELETE RESTRICT,
    acquired_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    source_action_id    INT REFERENCES actions(id),
    metadata            JSONB DEFAULT '{}'::jsonb,
    UNIQUE(playthrough_id, relic_id)
);

CREATE INDEX idx_inventory_playthrough ON playthrough_inventory(playthrough_id);

CREATE TABLE playthrough_events (
    id                  BIGSERIAL PRIMARY KEY,
    playthrough_id      BIGINT NOT NULL REFERENCES playthroughs(id) ON DELETE CASCADE,
    occurred_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    event_type          TEXT NOT NULL,
    payload             JSONB DEFAULT '{}'::jsonb
);

-- ============================================================
-- VIEWS
-- ============================================================

CREATE VIEW vw_playthrough_state AS
SELECT
  p.id AS playthrough_id,
  p.player_id,
  p.status,
  p.current_environment,
  e.name AS current_environment_name,
  p.current_step,
  p.flags,
  COALESCE(jsonb_agg(pi.relic_id) FILTER (WHERE pi.relic_id IS NOT NULL), '[]') AS relic_ids
FROM playthroughs p
LEFT JOIN environments e ON e.id = p.current_environment
LEFT JOIN playthrough_inventory pi ON pi.playthrough_id = p.id
GROUP BY p.id, e.name;

-- ============================================================
-- SEED DATA
-- ============================================================

INSERT INTO environments (slug, name, description, display_order) VALUES
('mistwood_forest', 'The Mistwood Forest', 'An ancient forest teeming with whispering spirits, hidden ruins, and shifting paths.', 1),
('ember_city', 'The Ember City', 'A molten metropolis of forges, rebellion, and greed, built around a volcano’s heart.', 2),
('crystal_archives', 'The Crystal Archives', 'A vast library of living crystals storing knowledge, guarded by sentient scholars.', 3),
('drowned_sanctum', 'The Drowned Sanctum', 'A sunken temple city beneath the ocean, where echoes of lost gods still breathe.', 4),
('astral_gate', 'The Astral Gate', 'A realm between realms, the nexus of fate where all choices converge.', 5);

INSERT INTO relics (slug, name, description, rarity, metadata) VALUES
('forest_sigil','Forest Sigil','An ancient wooden sigil that calms spirits and can open hidden paths.', 'legendary', '{"effects": {"calm_waters": true, "opens_astral_gate": false}}'),
('firebrand','Firebrand','A forged weapon that can burn through magical bindings.', 'rare', '{"effects": {"burn_bindings": true}}');

INSERT INTO endings (code, title, description, ending_type, required_conditions) VALUES
('true_ending','World Restored','You united the realms and restored balance to the Shattered Realms.', 'true_ending', '{"min_relics": 3}'),
('power_ending','Rule of Chaos','You absorbed the realms’ power and became a god — the world fades.', 'power_ending', '{"must_have_flag":"took_power"}'),
('bittersweet','One Realm Survives','You chose to rebuild a single realm at the cost of the others.', 'bittersweet', '{}'),
('loop','The Loop','You walked away and awakened back in the Mistwood — the cycle restarts.', 'loop', '{}');

-- Example Mistwood Forest action/scenario
INSERT INTO actions (environment_id, code, title, description, ui_hint)
SELECT id, 'follow_whispers','Follow the Whispers','Track the voices that call your name.','risky'
FROM environments WHERE slug='mistwood_forest';

INSERT INTO scenarios (name, environment_id, description, consequence, is_good)
SELECT 'spectral_guide_offer', id,
'You find a spectral guide who offers to lead you to truth at a price.',
'{"set_flag":"guided_by_specter","grant_relic":"forest_sigil"}',
true
FROM environments WHERE slug='mistwood_forest';

INSERT INTO action_scenarios (action_id, scenario_id, weight, condition)
SELECT a.id, s.id, 1.0, '{}'::jsonb
FROM actions a JOIN scenarios s ON s.name='spectral_guide_offer'
WHERE a.code='follow_whispers';


