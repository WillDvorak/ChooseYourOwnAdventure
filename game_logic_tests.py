import pytest
from unittest.mock import MagicMock
from game_logic import TextQuestGame

@pytest.fixture
def game():
    """Create a mock game engine."""
    g = TextQuestGame(db_config={})
    g.db = MagicMock()
    g.cursor = MagicMock()
    return g


def test_get_scene_by_code_returns_dict(game):
    game.cursor.fetchone.return_value = {"code": "intro", "title": "Start", "body": "Test", "is_terminal": 0}
    scene = game.get_scene_by_code("intro")

    game.cursor.execute.assert_called_once_with("SELECT * FROM scenes WHERE code = %s", ("intro",))
    assert scene["code"] == "intro"
    assert scene["title"] == "Start"


def test_available_choices_filters_by_flag(game):
    game.current_scene = {"id": 1}
    game.flags = set()  # No torch
    game.get_choices_for_scene = MagicMock(return_value=[
        {"label": "Descend", "requires_flag": "torch"},
        {"label": "Retreat", "requires_flag": None},
    ])
    valid = game.available_choices()
    assert len(valid) == 1
    assert valid[0]["label"] == "Retreat"


def test_apply_choice_sets_and_removes_flags(game):
    game.current_scene = {"id": 1, "code": "cave"}
    game.get_scene_by_code = MagicMock(return_value={"code": "forest", "title": "Forest", "body": "", "is_terminal": 0})

    # Set a flag
    choice_set = {"sets_flag": "torch", "target_scene_code": "forest"}
    game.apply_choice(choice_set)
    assert "torch" in game.flags
    assert game.current_scene["code"] == "forest"

    # Remove a flag using !flag convention
    choice_remove = {"sets_flag": "!torch", "target_scene_code": "forest"}
    game.apply_choice(choice_remove)
    assert "torch" not in game.flags


def test_choose_invalid_index(monkeypatch, game):
    game.available_choices = MagicMock(return_value=[{"label": "Go"}])
    printed = []
    monkeypatch.setattr("builtins.print", lambda *a, **kw: printed.append(a[0]))

    result = game.choose(99)
    assert result is False
    assert any("Invalid choice" in s for s in printed)


def test_display_scene_terminal(monkeypatch, game):
    game.current_scene = {"id": 10, "title": "Treasure", "body": "You win!", "is_terminal": 1}
    printed = []
    monkeypatch.setattr("builtins.print", lambda *a, **kw: printed.append(a[0]))

    result = game.display_scene()
    assert "✨ The End." in printed[-1]
    assert result == []
