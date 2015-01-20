package vsubhuman.swing.spantable;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.DefaultListSelectionModel;

import jp.gr.java_conf.tame.swing.table.CellSpan;
import jp.gr.java_conf.tame.swing.table.MultiSpanCellTable;

/**
 * @author VSubhuman
 * @version 1.0
 */
public class MultiSpanCellTableSelectionHandler
implements KeyListener,	MouseListener, MouseMotionListener {

	private MultiSpanCellTable table;
	private CellSpan cellSpan;

	/**
	 * @param table
	 * @param cellSpan
	 * @since 1.0
	 */
	public MultiSpanCellTableSelectionHandler(MultiSpanCellTable table, CellSpan cellSpan) {
		
		this.table = table;
		this.cellSpan = cellSpan;
		
		minimumSelectableRow = 0;
		maximumSelectableRow = table.getRowCount() - 1;
		minimumSelectableColumn = 0;
		maximumSelectableColumn = table.getColumnCount() - 1;
		
		table.setSelectionModel(new SelectionModel());
		table.getColumnModel().setSelectionModel(new SelectionModel());
	}
	
////////////
// SERVICE
////////////
	
	private boolean selectionAllowed;

	private int minimumSelectableColumn;
	private int maximumSelectableColumn;
	private int minimumSelectableRow;
	private int maximumSelectableRow;
	
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;
	private boolean tab;
	
	private int selectedRow = -1;
	private int selectedColumn = -1;
	private int visibleRow;
	private int visibleColumn;
	private int rowBeforeMerge;
	private int columnBeforeMerge;
	
	private boolean merged;
	
	public void setSelectedCell(int row, int col) {
		
		selectedRow = row;
		selectedColumn = col;
		
		if (getCellSpan().isVisible(selectedRow, selectedColumn)) {
			
			setTableSelection(selectedRow, selectedColumn);
						
		} else {

			toMergedCell();
		} 
	}
	
	private void setTableSelection(int row, int col) {
		
		visibleRow = row;
		visibleColumn = col;
		
		setSelectionAllowed(true);
		table.changeSelection(row, col, false, false);
		setSelectionAllowed(false);
	}
	
	private void selectCellAtPoint(Point p) {
		
		selectedRow = table.rowAtPoint(p);
		selectedColumn = table.columnAtPoint(p);

		if (selectedRow >= minimumSelectableRow
				&& selectedRow <= maximumSelectableRow
				&& selectedColumn >= minimumSelectableColumn
				&& selectedColumn <= maximumSelectableColumn) {
			
			rowBeforeMerge = selectedRow;
			columnBeforeMerge = selectedColumn;
		}
		
		setTableSelection(selectedRow, selectedColumn);
	}
	
	private void toMergedCell() {
		
		int[] span = getCellSpan().getSpan(selectedRow, selectedColumn);
		
		if (up || down) columnBeforeMerge = selectedColumn;
		else if (left || right) rowBeforeMerge = selectedRow;
		
		selectedRow += span[CellSpan.ROW];
		selectedColumn += span[CellSpan.COLUMN];
		
		if (up || down) rowBeforeMerge = selectedRow;
		else if (left || right) columnBeforeMerge = selectedColumn;
		
		setTableSelection(selectedRow, selectedColumn);
		
		merged = true;
	}
	
	public boolean isSelectionAllowed() {
		return selectionAllowed;
	}
	
	public void setSelectionAllowed(boolean selectionAllowed) {
		this.selectionAllowed = selectionAllowed;
	}
	
	private class SelectionModel extends DefaultListSelectionModel {
		
		private static final long serialVersionUID = 1L;
		
		@Override
		public void setSelectionInterval(int arg0, int arg1) {
			
			if (isSelectionAllowed()) {
				
				super.setSelectionInterval(arg0, arg1);
			}
		}
	}
	
////////
// Key
////////
	
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {

		int kc = e.getKeyCode();
		up = kc == KeyEvent.VK_UP;
		down = kc == KeyEvent.VK_DOWN;
		left = kc == KeyEvent.VK_LEFT;
		right = kc == KeyEvent.VK_RIGHT;
		tab = kc == KeyEvent.VK_TAB;
		
		if (e.getModifiers() == 0 && (up || down || left || right || tab)) {
			
			boolean start = selectedRow < 0 || selectedColumn < 0;
			
			if (start) {
				
				selectedRow = minimumSelectableRow;
				selectedColumn = minimumSelectableColumn;
				setTableSelection(selectedRow, selectedColumn);
				
			} else {
				
				int[] span = getCellSpan().getSpan(selectedRow, selectedColumn);
				int rowSpan = span[CellSpan.ROW];
				int colSpan = span[CellSpan.COLUMN];
				
				if (up && selectedRow > minimumSelectableRow) {
					
					selectedRow--;
				
				} else if (down && selectedRow + rowSpan < maximumSelectableRow + 1) {
						
					selectedRow += rowSpan;
					
				} else if (left && selectedColumn > minimumSelectableColumn) {
					
					selectedColumn--;
					
				} else if (right && selectedColumn + colSpan < maximumSelectableColumn + 1) {
					
					selectedColumn += colSpan;
					
				} else if (tab) {
					
					if (selectedColumn + colSpan < maximumSelectableColumn + 1) {
						
						selectedColumn += colSpan;

					} else if (merged) {
						
						if (rowBeforeMerge < maximumSelectableRow) {
							
							selectedColumn = minimumSelectableColumn;
							rowBeforeMerge++;
							selectedRow = rowBeforeMerge;
							
						} else {
							
							selectedColumn = minimumSelectableColumn;
							selectedRow = minimumSelectableRow;
							rowBeforeMerge = selectedRow;
						}
						
					} else if (selectedRow < maximumSelectableRow) {
						
						selectedColumn = minimumSelectableColumn;
						selectedRow++;
						
					} else {
						
						selectedColumn = minimumSelectableColumn;
						selectedRow = minimumSelectableRow;
					}
					
					right = true;
				}
				
				boolean rowChanged = selectedRow != visibleRow;
				boolean colChanged = selectedColumn != visibleColumn;
				
				if (rowChanged || colChanged) {
					
					if (merged) {
							
						if (up || down) selectedColumn = columnBeforeMerge;
						else if (left || right) selectedRow = rowBeforeMerge;
							
						merged = false;
					}
						
					setSelectedCell(selectedRow, selectedColumn);
				}
			}
			
		} else {
			
			table.getParent().dispatchEvent(e);
		}
	}
	
//////////
// MOUSE
//////////
	
	private boolean isFirstButtonPressed;
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			
			isFirstButtonPressed = false;
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			
			isFirstButtonPressed = true;
			selectCellAtPoint(e.getPoint());
		}
	}
	
