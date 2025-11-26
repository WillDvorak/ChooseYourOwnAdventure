-- V2__seed.sql
-- Expanded story content for the adventure game (15 scenes)

USE textquest;

-- ---------- Scenes (15 total) ----------
INSERT INTO scenes (code, title, body, is_terminal) VALUES
-- Starting area
('intro',   'The Long One',     'You wake at a campfire. A narrow path leads into a pine forest. A faint glow flickers to the east. To the west, you see ancient ruins silhouetted against the moon.', 0),
('forest',  'Under Tall Pines', 'The forest hums with life. You notice footprints leading deeper, a torn map scrap on the ground, and hear distant howling.', 0),
('cave',    'Cave Mouth',       'A damp cave descends into darkness. It smells of iron and something ancient. Your torch sputters, revealing strange markings on the walls.', 0),
('ruins',   'Ancient Ruins',    'Crumbling stone pillars rise from the earth. Ancient symbols glow faintly. A pedestal in the center holds something mysterious.', 0),
('bridge',  'The Old Bridge',   'A rickety wooden bridge spans a chasm. Below, a river roars. The bridge looks unstable, but it''s the only way across.', 0),
('village', 'The Quiet Village', 'A small village lies ahead. Smoke rises from chimneys, but you see no one. Something feels wrong here.', 0),
('dungeon', 'The Dark Dungeon', 'You descend into a dungeon. Torches line the walls, casting eerie shadows. You hear something moving in the darkness ahead.', 0),
('temple',  'The Sacred Temple', 'A grand temple stands before you. Its doors are sealed with ancient magic. Strange symbols pulse with power.', 0),
-- Treasure and special locations
('treasure','Hidden Cache',     'You pry open a chest: old coins, a small brass key, and a glowing amulet. The amulet pulses with ancient power.', 0),
-- Danger scenes
('danger',  'Dangerous Path',   'You stumble upon a pack of wolves! They bare their teeth menacingly and begin to circle you.', 0),
('danger2', 'The Ambush',       'Bandits leap from the shadows! They brandish weapons and demand your valuables.', 0),
-- Healing
('heal',    'Healing Spring',   'You discover a clear spring. The water glows faintly and appears magical. You feel its restorative power.', 0),
-- Endings
('ending1', 'The Hero''s Return', 'You return to the village with the ancient artifacts. The people celebrate your victory. You have saved the realm!', 1),
('ending2', 'The Power Within',   'You absorb the ancient power. You feel yourself transforming, becoming something more than human. The world will never be the same.', 1),
('death',   'Game Over',        'Your health has dropped to zero. Your adventure ends here. The darkness claims you.', 1);

-- ---------- Items -----------
INSERT INTO items (label, title, description, image_url) VALUES
-- Torch
('torch', 'Torch',
 'The torch sputters to life in your hand, a hungry orange flame gnawing at oil-soaked cloth. Heat licks your knuckles and the dry wood handle is rough, splintered from too many desperate grips. Every time you raise it, shadows recoil along the stone, revealing just enough to make you wish you saw less. The smoke curls upward in thin, bitter threads that sting your eyes and cling to your clothes.',
 'https://your-cdn.example.com/images/torch.png'),

-- Key
('key', 'Ancient Key',
 'The iron key is heavier than it looks, its cold weight dragging at your palm. Centuries of rust and grime have darkened the metal to a dull, almost black sheen, but the teeth are still sharp, cut with unsettling precision. When you flex your fingers around it, you feel a faint vibration, as if it remembers every lock it has ever turned. It smells of old rain, locked rooms, and secrets that should have stayed that way.',
 'https://your-cdn.example.com/images/key.png'),

-- Map
('map', 'Tattered Map',
 'Unfolding the map sends a whisper of brittle parchment through the air, the curled edges fighting to roll back into themselves. Faded ink traces forests of cramped, crooked trees and jagged cliff lines that seem to shift if you stare too long. Caves are marked by hollow black mouths, and a trail of tiny, desperate dashes winds toward an ominous X. It feels fragile beneath your fingers, yet the stains and burn marks hint at how many hands failed to follow it home.',
 'https://your-cdn.example.com/images/map.png'),

