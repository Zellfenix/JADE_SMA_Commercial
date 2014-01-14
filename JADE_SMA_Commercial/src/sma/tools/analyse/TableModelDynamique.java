package sma.tools.analyse;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TableModelDynamique extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<Stats> stats = new ArrayList<Stats>();
 
    private final String[] entetes = {"Name", "Status", "Satisfaction", "Satisfaction Average", "Production", "Production Stock", "Price", "Price Average", "Consuption", "Consuption Stock", "Money", "Money Average"};
 
    public TableModelDynamique() {
        super();
    }
 
    public int getRowCount() {
        return stats.size();
    }
 
    public int getColumnCount() {
        return entetes.length;
    }
 
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }
 
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return stats.get(rowIndex).getName();
            case 1:
                return stats.get(rowIndex).getStatus();
            case 2:
                return stats.get(rowIndex).getSatisfaction();
            case 3:
                return stats.get(rowIndex).getAverage_satifaction();
            case 4:
                return stats.get(rowIndex).getProduction();
            case 5:
                return stats.get(rowIndex).getStock_production();
            case 6:
                return stats.get(rowIndex).getPrice();
            case 7:
                return stats.get(rowIndex).getAverage_price();
            case 8:
                return stats.get(rowIndex).getConsumption();
            case 9:
                return stats.get(rowIndex).getStock_consumption();
            case 10:
                return stats.get(rowIndex).getMoney();
            case 11:
                return stats.get(rowIndex).getAverage_money();
            default:
                return null; //Ne devrait jamais arriver
        }
    }
 
    public void add(Stats stats) {
        this.stats.add(stats);
 
        fireTableRowsInserted(this.stats.size() -1, this.stats.size() -1);
    }
 
    public void remove(int rowIndex) {
    	this.stats.remove(rowIndex);
 
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void setStats(List<Stats> stats){
    	this.stats = stats;
    }
}

