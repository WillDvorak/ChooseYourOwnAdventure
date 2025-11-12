-- V2__seed.sql
-- Expanded story content for "Echoes of the Shattered Realms" adventure game

USE textquest;

-- =========================
-- REALM 1: MISTWOOD FOREST
-- =========================
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('intro', 'Awakening in Mistwood', 'You awaken in the Mistwood Forest, disoriented and alone. Ancient trees tower above you, their branches whispering secrets in a language you almost understand. A narrow path leads deeper into the forest, and you notice a faint glow flickering to the east. The air hums with ancient magic, and you sense that your choices here will shape everything to come.', 0),
('forest_path', 'The Whispering Path', 'The forest path winds deeper into the Mistwood. Strange voices call your name from the shadows, beckoning you forward. The trees seem to watch your every move. You notice fresh footprints in the damp earth and a torn scrap of what might be an ancient map.', 0),
('watcher_tree', 'The Watcher Tree', 'You climb the ancient Watcher Tree, its bark smooth and warm beneath your hands. From the top, you gain a breathtaking view of the shattered realms. To the east, you see the fiery glow of Ember City. To the south, crystalline spires mark the Crystal Archives. Your foresight reveals a hidden path that could bypass dangers ahead.', 0),
('ruined_shrine', 'The Ruined Shrine', 'Before you stands a crumbling shrine, overgrown with moss and vines. Ancient symbols glow faintly on weathered stone. At the center of the shrine, you find a Forest Sigil - a carved wooden amulet that pulses with nature magic. This relic could calm ancient spirits or unlock paths in other realms.', 0),
('forest_campfire', 'The Safety of Fire', 'You light a fire, creating a circle of safety and warmth in the dark forest. The flames drive away shadowy predators, but the light also draws attention. You hear voices in the distance - bandits who have noticed your presence. Your safety here may cost you dearly in the next realm.', 0),
('forest_whispers', 'Following the Whispers', 'You follow the spectral voices deeper into the forest. A translucent figure materializes before you - a spectral guide who claims to know the truth about the Shattering. "I can lead you to answers," it says, "but every truth has a price. Will you trust me?"', 0);

-- =========================
-- REALM 2: EMBER CITY
-- =========================
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('ember_city_gates', 'Gates of Ember City', 'You arrive at Ember City, a molten metropolis built around a volcano''s heart. The air shimmers with heat, and the sounds of forges echo through the streets. Guards watch from the gates, and you notice tensions running high. A rebellion is brewing, and the Tyrant hoards fire crystals that could power entire realms.', 0),
('rebellion_hideout', 'The Rebellion Hideout', 'You find yourself in a secret hideout beneath the city. Rebel leaders speak in hushed tones about overthrowing the Tyrant. "Join us," they say, "and together we can bring justice to Ember City. But know this - our war will burn brighter, and some will not survive the flames."', 0),
('forgemaster_shop', 'The Forgemaster''s Workshop', 'The Forgemaster''s workshop glows with the heat of a thousand fires. Weapons line the walls, and mystical artifacts shimmer on display. "I trade in relics and favors," the Forgemaster says. "What do you have to offer? I can forge you a Firebrand - a weapon of pure flame that can cut through any obstacle."', 0),
('citadel_sneak', 'The Tyrant''s Citadel', 'You sneak into the Tyrant''s citadel, moving through shadowed corridors. In a hidden chamber, you discover the truth: the Tyrant guards a key shard of reality itself. This shard could unlock secrets in the Crystal Archives. But the Tyrant is approaching, and you must decide quickly whether to take it or flee.', 0),
('tyrant_challenge', 'Challenge to the Tyrant', 'You stand before the Tyrant of Ember City, a figure wreathed in flames and darkness. "You dare challenge me?" they roar. If you hold the Forest Sigil, an ancient spirit awakens to aid you. Without it, you face imprisonment and must escape through dangerous passages.', 0),
('citadel_escape', 'Escape from the Citadel', 'You flee the citadel, guards hot on your trail. Through secret passages and hidden routes, you make your way to freedom. The city burns behind you, and you carry with you either a Firebrand weapon or knowledge of the reality shard - or perhaps both.', 0);

