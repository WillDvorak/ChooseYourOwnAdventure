-- V2__seed.sql
-- Initial story content for the adventure game

USE textquest;

-- ---------- Scenes ----------
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('intro',   'The Long One',     'You wake at a campfire. A narrow path leads into a pine forest. A faint glow flickers to the east.', 0),
('forest',  'Under Tall Pines', 'The forest hums. You notice footprints and a torn map scrap.',                                        0),
('cave',    'Cave Mouth',       'A damp cave descends. It smells of iron. Your torch sputters.',                                       0),
('treasure','Hidden Cache',     'You pry open a chest: old coins and a small brass key.',                                              1),
('danger',  'Dangerous Path',   'You stumble upon a pack of wolves! They bare their teeth menacingly.',                                0),
('heal',    'Healing Spring',   'You discover a clear spring. The water glows faintly and appears magical.',                           0),
('death',   'Game Over',        'Your health has dropped to zero. Your adventure ends here.',                                          1);

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
SELECT id, 'Take the dangerous path (risky!)', 'danger', NULL, NULL
FROM scenes WHERE code = 'forest';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Look for a healing spring', 'heal', NULL, NULL
FROM scenes WHERE code = 'forest';

-- ---------- Cave (needs torch to reach treasure) ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Descend with your torch', 'treasure', 'torch', 'gold'
FROM scenes WHERE code = 'cave';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Retreat to the campfire', 'intro', NULL, NULL
FROM scenes WHERE code = 'cave';

-- ---------- Danger (health damage) ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight the wolves (-30 HP)', 'forest', NULL, 'health:-30'
FROM scenes WHERE code = 'danger';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Run away carefully (-10 HP)', 'forest', NULL, 'health:-10'
FROM scenes WHERE code = 'danger';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Try to befriend them (risky! -50 HP)', 'cave', NULL, 'health:-50'
FROM scenes WHERE code = 'danger';

-- ---------- Healing Spring (health restoration) ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Drink from the spring (+25 HP)', 'forest', NULL, 'health:25'
FROM scenes WHERE code = 'heal';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Bathe in the spring (full heal!)', 'forest', NULL, 'health:100'
FROM scenes WHERE code = 'heal';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Leave without drinking', 'forest', NULL, NULL
FROM scenes WHERE code = 'heal';
