package com.checkmatex.logic;

import java.util.Stack;                                 //LIFO

public class UndoRedoManager {
    
    private Stack<GameState> undoStack;                 //stores older moves
    private Stack<GameState> redoStack;                 //stores states that were undo

    public UndoRedoManager() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void saveState(GameState state) {
        undoStack.push(state.copy());
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public GameState undo(GameState currentState) {
        if (canUndo()) {
            redoStack.push(currentState.copy());
            return undoStack.pop();
        }
        return currentState;
    }

    public GameState redo(GameState currentState) {
        if (canRedo()) {
            undoStack.push(currentState.copy());
            return redoStack.pop();
        }
        return currentState;
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