-- =========================
-- REALM 3: CRYSTAL ARCHIVES
-- =========================
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('crystal_archives_entrance', 'Entrance to the Crystal Archives', 'The Crystal Archives rise before you, a vast library of living crystals that store the knowledge of ages. Sentient scholars float between the stacks, their forms shifting between crystal and flesh. The air hums with stored memories, and you sense that answers about the Shattering lie within these walls.', 0),
('archivist_chamber', 'The Archivist''s Chamber', 'The Archivist, an ancient crystal being, waits for you. "You seek truth about the Shattering," they say. "I can reveal it to you, but the truth is heavy. Know this: to restore the realms, one must be sacrificed. That is the price of unity." The knowledge settles into your mind like a stone.', 0),
('forbidden_crystal', 'The Forbidden Crystal', 'You approach a corrupted crystal pulsing with dark energy. It offers forbidden knowledge - the complete history of the Shattering, including secrets that were meant to stay hidden. "Take my wisdom," it whispers, "but know that some knowledge comes with a curse. Your mind may never be the same."', 0),
('damaged_tome', 'The Damaged Tome', 'You discover a damaged tome that seems to contain knowledge about the Drowned Sanctum. As you repair its crystalline pages, secrets unfold before you. You learn of hidden underwater passages that could bypass the dangers of the sunken temple. The knowledge feels like a gift and a burden.', 0),
('corrupted_crystal_destroy', 'Destroying the Corruption', 'You shatter the corrupted crystal, purging false knowledge from the Archives. Light floods the chamber as the corruption dissolves. However, the act has a cost - you feel memories slipping away, particularly those of the Mistwood. You''ve forgotten something important, something that once connected you to the forest realm.', 0);

-- =========================
-- REALM 4: DROWNED SANCTUM
-- =========================
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('drowned_sanctum_entrance', 'Entrance to the Drowned Sanctum', 'You stand at the edge of the Drowned Sanctum, a sunken temple city beneath the ocean. Ancient architecture rises from the depths, and you hear echoes of lost gods whispering in the currents. The water seems alive, and you sense that powerful forces await below the surface.', 0),
('siren_song', 'Following the Siren Song', 'A haunting melody draws you deeper into the Sanctum. The Siren''s voice promises power over water itself, the ability to command the oceans. "Join with me," she sings, "and the depths will be yours. But beware - those who embrace the sea may lose their humanity to the waves."', 0),
('sunken_catacombs', 'The Sunken Catacombs', 'You explore the ancient catacombs beneath the temple. In a hidden chamber, you discover a Portal Shard - a fragment of reality that pulses with astral energy. This shard is necessary to reach the Astral Gate, the nexus where all choices converge. The weight of destiny settles on your shoulders.', 0),
('sea_priestess', 'Negotiating with the Sea Priestess', 'The Sea Priestess awaits you in her underwater court. "I know of your journey," she says. "If you helped the Rebellion in Ember City, I will grant you safe passage. But if you stood with the Tyrant, the depths will show no mercy." Her eyes hold the wisdom of the ocean depths.', 0),
('relic_offering', 'Offering a Relic to the Depths', 'You stand before an ancient altar in the deepest part of the Sanctum. The water around you seems to hold its breath, waiting for your offering. The relic you choose to offer will determine your fate here - some bring calm, others cause explosions, and some may lead to your doom.', 0),
('sanctum_safe_passage', 'Safe Passage Through the Sanctum', 'The waters calm around you as your offering is accepted. Safe passages open through the temple, and you feel the blessing of the ancient gods. You move forward with confidence, knowing that the depths favor your journey.', 0),
('sanctum_vault', 'The Secret Vault', 'A steam explosion reveals a hidden vault deep within the Sanctum. Ancient treasures glow in the darkness, and you sense that powerful artifacts await within. This discovery could change everything, but entering the vault may have consequences you cannot foresee.', 0);

