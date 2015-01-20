package vsubhuman.xlsmanager.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import jp.gr.java_conf.tame.swing.table.CellSpan;
import jp.gr.java_conf.tame.swing.table.MultiSpanCellTable;
import jp.gr.java_conf.tame.swing.table.MultiSpanCellTableUI;
import vsubhuman.swing.spantable.MultiSpanCellTableSelectionHandler;
import vsubhuman.xlsmanager.wrapper.Page;
import vsubhuman.xlsmanager.wrapper.Row;

public class PageTable extends MultiSpanCellTable implements MouseWheelListener {

	private static final long serialVersionUID = 1L;
	public static final int MINIMUM_COLUMN_WIDTH = 50;
	public static final int PREFERRED_COLUMN_WIDTH = 100;
	public static final int PREFERRED_ROW_HEIGHT = 16;
	private static final int AUTO_RESIZING_MODE = AUTO_RESIZE_SUBSEQUENT_COLUMNS; 

	private Renderer renderer = null;
	private final Page page;
	private List<Row> rows = null;
	private int[] columnsWidth = null;;
	
	private final boolean cutTopEmptyRows;
	private final boolean cutLastEmptyColumns;
	
	private MultiSpanCellTableSelectionHandler handler = null;
	
	public PageTable(Page page, boolean cutTopEmptyRows, boolean cutLastEmptyColumns) {
		
		super(null);
		this.page = page;
		this.rows = page.getRows();
		this.columnsWidth = page.getColWidths();
		
		this.cutTopEmptyRows = cutTopEmptyRows;
		this.cutLastEmptyColumns = cutLastEmptyColumns;
		
		setGridColor(Color.LIGHT_GRAY);
		setSelectionBackground(new Color(220,220,220));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnSelectionAllowed(true);
		getColumnModel().setColumnMargin(0);
		setRowMargin(0);
		addMouseWheelListener(this);

		PageTableModel model = new PageTableModel(page, cutTopEmptyRows, cutLastEmptyColumns);
		
		if (cutLastEmptyColumns) {
				
			columnsWidth = Arrays.copyOf(columnsWidth, model.getColumnCount() - 1);
		}
		
		setModel(model);

		CellSpan cellSpan = (CellSpan) model.getCellAttribute();
		handler = MultiSpanCellTableSelectionHandler.handle(this, cellSpan);
		handler.setMinimumSelectableColumn(1);

		((MultiSpanCellTableUI) getUI()).setDrawGrid(false);
	}

	void setSelectedCell(int row, int column) {

		column += 1;
		
		Rectangle r = getCellRect(row, column, true);
		requestFocus();
		handler.setSelectedCell(row, column);
		scrollRectToVisible(r);
	}
	
	public boolean isCutTopEmptyRows() {
		return cutTopEmptyRows;
	}
	
	public boolean isCutLastEmptyColumns() {
		return cutLastEmptyColumns;
	}
	
	private int minimumRowHeight = 15;
	private int maximumRowHeight = 150;
	private int maximumColWidth = 550;
	private double zoomRate = 1.1d;
	
	public void zoomIn() {

		for (int i = 1; i < getColumnCount(); i++) {
			
			int w = getColumnModel().getColumn(i).getWidth();
			w *= zoomRate;
			if (w > maximumColWidth) {
				
				w = maximumColWidth;
			}
			getColumnModel().getColumn(i).setPreferredWidth(w);
		}
		
		int h = getRowHeight();
		h *= zoomRate;
		if (h > maximumRowHeight) {
			
			h = maximumRowHeight;
		}
		setRowHeight(h);
	}
	
	public void zoomOut() {

		for (int i = 1; i < getColumnCount(); i++) {
			
			int w = getColumnModel().getColumn(i).getWidth();
			w /= zoomRate;
			getColumnModel().getColumn(i).setPreferredWidth(w);
		}
		
		int h = getRowHeight();
		h /= zoomRate;
		if (h < minimumRowHeight) {
			
			h = minimumRowHeight;
		}
		setRowHeight(h);
	}
	
