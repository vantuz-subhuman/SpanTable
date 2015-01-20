package vsubhuman.xlsmanager.wrapper;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="cells")
public class Cell {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

//	@ManyToOne(targetEntity=Row.class, cascade=CascadeType.REFRESH, fetch=FetchType.LAZY)
//	@JoinColumn(name="rowId")
//	private Row row;
	
	@ManyToOne(targetEntity=Page.class, cascade=CascadeType.REFRESH, fetch=FetchType.LAZY)
	@JoinColumn(name="pageId")
	private Page page;
	
	@Column(name="columnIndex")
	private int columnIndex;
	
	@Column(name="rowIndex")
	private int rowIndex;

	@Column(name="value")
	private String value;
	
	@Column(name="alignment")
	private int alignment;
	
	@Column(name="valignment")
	private int valignment;
	
	@Column(name="bgColor")
	private String backgroundColorHex; 
	
	@Column(name="fgColor")
	private String foregroundColorHex; 
	
	public Cell() {}
	
	public Cell(int column, String value, int alignment, int valignment,
			String bg, String fg) {
		
		this.columnIndex = column;
		this.value = value;
		this.alignment = alignment;
		this.valignment = valignment;
		this.backgroundColorHex = bg;
		this.foregroundColorHex = fg;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
//	public Row getRow() {
//		return row;
//	}
//	public void setRow(Row row) {
//		this.row = row;
//	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public int getColumnIndex() {
		return columnIndex;
	}
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getAlignment() {
		return alignment;
	}
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	public int getValignment() {
		return valignment;
	}
	public void setValignment(int valignment) {
		this.valignment = valignment;
	}
	public String getBackgroundColorHex() {
		return backgroundColorHex;
	}
	public void setBackgroundColorHex(String backgroundColorHex) {
		this.backgroundColorHex = backgroundColorHex;
	}
	public String getForegroundColorHex() {
		return foregroundColorHex;
	}
	public void setForegroundColorHex(String foregroundColorHex) {
		this.foregroundColorHex = foregroundColorHex;
	}
}