-- =========================
-- REALM 5: ASTRAL GATE (Final Realm)
-- =========================
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('astral_gate', 'The Astral Gate', 'You stand at the Astral Gate, the nexus of all realms. Reality itself seems to bend around you, and you can see all five shattered realms floating in the void. This is where fate converges, where your choices will determine the future of everything. The Gate awaits your decision.', 0),
('restore_realms', 'Restoring the Realms', 'You channel the power of your collected relics, feeling the energy of the Forest Sigil, Firebrand, Portal Shard, and more flowing through you. The shattered realms begin to merge, their fragments weaving together into a unified whole. Harmony returns to the world, and you feel the gratitude of all living things. The realms are restored, and balance is achieved. This is the True Ending.', 1),
('rule_chaos', 'Ruling the Chaos', 'You seize all the energy of the shattered realms, drawing it into yourself. Power floods through your veins as you ascend beyond mortal form. You become a god, but at a terrible cost - the realms themselves die, their life force consumed by your transformation. You rule over a wasteland of your own creation. This is the Power Ending.', 1),
('rebuild_one_realm', 'Rebuilding One Realm', 'You choose to save one realm, pouring all your power into restoring it to its former glory. The chosen realm flourishes, becoming a paradise of beauty and harmony. But the other realms fade away, their essence used to fuel the one. It is a bittersweet victory - you saved something beautiful, but at the cost of all else. This is the Bittersweet Ending.', 1),
('walk_away', 'Walking Away', 'You turn from the Astral Gate, refusing the power offered to you. "Let the realms decide their own fate," you say. But as you walk away, reality shifts around you. You find yourself back in the Mistwood Forest, the cycle beginning anew. Your journey starts over, but perhaps this time, with the knowledge you''ve gained, you can find a different path. This is the Loop Ending.', 1);

-- =========================
-- ADDITIONAL SCENES (Alternate Paths and Consequences)
-- =========================
INSERT INTO scenes (code, title, body, is_terminal) VALUES
('forest_bandits', 'Ambushed by Bandits', 'The bandits you alerted in the Mistwood catch up to you. They surround you, weapons drawn. "You should not have lit that fire," their leader says. You must fight or negotiate your way out, and the outcome will affect your journey to Ember City.', 0),
('tyrant_imprisoned', 'Imprisoned by the Tyrant', 'Without the Forest Sigil, you are overwhelmed by the Tyrant''s power. You find yourself in a dark cell, your weapons taken. Through a small window, you can see the city burning. You must find a way to escape, but your options are limited.', 0),
('cursed_mind', 'The Cursed Knowledge', 'The forbidden knowledge from the Crystal Archives has taken root in your mind. Reality seems to shift around you, and you''re not always sure what''s real. Choices become harder as the curse clouds your judgment. You must find a way to break free or learn to live with the curse.', 0),
('sanctum_drowning', 'Swallowed by the Depths', 'Without a proper offering, the sea rejects you. The waters rise, pulling you down into the darkness. Ancient spirits of the drowned claim you, and your journey ends in the cold embrace of the depths. You have failed to reach the Astral Gate.', 1);

-- =========================
-- CHOICES: MISTWOOD FOREST
-- =========================

-- From intro
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Follow the forest path', 'forest_path', NULL, NULL
FROM scenes WHERE code = 'intro';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Climb the Watcher Tree', 'watcher_tree', NULL, 'foresight'
FROM scenes WHERE code = 'intro';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Investigate the ruined shrine', 'ruined_shrine', NULL, NULL
FROM scenes WHERE code = 'intro';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Light a fire for safety', 'forest_campfire', NULL, 'bandits_alerted'
FROM scenes WHERE code = 'intro';

-- From forest_path
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Follow the whispers', 'forest_whispers', NULL, NULL
FROM scenes WHERE code = 'forest_path';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Investigate the ruined shrine', 'ruined_shrine', NULL, NULL
FROM scenes WHERE code = 'forest_path';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head toward Ember City', 'ember_city_gates', NULL, NULL
FROM scenes WHERE code = 'forest_path';

-- From watcher_tree
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Use foresight to find safe path to Ember City', 'ember_city_gates', NULL, NULL
FROM scenes WHERE code = 'watcher_tree';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Investigate the ruined shrine', 'ruined_shrine', NULL, NULL
FROM scenes WHERE code = 'watcher_tree';

-- From ruined_shrine
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Take the Forest Sigil', 'forest_path', NULL, 'forest_sigil'
FROM scenes WHERE code = 'ruined_shrine';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Leave the sigil and continue', 'forest_path', NULL, NULL
FROM scenes WHERE code = 'ruined_shrine';

