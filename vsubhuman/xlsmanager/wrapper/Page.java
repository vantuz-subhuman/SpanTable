package vsubhuman.xlsmanager.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name="pages")
public class Page {
	
	public static final int FIRST_ROW = 0;
	public static final int LAST_ROW = 1;
	public static final int FIRST_COLUMN = 2;
	public static final int LAST_COLUMN = 3;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name="indexInDocument")
	private int indexInDocument;
	
	@ManyToOne(targetEntity=Document.class, cascade=CascadeType.REFRESH, fetch=FetchType.LAZY)
	@JoinColumn(name="documentId")
	private Document document;
	
	@Column(name="name", nullable=false)
	private String name;

	@Column(name="rowsCount")
	private int rowsCount;
	
	@Column(name="columnsCount")
	private int columnsCount;
	
	@OneToMany(targetEntity=Row.class, cascade=CascadeType.REFRESH, fetch=FetchType.LAZY, mappedBy="page")
	@IndexColumn(name="id")
	private List<Row> rows = new ArrayList<Row>();
	
	@OneToMany(targetEntity=Cell.class, cascade=CascadeType.REFRESH, fetch=FetchType.LAZY, mappedBy="page")
	@IndexColumn(name="id")
	private List<Cell> cells = new ArrayList<Cell>();
	
	@Column(name="widths")
	private int[] colWidths;
	
	@Column(name="merge")
	private int[][] mergeRegions;
	
	public Page() {}
	
	public Page(int index, String name, int rowsCount, int columnsCount,
			List<Row> rows, int[] colWidths, int[][] mergeRegions) {
		
		this.indexInDocument = index;
		this.name = name;
		this.rowsCount = rowsCount;
		this.columnsCount = columnsCount;
		this.rows = rows;
		this.colWidths = colWidths;
		this.mergeRegions = mergeRegions;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getIndexInDocument() {
		return indexInDocument;
	}
	public void setIndexInDocument(int indexInDocument) {
		this.indexInDocument = indexInDocument;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRowsCount() {
		return rowsCount;
	}
	public void setRowsCount(int rowsCount) {
		this.rowsCount = rowsCount;
	}
	public int getColumnsCount() {
		return columnsCount;
	}
	public void setColumnsCount(int columnsCount) {
		this.columnsCount = columnsCount;
	}
	public List<Row> getRows() {
		return rows;
	}
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	public List<Cell> getCells() {
		return cells;
	}
	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
	public int[] getColWidths() {
		return colWidths;
	}
	public void setColWidths(int[] colWidths) {
		this.colWidths = colWidths;
	}
	public int[][] getMergeRegions() {
		return mergeRegions;
	}
	public void setMergeRegions(int[][] mergeRegions) {
		this.mergeRegions = mergeRegions;
	}
}
