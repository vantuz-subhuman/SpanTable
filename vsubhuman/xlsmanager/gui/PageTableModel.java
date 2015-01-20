package vsubhuman.xlsmanager.gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import vsubhuman.xlsmanager.color.ColorHandler;
import vsubhuman.xlsmanager.wrapper.Cell;
import vsubhuman.xlsmanager.wrapper.Page;
import jp.gr.java_conf.tame.swing.table.AttributiveCellTableModel;
import jp.gr.java_conf.tame.swing.table.CellSpan;

public class PageTableModel extends AttributiveCellTableModel {

	private static final long serialVersionUID = 1L;
	
	public static final int BACKGROUND = 0, FOREGROUND = 1; 
	public static final int HORISONTAL = 0, VERTICAL = 1;

	private boolean cutRows, cutCols;
	
	private Color[][][] colors;
	private int[][][] alignment;
	
	public PageTableModel(Page page, boolean cutRows, boolean cutCols) {

		this.cutRows = cutRows;
		this.cutCols = cutCols;
		
		int rowsCount = page.getRowsCount();
		int columnsCount = page.getColumnsCount() + 1;
		int[][] mergeRegions = page.getMergeRegions();
		
		String[] names = TableHandler.createColumnsNames(columnsCount);
		String[][] data = null;

		List<Cell> cells = page.getCells();
		Cell cell = null;
		
		String value = null;
		
		int emptyRows = 0;
		int lastColumnWithData = 0;
		for (int i = 0; i < cells.size(); i++) {
			
			cell = cells.get(i);
			if (cell != null) {
				
				value = cell.getValue();
				if (value == null) continue;

				if (cutRows) {
					cutRows = false;
					emptyRows = cell.getRowIndex();
					rowsCount -= emptyRows;
				}
				
				if (data == null) {
					data = new String[rowsCount][columnsCount];
					colors = new Color[rowsCount][columnsCount][2];
					alignment = new int[rowsCount][columnsCount][2];
				}
				
				if (cutCols && cell.getColumnIndex() > lastColumnWithData) {
					lastColumnWithData = cell.getColumnIndex();
				}
				
				int rowIndex = cell.getRowIndex() - emptyRows;
				int columnIndex = cell.getColumnIndex() + 1;
				
				data[rowIndex][columnIndex] = value;
				
				colors[rowIndex][columnIndex][BACKGROUND] =
					ColorHandler.hexStringToColor(
							cell.getBackgroundColorHex());
				colors[rowIndex][columnIndex][FOREGROUND] =
					ColorHandler.hexStringToColor(
							cell.getForegroundColorHex());
					
				alignment[rowIndex][columnIndex][HORISONTAL] = cell.getAlignment();
				alignment[rowIndex][columnIndex][VERTICAL] = cell.getValignment();
			}
		}
		
		if (data != null) {
			
			if (cutCols) {
				
				int columns = lastColumnWithData + 2;
				for (int i = 0; i < data.length; i++) {
					
					data[i] = Arrays.copyOf(data[i], columns);
				}
				
				names = Arrays.copyOf(names, columns);
			}
			
		} else {
			
			data = new String[0][0];
		}
		
		setDataVector(data, names);
		
		CellSpan cellSpan = (CellSpan) getCellAttribute();
		if (mergeRegions != null) {
			
			int[][][] merges = formatMergeRegions(mergeRegions, emptyRows, lastColumnWithData);
			for (int m = 0; m < merges.length; m++) {
				
				cellSpan.combine(merges[m][0], merges[m][1]);
			}
		}
	}
	
	public Color getColorAt(int row, int column, int ground) {
		
		if (ground != BACKGROUND && ground != FOREGROUND) {
			
			throw new IllegalArgumentException("Illegal ground specified!");
			
		} else {
		
			return colors[row][column][ground];
		}
	}
	
	public int getAlignmentAt(int row, int column, int axis) {
		
		if (axis != HORISONTAL && axis != VERTICAL) {
			
			throw new IllegalArgumentException("Illegal axis specified!");
			
		} else {
			
			return alignment[row][column][axis];
		}
	}
	
	private int[][][] formatMergeRegions(int[][] mergeRegions, int emptyRows, int lastColWidthData) {

		if (mergeRegions == null || mergeRegions.length == 0) {
			
			return null;
			
		} else {
		
			int[][][] merges = new int[mergeRegions.length][][];
			
			for (int region = 0; region < mergeRegions.length; region++) {
		
				if (mergeRegions[region].length == 4) {
				
					merges[region] = new int[2][];
					
					int firstRow = mergeRegions[region][Page.FIRST_ROW];
					int lastRow = mergeRegions[region][Page.LAST_ROW];
					int firstColumn = mergeRegions[region][Page.FIRST_COLUMN];
					int lastColumn = mergeRegions[region][Page.LAST_COLUMN];
					
					if (firstRow > lastRow || firstColumn > lastColumn) {
						
						return null;
						
					} else {

						if (cutRows) {
							
							firstRow -= emptyRows;
							lastRow -= emptyRows;
							
							if (firstRow < 0 && lastRow > 0) {
								
								firstRow = 0;
							}
						}
						
						if (cutCols
								&& firstColumn <= lastColWidthData
								&& lastColumn > lastColWidthData) {
							
							lastColumn = lastColWidthData;
						}
						
						merges[region][0] = new int[(lastRow - firstRow) + 1];
						for (int row = 0; firstRow <= lastRow; row++, firstRow++) {
							
							merges[region][0][row] = firstRow;
						}
						
						merges[region][1] = new int[(lastColumn - firstColumn) + 1];
						for (int col = 0; firstColumn <= lastColumn; col++, firstColumn++) {
							
							merges[region][1][col] = firstColumn + 1;
						}
					}
					
				} else {
					
					return null;
				}
			}
			
			return merges;
		}
	}
}