-- From forest_campfire
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Continue to Ember City', 'forest_bandits', 'bandits_alerted', NULL
FROM scenes WHERE code = 'forest_campfire';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Wait until morning', 'forest_path', NULL, NULL
FROM scenes WHERE code = 'forest_campfire';

-- From forest_whispers
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Trust the spectral guide', 'ember_city_gates', NULL, 'spectral_guide'
FROM scenes WHERE code = 'forest_whispers';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Refuse and find your own way', 'forest_path', NULL, NULL
FROM scenes WHERE code = 'forest_whispers';

-- From forest_bandits
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight the bandits', 'ember_city_gates', NULL, NULL
FROM scenes WHERE code = 'forest_bandits';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Negotiate with the bandits', 'ember_city_gates', NULL, NULL
FROM scenes WHERE code = 'forest_bandits';

-- =========================
-- CHOICES: EMBER CITY
-- =========================

-- From ember_city_gates
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Join the Rebellion', 'rebellion_hideout', NULL, NULL
FROM scenes WHERE code = 'ember_city_gates';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Visit the Forgemaster', 'forgemaster_shop', NULL, NULL
FROM scenes WHERE code = 'ember_city_gates';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Sneak into the Citadel', 'citadel_sneak', NULL, NULL
FROM scenes WHERE code = 'ember_city_gates';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Challenge the Tyrant directly', 'tyrant_challenge', NULL, NULL
FROM scenes WHERE code = 'ember_city_gates';

-- From rebellion_hideout
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Commit to the Rebellion', 'crystal_archives_entrance', NULL, 'alliance_rebellion'
FROM scenes WHERE code = 'rebellion_hideout';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Leave and visit the Forgemaster', 'forgemaster_shop', NULL, NULL
FROM scenes WHERE code = 'rebellion_hideout';

-- From forgemaster_shop
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Trade Forest Sigil for Firebrand', 'crystal_archives_entrance', 'forest_sigil', 'firebrand'
FROM scenes WHERE code = 'forgemaster_shop';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Trade gold for Firebrand', 'crystal_archives_entrance', NULL, 'firebrand'
FROM scenes WHERE code = 'forgemaster_shop';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Leave without trading', 'crystal_archives_entrance', NULL, NULL
FROM scenes WHERE code = 'forgemaster_shop';

-- From citadel_sneak
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Take the reality shard', 'citadel_escape', NULL, 'reality_shard'
FROM scenes WHERE code = 'citadel_sneak';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Flee without taking anything', 'citadel_escape', NULL, NULL
FROM scenes WHERE code = 'citadel_sneak';

-- From tyrant_challenge
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight with Forest Sigil', 'crystal_archives_entrance', 'forest_sigil', NULL
FROM scenes WHERE code = 'tyrant_challenge';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Fight without sigil', 'tyrant_imprisoned', NULL, NULL
FROM scenes WHERE code = 'tyrant_challenge';

-- From tyrant_imprisoned
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Escape through the window', 'citadel_escape', NULL, NULL
FROM scenes WHERE code = 'tyrant_imprisoned';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Wait for an opportunity', 'citadel_escape', NULL, NULL
FROM scenes WHERE code = 'tyrant_imprisoned';

-- From citadel_escape
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Head to Crystal Archives', 'crystal_archives_entrance', NULL, NULL
FROM scenes WHERE code = 'citadel_escape';

-- =========================
-- CHOICES: CRYSTAL ARCHIVES
-- =========================

-- From crystal_archives_entrance
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Consult the Archivist', 'archivist_chamber', NULL, 'knowledge_shattering'
FROM scenes WHERE code = 'crystal_archives_entrance';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Steal from the Forbidden Crystal', 'forbidden_crystal', NULL, NULL
FROM scenes WHERE code = 'crystal_archives_entrance';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Repair the Damaged Tome', 'damaged_tome', NULL, NULL
FROM scenes WHERE code = 'crystal_archives_entrance';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Destroy the Corrupted Crystal', 'corrupted_crystal_destroy', NULL, NULL
FROM scenes WHERE code = 'crystal_archives_entrance';

-- From archivist_chamber
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Accept the knowledge', 'drowned_sanctum_entrance', NULL, NULL
FROM scenes WHERE code = 'archivist_chamber';

