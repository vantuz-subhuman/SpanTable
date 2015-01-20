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
@Table(name="rows")
public class Row implements Comparable<Row> {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Column(name="indexInPage")
	private int indexInPage;
	
	@Column(name="height")
	private int height;
	
	@ManyToOne(targetEntity=Page.class, cascade=CascadeType.REFRESH, fetch=FetchType.LAZY)
	@JoinColumn(name="pageId")
	private Page page;
	
//	@OneToMany(targetEntity=Cell.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="row")
//	@IndexColumn(name="id")
//	private List<Cell> cells = new ArrayList<Cell>();
	
	public Row() {}
	
	public Row(int index/*, List<Cell> cells*/) {
		
		this.indexInPage = index;
//		this.cells = cells;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getIndexInPage() {
		return indexInPage;
	}
	public void setIndexInPage(int indexInPage) {
		this.indexInPage = indexInPage;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
//	public List<Cell> getCells() {
//		return cells;
//	}
//	public void setCells(List<Cell> cells) {
//		this.cells = cells;
//	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Row) {
			
			return getId() == ((Row) obj).getId();
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		
		return ((Long) getId()).hashCode();
	}
	
	@Override
	public int compareTo(Row o) {
		
		Long id1 = getId();
		Long id2 = o.getId();
		
		return id1.compareTo(id2);
	}
}
