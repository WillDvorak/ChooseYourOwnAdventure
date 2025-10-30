import mysql.connector
import json
import random

class TextQuestGame:
    """
    A database-driven Choose Your Own Adventure engine.
    Supports conditional choices, flag effects, and persistence.
    """

    def __init__(self, db_config, player_id=None):
        self.db = mysql.connector.connect(**db_config)
        self.cursor = self.db.cursor(dictionary=True)
        self.flags = set()
        self.visited_scenes = set()
        self.current_scene = None
        self.player_id = player_id

        if player_id:
            if not self.load_game(player_id):
                self.current_scene = self.get_scene_by_code("intro")
        else:
            self.current_scene = self.get_scene_by_code("intro")

    def get_scene_by_code(self, code):
        self.cursor.execute("SELECT * FROM scenes WHERE code = %s", (code,))
        return self.cursor.fetchone()

    def get_choices_for_scene(self, scene_id):
        self.cursor.execute("SELECT * FROM choices WHERE scene_id = %s", (scene_id,))
        return self.cursor.fetchall()

    def available_choices(self):
        """Return only valid choices given current flags."""
        all_choices = self.get_choices_for_scene(self.current_scene["id"])
        return [
            c for c in all_choices
            if c["requires_flag"] is None or c["requires_flag"] in self.flags
        ]

    def apply_choice(self, choice):
        """Apply effects of a choice and move to next scene."""
        # Flag effects
        if choice["sets_flag"]:
            flag = choice["sets_flag"]
            if flag.startswith("!"):
                # Convention: !flag means remove
                self.flags.discard(flag[1:])
            else:
                self.flags.add(flag)

        # Move to next scene
        self.visited_scenes.add(self.current_scene["code"])
        self.current_scene = self.get_scene_by_code(choice["target_scene_code"])

        # Optional: random dynamic flavor text
        if random.random() < 0.15:
            print("\nA cold wind whispers through the trees...")

    def display_scene(self):
        """Render the scene text and available choices."""
        s = self.current_scene
        print(f"\n== {s['title']} ==")
        print(s['body'])

        if s["is_terminal"]:
            print("\n✨ The End.")
            return []

        choices = self.available_choices()
        for i, c in enumerate(choices, 1):
            print(f"{i}. {c['label']}")
        return choices

    def choose(self, index):
        """Handle numeric player input."""
        valid = self.available_choices()
        if not valid:
            print("No choices available.")
            return False

        if index < 1 or index > len(valid):
            print("Invalid choice.")
            return False

        self.apply_choice(valid[index - 1])
        return True


    def save_game(self, player_id):
        """Save progress to a player_saves table."""
        self.cursor.execute("""
            CREATE TABLE IF NOT EXISTS player_saves (
                id INT PRIMARY KEY AUTO_INCREMENT,
                player_id VARCHAR(64) UNIQUE,
                scene_code VARCHAR(64),
                flags TEXT,
                visited TEXT
            )
        """)
        self.db.commit()

        flags_json = json.dumps(list(self.flags))
        visited_json = json.dumps(list(self.visited_scenes))

        self.cursor.execute("""
            INSERT INTO player_saves (player_id, scene_code, flags, visited)
            VALUES (%s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                scene_code = VALUES(scene_code),
                flags = VALUES(flags),
                visited = VALUES(visited)
        """, (player_id, self.current_scene["code"], flags_json, visited_json))
        self.db.commit()
        print(f"Game saved for player '{player_id}'.")

    def load_game(self, player_id):
        """Load a saved game if available."""
        self.cursor.execute("SELECT * FROM player_saves WHERE player_id = %s", (player_id,))
        row = self.cursor.fetchone()
        if not row:
            return False
        self.flags = set(json.loads(row["flags"]))
        self.visited_scenes = set(json.loads(row["visited"]))
        self.current_scene = self.get_scene_by_code(row["scene_code"])
        print(f"Loaded save for '{player_id}' at scene '{self.current_scene['code']}'.")
        return True


    def play(self):
        print("Welcome")
        self.display_scene()

        while not self.current_scene["is_terminal"]:
            try:
                choice = int(input("\nYour choice > "))
                if not self.choose(choice):
                    continue
                self.display_scene()
            except ValueError:
                print("Please enter a number.")
            except KeyboardInterrupt:
                print("\nAuto-saving before exit...")
                if self.player_id:
                    self.save_game(self.player_id)
                break