-- From forbidden_crystal
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Take the forbidden knowledge', 'cursed_mind', NULL, 'knowledge_cursed'
FROM scenes WHERE code = 'forbidden_crystal';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Reject the knowledge', 'drowned_sanctum_entrance', NULL, NULL
FROM scenes WHERE code = 'forbidden_crystal';

-- From damaged_tome
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Use the hidden passage knowledge', 'drowned_sanctum_entrance', NULL, 'hidden_passage'
FROM scenes WHERE code = 'damaged_tome';

-- From corrupted_crystal_destroy
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Continue to Drowned Sanctum', 'drowned_sanctum_entrance', NULL, NULL
FROM scenes WHERE code = 'corrupted_crystal_destroy';

-- From cursed_mind
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Continue despite the curse', 'drowned_sanctum_entrance', NULL, NULL
FROM scenes WHERE code = 'cursed_mind';

-- =========================
-- CHOICES: DROWNED SANCTUM
-- =========================

-- From drowned_sanctum_entrance
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Follow the Siren Song', 'siren_song', NULL, NULL
FROM scenes WHERE code = 'drowned_sanctum_entrance';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Explore the Sunken Catacombs', 'sunken_catacombs', NULL, NULL
FROM scenes WHERE code = 'drowned_sanctum_entrance';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Negotiate with the Sea Priestess', 'sea_priestess', NULL, NULL
FROM scenes WHERE code = 'drowned_sanctum_entrance';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Offer a Relic to the Depths', 'relic_offering', NULL, NULL
FROM scenes WHERE code = 'drowned_sanctum_entrance';

-- From siren_song
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Accept the Siren''s power', 'astral_gate', NULL, 'water_power'
FROM scenes WHERE code = 'siren_song';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Reject the Siren''s power', 'drowned_sanctum_entrance', NULL, NULL
FROM scenes WHERE code = 'siren_song';

-- From sunken_catacombs
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Take the Portal Shard', 'astral_gate', NULL, 'portal_shard'
FROM scenes WHERE code = 'sunken_catacombs';

-- From sea_priestess
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Form an alliance', 'astral_gate', 'alliance_rebellion', 'alliance_sea_priestess'
FROM scenes WHERE code = 'sea_priestess';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Face the consequences', 'astral_gate', NULL, NULL
FROM scenes WHERE code = 'sea_priestess';

-- From relic_offering
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Offer Forest Sigil', 'sanctum_safe_passage', 'forest_sigil', NULL
FROM scenes WHERE code = 'relic_offering';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Offer Firebrand', 'sanctum_vault', 'firebrand', NULL
FROM scenes WHERE code = 'relic_offering';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Offer nothing', 'sanctum_drowning', NULL, NULL
FROM scenes WHERE code = 'relic_offering';

-- From sanctum_safe_passage
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Continue to Astral Gate', 'astral_gate', NULL, NULL
FROM scenes WHERE code = 'sanctum_safe_passage';

-- From sanctum_vault
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Enter the vault', 'astral_gate', NULL, 'ancient_artifacts'
FROM scenes WHERE code = 'sanctum_vault';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Leave and continue to Astral Gate', 'astral_gate', NULL, NULL
FROM scenes WHERE code = 'sanctum_vault';

-- =========================
-- CHOICES: ASTRAL GATE (Final Decisions)
-- =========================

-- From astral_gate
-- Restore All Realms requires 3+ relics (has_3_relics flag is set automatically by GameService)
INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Restore All Realms', 'restore_realms', 'has_3_relics', NULL
FROM scenes WHERE code = 'astral_gate';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Rule the Chaos', 'rule_chaos', NULL, NULL
FROM scenes WHERE code = 'astral_gate';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Rebuild One Realm', 'rebuild_one_realm', NULL, NULL
FROM scenes WHERE code = 'astral_gate';

INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
SELECT id, 'Walk Away', 'walk_away', NULL, NULL
FROM scenes WHERE code = 'astral_gate';

-- Note: The restore_realms ending requires 3+ relics
-- The GameService automatically sets the "has_3_relics" flag when the player reaches the Astral Gate
-- Relics include: forest_sigil, firebrand, portal_shard, reality_shard, ancient_artifacts, water_power