/////////////////
// MOUSE MOTION
/////////////////
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {

		if (isFirstButtonPressed) {
			
			selectCellAtPoint(e.getPoint());
		}
	}
	
///////////
// PUBLIC
///////////
	
	/**
	 * @since 1.0
	 */
	public MultiSpanCellTable getTable() {
		return table;
	}
	
	/**
	 * @since 1.0 
	 */
	public CellSpan getCellSpan() {
		return cellSpan;
	}
	
	/**
	 * @param table
	 * @param cellSpan
	 * @return
	 * @since 1.0
	 */
	public static MultiSpanCellTableSelectionHandler handle(MultiSpanCellTable table, CellSpan cellSpan) {
		
		MultiSpanCellTableSelectionHandler handler =
			new MultiSpanCellTableSelectionHandler(table, cellSpan);
		
		table.addKeyListener(handler);
		table.addMouseListener(handler);
		table.addMouseMotionListener(handler);
		
		return handler;
	}

	public int getMinimumSelectableColumn() {
		return minimumSelectableColumn;
	}

	public void setMinimumSelectableColumn(int minimumSelectableColumn) {
		this.minimumSelectableColumn = minimumSelectableColumn;
	}

	public int getMaximumSelectableColumn() {
		return maximumSelectableColumn;
	}

	public void setMaximumSelectableColumn(int maximumSelectableColumn) {
		this.maximumSelectableColumn = maximumSelectableColumn;
	}

	public int getMinimumSelectableRow() {
		return minimumSelectableRow;
	}

	public void setMinimumSelectableRow(int minimumSelectableRow) {
		this.minimumSelectableRow = minimumSelectableRow;
	}

	public int getMaximumSelectableRow() {
		return maximumSelectableRow;
	}

	public void setMaximumSelectableRow(int maximumSelectableRow) {
		this.maximumSelectableRow = maximumSelectableRow;
	}
}