-- Amulet
('amulet', 'Luminous Amulet',
 'The amulet hangs heavy from its gold chain, the massive stone at its heart pulsing with a soft violet-to-blue glow. Light seeps from beneath the polished surface like trapped starlight, brushing your skin with a cold, tingling sensation. The metal frame is impossibly smooth, every curve and filigree too perfect for mortal craftsmanship. When you hold it up, the world around you dims, as though the gem is quietly demanding all light for itself.',
 'https://your-cdn.example.com/images/amulet.png'),

-- Knowledge
('knowledge', 'Spark of Knowledge',
 'A sudden clarity blooms behind your eyes, like a lantern being lit inside your skull. You feel ideas slotting into place, cold and sharp as crystal, while pale rays of thought radiate outward in every direction. The sensation is both exhilarating and terrifying—your mind stretched wide open, every memory painfully bright. For a moment, you’re certain you can see the pattern behind everything… and equally certain that the pattern is staring back.',
 'https://your-cdn.example.com/images/knowledge.png'),

-- Potion
('potion', 'Healing Potion',
 'The healing potion swirls in its round glass bottle, the crimson liquid glowing softly as if it contains a captive ember. Tiny bubbles crawl lazily upward, bursting in soundless sparks against the inside of the glass. The cork and rough rope bite into your fingers, sticky with the faintest residue of herbs and iron. When you uncork it, a warm, metallic sweetness rises—a promise of mended flesh and borrowed time.',
 'https://your-cdn.example.com/images/potion.png'),

-- Sword
('sword', 'Steel Sword',
 'The sword’s weight settles into your hand with the familiarity of a bad habit. Its steel blade is straight and honest, dulled only by a dusting of time and scars from battles that were anything but. The leather-wrapped grip is worn where countless fingers have clenched in fear and fury, slick in some places, rough in others. When you raise it, the air around the edge seems to grow still, as if the world is waiting to see who will bleed next.',
 'https://your-cdn.example.com/images/sword.png'),

-- Gold
('gold', 'Pouch of Gold',
 'The small pouch of coins is soft and yielding, the leather worn thin where it has been clutched and hidden. Inside, the gold pieces chime against each other with a muted, greedy music whenever you move. They are warm to the touch, as though they’ve absorbed every grasping hand and feverish heartbeat that ever coveted them. Even through the bag, you can feel the weight of choices—doors that will open, oaths that will be broken, all paid for in quiet, shining circles.',
 'https://your-cdn.example.com/images/gold.png');


-- ---------- Choices from intro ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head into the forest', 'forest', NULL, NULL
FROM scenes WHERE code = 'intro';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Walk toward the glow', 'cave', NULL, NULL
FROM scenes WHERE code = 'intro';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Explore the ancient ruins', 'ruins', NULL, NULL
FROM scenes WHERE code = 'intro';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Cross the old bridge', 'bridge', NULL, NULL
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

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Follow the path to the village', 'village', NULL, NULL
FROM scenes WHERE code = 'forest';

-- ---------- Cave ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Descend deeper (needs torch)', 'treasure', 'torch', 'key'
FROM scenes WHERE code = 'cave';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Retreat to the campfire', 'intro', NULL, NULL
FROM scenes WHERE code = 'cave';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Explore a side passage', 'dungeon', NULL, NULL
FROM scenes WHERE code = 'cave';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Study the wall markings', 'cave', NULL, 'map'
FROM scenes WHERE code = 'cave';

-- ---------- Ruins ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Examine the pedestal', 'ruins', NULL, 'amulet'
FROM scenes WHERE code = 'ruins';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Read the ancient symbols', 'ruins', NULL, 'knowledge'
FROM scenes WHERE code = 'ruins';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head to the temple', 'temple', NULL, NULL
FROM scenes WHERE code = 'ruins';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Return to the campfire', 'intro', NULL, NULL
FROM scenes WHERE code = 'ruins';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head to the bridge', 'bridge', NULL, NULL
FROM scenes WHERE code = 'ruins';

