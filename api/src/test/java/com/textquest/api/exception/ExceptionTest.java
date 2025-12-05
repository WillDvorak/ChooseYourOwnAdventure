package com.textquest.api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testGameEndedException_WithMessage() {
        // Arrange
        String message = "Game has already ended";

        // Act
        GameEndedException exception = new GameEndedException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testGameEndedException_IsRuntimeException() {
        // Arrange & Act
        GameEndedException exception = new GameEndedException("Test message");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testGameEndedException_WithEmptyMessage() {
        // Act
        GameEndedException exception = new GameEndedException("");

        // Assert
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    void testGameSessionNotFoundException_WithMessage() {
        // Arrange
        String message = "Game session not found: 123";

        // Act
        GameSessionNotFoundException exception = new GameSessionNotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testGameSessionNotFoundException_IsRuntimeException() {
        // Arrange & Act
        GameSessionNotFoundException exception = new GameSessionNotFoundException("Test message");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testGameSessionNotFoundException_WithNullMessage() {
        // Act
        GameSessionNotFoundException exception = new GameSessionNotFoundException(null);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testInvalidChoiceException_WithMessage() {
        // Arrange
        String message = "Choice requirements not met";

        // Act
        InvalidChoiceException exception = new InvalidChoiceException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testInvalidChoiceException_IsRuntimeException() {
        // Arrange & Act
        InvalidChoiceException exception = new InvalidChoiceException("Test message");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testInvalidChoiceException_WithDetailedMessage() {
        // Arrange
        String message = "Choice requirements not met. Required flag: key";

        // Act
        InvalidChoiceException exception = new InvalidChoiceException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testSceneNotFoundException_WithMessage() {
        // Arrange
        String message = "Scene not found: cave";

        // Act
        SceneNotFoundException exception = new SceneNotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testSceneNotFoundException_IsRuntimeException() {
        // Arrange & Act
        SceneNotFoundException exception = new SceneNotFoundException("Test message");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testSceneNotFoundException_WithStartingSceneMessage() {
        // Arrange
        String message = "Starting scene not found: intro";

        // Act
        SceneNotFoundException exception = new SceneNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testAllExceptions_CanBeThrown() {
        // Test that all exceptions can be thrown and caught
        assertThrows(GameEndedException.class, () -> {
            throw new GameEndedException("Test");
        });

        assertThrows(GameSessionNotFoundException.class, () -> {
            throw new GameSessionNotFoundException("Test");
        });

        assertThrows(InvalidChoiceException.class, () -> {
            throw new InvalidChoiceException("Test");
        });

        assertThrows(SceneNotFoundException.class, () -> {
            throw new SceneNotFoundException("Test");
        });
    }

    @Test
    void testExceptionMessages_ArePreserved() {
        // Arrange
        String gameEndedMsg = "The game has ended";
        String sessionNotFoundMsg = "Session 456 not found";
        String invalidChoiceMsg = "Invalid choice ID: 789";
        String sceneNotFoundMsg = "Scene 'forest' does not exist";

        // Act
        GameEndedException gameEnded = new GameEndedException(gameEndedMsg);
        GameSessionNotFoundException sessionNotFound = new GameSessionNotFoundException(sessionNotFoundMsg);
        InvalidChoiceException invalidChoice = new InvalidChoiceException(invalidChoiceMsg);
        SceneNotFoundException sceneNotFound = new SceneNotFoundException(sceneNotFoundMsg);

        // Assert
        assertEquals(gameEndedMsg, gameEnded.getMessage());
        assertEquals(sessionNotFoundMsg, sessionNotFound.getMessage());
        assertEquals(invalidChoiceMsg, invalidChoice.getMessage());
        assertEquals(sceneNotFoundMsg, sceneNotFound.getMessage());
    }

    @Test
    void testExceptionInheritance() {
        // Test that all custom exceptions properly extend RuntimeException
        GameEndedException gameEnded = new GameEndedException("test");
        GameSessionNotFoundException sessionNotFound = new GameSessionNotFoundException("test");
        InvalidChoiceException invalidChoice = new InvalidChoiceException("test");
        SceneNotFoundException sceneNotFound = new SceneNotFoundException("test");

        assertTrue(gameEnded instanceof RuntimeException);
        assertTrue(sessionNotFound instanceof RuntimeException);
        assertTrue(invalidChoice instanceof RuntimeException);
        assertTrue(sceneNotFound instanceof RuntimeException);
    }
}