	public void setFitZoom(boolean val) {

		if ((val && getAutoResizeMode() != AUTO_RESIZING_MODE)
				|| (!val && getAutoResizeMode() != AUTO_RESIZE_OFF)) {
			
			setAutoResizeMode(val ? AUTO_RESIZING_MODE : AUTO_RESIZE_OFF);
		}
	}
	
	private boolean sized = false;
	
	@Override
	public void setSize(int width, int height) {

		super.setSize(width, height);
		
		if (!sized) {
			
			resizeColumns();
			resizeRows();
			sized = true;
		}
	}
	
	private void resizeColumns() {
		
		if (getAutoResizeMode() != AUTO_RESIZE_OFF) {
			
			setAutoResizeMode(AUTO_RESIZE_OFF);
		}

		TableCellRenderer renderer = getTableHeader().getDefaultRenderer();
		Component c = renderer.getTableCellRendererComponent(this,
				String.valueOf(page.getRowsCount()), false, false, 0, 0);
		int numColWidth = c.getPreferredSize().width + 10;
		
		getColumnModel().getColumn(0).setMinWidth(numColWidth);
		getColumnModel().getColumn(0).setMaxWidth(numColWidth);
		
		int colw;
		for (int i = 0; i < columnsWidth.length; i++) {

			colw = columnsWidth[i];
			getColumnModel().getColumn(i + 1).setPreferredWidth(
					colw < MINIMUM_COLUMN_WIDTH ? MINIMUM_COLUMN_WIDTH : colw);
		}
	}
	
	private void resizeRows() {
		
		if (rows != null) {
			
			for (Row row : rows) {
				
				//TODO
				setRowHeight(row.getIndexInPage(), row.getHeight());
			}
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		
		return false;
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		
		if (renderer == null) {
				
			renderer = new Renderer();
		}
			
		return renderer;
	}
	
	private Border defaultBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.LIGHT_GRAY),
			BorderFactory.createEmptyBorder(0, 2, 0, 2));
	
	private Border focusedBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK),
			BorderFactory.createEmptyBorder(0, 2, 0, 2));
	
	private Color defaultBackgroundColor = Color.WHITE;
	private Color defaultForegroundColor = Color.BLACK;
	
	private Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
	
	private class Renderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;

		private Color bg = null;
		private Color fg = null;

		private TableCellRenderer headerRenderer = null;
		private PageTableModel model = null;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

			if (headerRenderer == null) {
				
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			
			if (model == null) {
				
				model = (PageTableModel) getModel();
			}

			JLabel lab;
			
			if (column == 0) {
				
				lab = (JLabel) headerRenderer.getTableCellRendererComponent(
						table, String.valueOf(row + 1), false, false, row, column);
				
			} else {
				
				lab = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus,
						row, column - 1);

				lab.setFont(font);
				
				lab.setHorizontalAlignment(
						model.getAlignmentAt(row, column, PageTableModel.HORISONTAL));
				lab.setVerticalAlignment(
						model.getAlignmentAt(row, column, PageTableModel.VERTICAL));
				
				if (hasFocus) {
						
					lab.setBorder(focusedBorder);
					lab.setForeground(Color.BLACK);
						
				} else {
						
					lab.setBorder(defaultBorder);
						
					bg = model.getColorAt(row, column, PageTableModel.BACKGROUND);
					lab.setBackground(bg == null ? defaultBackgroundColor : bg);
						
					fg = model.getColorAt(row, column, PageTableModel.FOREGROUND);
					lab.setForeground(fg == null ? defaultForegroundColor : fg);
				}
			}
			
			return lab;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		if (e.isControlDown() && getAutoResizeMode() == AUTO_RESIZE_OFF) {
			
			int x = e.getWheelRotation();
			if (x > 0) {
				
				zoomOut();
				
			} else {
				
				zoomIn();
			}
			
		} else {
			
			getParent().dispatchEvent(e);
		}
	}
	
	public Page getPage() {
		return page;
	}
}
