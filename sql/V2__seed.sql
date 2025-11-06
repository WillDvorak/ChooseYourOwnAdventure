-- V2__seed.sql
-- Initial story content for the adventure game

USE textquest;

-- ---------- Scenes ----------
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('intro',   'The Long One',     'You wake at a campfire. A narrow path leads into a pine forest. A faint glow flickers to the east.', 0),
('forest',  'Under Tall Pines', 'The forest hums. You notice footprints and a torn map scrap.',                                        0),
('clearing', 'Forest Clearing', 'A small clearing opens before you. Sunlight filters through the canopy. A wooden sign points west.', 0),
('cave',    'Cave Mouth',       'A damp cave descends. It smells of iron. Your torch sputters.',                                       0),
('tunnel',  'Dark Tunnel',      'You squeeze through a narrow passage. The air grows thin. Strange markings cover the walls.', 0),
('treasure','Hidden Cache',     'You pry open a chest: old coins and a small brass key.',                                              0),
('exit',    'The Way Out',      'Using the brass key, you unlock a hidden door. Bright daylight streams in. You have escaped!', 1);

-- ---------- Choices from intro ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head into the forest', 'forest', NULL, NULL
FROM scenes WHERE code = 'intro';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Walk toward the glow', 'cave', NULL, 'torch'
FROM scenes WHERE code = 'intro';

-- ---------- Forest ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Follow the footprints', 'cave', NULL, NULL
FROM scenes WHERE code = 'forest';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Search for supplies', 'forest', NULL, 'torch'
FROM scenes WHERE code = 'forest';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head deeper into the forest', 'clearing', NULL, NULL
FROM scenes WHERE code = 'forest';

-- ---------- Clearing ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Follow the sign west', 'cave', NULL, NULL
FROM scenes WHERE code = 'clearing';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Return to the forest', 'forest', NULL, NULL
FROM scenes WHERE code = 'clearing';

-- ---------- Cave (needs torch to reach treasure) ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Descend with your torch', 'treasure', 'torch', 'gold'
FROM scenes WHERE code = 'cave';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Explore a side passage', 'tunnel', NULL, NULL
FROM scenes WHERE code = 'cave';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Retreat to the campfire', 'intro', NULL, NULL
FROM scenes WHERE code = 'cave';

-- ---------- Tunnel ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Continue forward', 'treasure', NULL, 'gold'
FROM scenes WHERE code = 'tunnel';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Return to cave entrance', 'cave', NULL, NULL
FROM scenes WHERE code = 'tunnel';

-- ---------- Treasure (now leads to exit) ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Use the brass key', 'exit', 'gold', NULL
FROM scenes WHERE code = 'treasure';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Go back through the cave', 'cave', NULL, NULL
FROM scenes WHERE code = 'treasure';
