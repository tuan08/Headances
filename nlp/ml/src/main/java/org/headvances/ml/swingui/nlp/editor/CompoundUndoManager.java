package org.headvances.ml.swingui.nlp.editor ;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class CompoundUndoManager extends UndoManager 
  implements UndoableEditListener, DocumentListener {
	
	private UndoManager undoManager;
	private CompoundEdit compoundEdit;
	private JTextComponent textComponent;
	private UndoAction undoAction;
	private RedoAction redoAction;

	private int lastOffset;
	private int lastLength;

	public CompoundUndoManager(JTextComponent textComponent) {
		this.textComponent = textComponent;
		undoManager = this;
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		textComponent.getDocument().addUndoableEditListener( this );
	}

	public void undo() {
		textComponent.getDocument().addDocumentListener( this );
		super.undo();
		textComponent.getDocument().removeDocumentListener( this );
	}

	public void redo() {
		textComponent.getDocument().addDocumentListener( this );
		super.redo();
		textComponent.getDocument().removeDocumentListener( this );
	}

	/**
	 *  Whenever an UndoableEdit happens the edit will either be absorbed
	 *  by the current compound edit or a new compound edit will be started
	 **/
	public void undoableEditHappened(UndoableEditEvent e) {
		if (compoundEdit == null) {
			compoundEdit = startCompoundEdit( e.getEdit() );
			return;
		}

		int offsetChange = textComponent.getCaretPosition() - lastOffset;
		int lengthChange = textComponent.getDocument().getLength() - lastLength;

		//  Check for an attribute change

		AbstractDocument.DefaultDocumentEvent event =
			(AbstractDocument.DefaultDocumentEvent)e.getEdit();

		if(event.getType().equals(DocumentEvent.EventType.CHANGE)) {
			if (offsetChange == 0) {
				compoundEdit.addEdit(e.getEdit() );
				return;
			}
		}

		if(offsetChange == lengthChange &&  Math.abs(offsetChange) == 1) {
			compoundEdit.addEdit( e.getEdit() );
			lastOffset = textComponent.getCaretPosition();
			lastLength = textComponent.getDocument().getLength();
			return;
		}
		compoundEdit.end();
		compoundEdit = startCompoundEdit( e.getEdit() );
	}

	/**
	 *  Each CompoundEdit will store a group of related incremental edits
	 *  (ie. each character typed or backspaced is an incremental edit)
	 */
	private CompoundEdit startCompoundEdit(UndoableEdit anEdit) {
		//Track Caret and Document information of this compound edit
		lastOffset = textComponent.getCaretPosition();
		lastLength = textComponent.getDocument().getLength();
		//The compound edit is used to store incremental edits

		compoundEdit = new MyCompoundEdit();
		compoundEdit.addEdit( anEdit );

		//  The compound edit is added to the UndoManager. All incremental
		//  edits stored in the compound edit will be undone/redone at once
		addEdit( compoundEdit );

		undoAction.updateUndoState();
		redoAction.updateRedoState();

		return compoundEdit;
	}

	/**
	 *  The Action to Undo changes to the Document.
	 *  The state of the Action is managed by the CompoundUndoManager
	 */
	public Action getUndoAction() { return undoAction; }

	/**
	 *  The Action to Redo changes to the Document.
	 *  The state of the Action is managed by the CompoundUndoManager
	 */
	public Action getRedoAction() { return redoAction; }
	
	/**
	 *  Updates to the Document as a result of Undo/Redo will cause the
	 *  Caret to be repositioned
	 */
	public void insertUpdate(final DocumentEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int offset = e.getOffset() + e.getLength();
				offset = Math.min(offset, textComponent.getDocument().getLength());
				textComponent.setCaretPosition( offset );
			}
		});
	}

	public void removeUpdate(DocumentEvent e) {
		textComponent.setCaretPosition(e.getOffset());
	}

	public void changedUpdate(DocumentEvent e) {}


	class MyCompoundEdit extends CompoundEdit {
		public boolean isInProgress() {
			//  in order for the canUndo() and canRedo() methods to work
			//  assume that the compound edit is never in progress
			return false;
		}

		public void undo() throws CannotUndoException {
			//  End the edit so future edits don't get absorbed by this edit
			if (compoundEdit != null) compoundEdit.end();
			super.undo();
			//  Always start a new compound edit after an undo
			compoundEdit = null;
		}
	}

	/**
	 *Perform the Undo and update the state of the undo/redo Actions
	 */
	class UndoAction extends AbstractAction {
		public UndoAction() {
			putValue( Action.NAME, "Undo" );
			putValue( Action.SHORT_DESCRIPTION, getValue(Action.NAME) );
			putValue( Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_U) );
			putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z") );
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.undo();
				textComponent.requestFocusInWindow();
			} catch (CannotUndoException ex) {}
			updateUndoState();
			redoAction.updateRedoState();
		}

		private void updateUndoState() {
			setEnabled( undoManager.canUndo() );
		}
	}

	/**
	 * Perform the Redo and update the state of the undo/redo Actions
	 */
	class RedoAction extends AbstractAction {
		public RedoAction() {
			putValue( Action.NAME, "Redo" );
			putValue( Action.SHORT_DESCRIPTION, getValue(Action.NAME) );
			putValue( Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R) );
			putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK) );
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.redo();
				textComponent.requestFocusInWindow();
			} catch (CannotRedoException ex) {}
			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState() {
			setEnabled( undoManager.canRedo() );
		}
	}
}