-- ---------- Bridge ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Cross carefully', 'village', NULL, NULL
FROM scenes WHERE code = 'bridge';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Run across quickly (risky!)', 'village', NULL, 'health:-15'
FROM scenes WHERE code = 'bridge';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Turn back', 'intro', NULL, NULL
FROM scenes WHERE code = 'bridge';

-- ---------- Village ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Investigate the empty houses', 'village', NULL, NULL
FROM scenes WHERE code = 'village';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Search for survivors', 'village', NULL, 'potion'
FROM scenes WHERE code = 'village';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Continue to the temple', 'temple', NULL, NULL
FROM scenes WHERE code = 'village';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Return to the forest', 'forest', NULL, NULL
FROM scenes WHERE code = 'village';

-- ---------- Dungeon ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight the creature (needs sword)', 'treasure', 'sword', 'key'
FROM scenes WHERE code = 'dungeon';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Sneak past carefully', 'treasure', NULL, NULL
FROM scenes WHERE code = 'dungeon';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Retreat (risky!)', 'danger2', NULL, NULL
FROM scenes WHERE code = 'dungeon';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Search for a weapon', 'dungeon', NULL, 'sword'
FROM scenes WHERE code = 'dungeon';

-- ---------- Temple ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Use the amulet to open the door', 'temple', 'amulet', NULL
FROM scenes WHERE code = 'temple';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Use the key to unlock the door', 'temple', 'key', NULL
FROM scenes WHERE code = 'temple';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Force the door open (needs sword)', 'temple', 'sword', 'health:-25'
FROM scenes WHERE code = 'temple';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Study the symbols (needs knowledge)', 'temple', 'knowledge', NULL
FROM scenes WHERE code = 'temple';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Enter the temple (if door is open)', 'ending1', 'amulet', NULL
FROM scenes WHERE code = 'temple';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Enter the temple (if door is open)', 'ending1', 'key', NULL
FROM scenes WHERE code = 'temple';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Absorb the temple''s power', 'ending2', 'knowledge', NULL
FROM scenes WHERE code = 'temple';

-- ---------- Treasure ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Take the key and amulet', 'forest', NULL, 'key'
FROM scenes WHERE code = 'treasure';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Take only the gold', 'forest', NULL, 'gold'
FROM scenes WHERE code = 'treasure';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Explore further', 'dungeon', NULL, NULL
FROM scenes WHERE code = 'treasure';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Search for a weapon', 'treasure', NULL, 'sword'
FROM scenes WHERE code = 'treasure';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head to the temple', 'temple', NULL, NULL
FROM scenes WHERE code = 'treasure';

-- ---------- Danger (wolves) ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight the wolves (-30 HP)', 'forest', NULL, 'health:-30'
FROM scenes WHERE code = 'danger';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Run away carefully (-10 HP)', 'forest', NULL, 'health:-10'
FROM scenes WHERE code = 'danger';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Use your sword to fight (if you have it)', 'forest', 'sword', 'health:-10'
FROM scenes WHERE code = 'danger';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Try to befriend them (risky! -50 HP)', 'cave', NULL, 'health:-50'
FROM scenes WHERE code = 'danger';

-- ---------- Danger2 (bandits) ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight the bandits', 'village', NULL, 'health:-20'
FROM scenes WHERE code = 'danger2';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight with your sword (if you have it)', 'village', 'sword', 'health:-5'
FROM scenes WHERE code = 'danger2';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Negotiate with them', 'village', 'gold', NULL
FROM scenes WHERE code = 'danger2';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Run away', 'forest', NULL, 'health:-15'
FROM scenes WHERE code = 'danger2';

-- ---------- Healing Spring ----------
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Drink from the spring (+25 HP)', 'forest', NULL, 'health:25'
FROM scenes WHERE code = 'heal';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Bathe in the spring (full heal!)', 'forest', NULL, 'health:100'
FROM scenes WHERE code = 'heal';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fill a container with the water', 'forest', NULL, 'potion'
FROM scenes WHERE code = 'heal';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Leave without drinking', 'forest', NULL, NULL
FROM scenes WHERE code = 'heal';
