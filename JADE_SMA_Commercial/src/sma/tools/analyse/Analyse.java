/**
 * 
 */
package sma.tools.analyse;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import sma.agent.AgentCommercial;

/**
 * @author Jeremy
 * Classe d'analyse graphique de la simulation (A TEST)
 */
public class Analyse extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Analyse instance;
	
	//protected HashMap<String, AgentRepresentation> agents;
	protected HashMap<String, Stats> agent_stats;
	protected HashMap<String, AgentChart> agent_chart;
	
	private JTabbedPane tabbedPane;
	private JPanel panel_stats;
	//private JPanel panel_graphe;
	private JPanel panel_charts;
	private JTable jtable;
	private TableModelDynamique tableModel;

	private int count = 0;
	private long last_update = System.currentTimeMillis();


	public Analyse() throws HeadlessException {
		super();
		init();
	}

	public static Analyse getInstance() {
		if(instance == null){
			instance = new Analyse();
		}
		return instance;
	}
	
	private void init(){
		//agents = new HashMap<String, AgentRepresentation>(2);
		agent_stats = new HashMap<String, Stats>(2);
		agent_chart = new HashMap<String, AgentChart>(2);
		
		tabbedPane = new JTabbedPane();
		
		panel_stats = new JPanel();
		panel_stats.setLayout(new BorderLayout());
		tableModel = new TableModelDynamique();
		
		jtable = new JTable(tableModel);
		jtable.setDefaultRenderer(Object.class, new MyRenderer());
		
		panel_stats.add(jtable.getTableHeader(), BorderLayout.NORTH);
		panel_stats.add(jtable, BorderLayout.CENTER);
		
		//panel_graphe = new JPanel();
		//panel_graphe.setLayout(new CircleLayout());

		panel_charts = new JPanel();
		//panel_chart.setLayout(new CircleLayout());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//tabbedPane.addTab("Graphe", panel_graphe);
		tabbedPane.addTab("Statistiques", panel_stats);
		tabbedPane.addTab("Graphiques", new JScrollPane(panel_charts));
		getContentPane().add(tabbedPane);
		setSize(1024, 700);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		//pack();
		setVisible(true);
	}
	
	public void agent_setup(AgentCommercial agentCommercial){
		//AgentRepresentation agentRepresentation = new AgentRepresentation();
		//panel_graphe.add(agentRepresentation);
		//agents.put(agentCommercial.getName(), agentRepresentation);
		
		agent_stats.put(agentCommercial.getName(), new Stats(agentCommercial));
		tableModel.setStats( new ArrayList<Stats>(agent_stats.values()) );
		
		//panel_graphe.setSize(getPreferredSize().width, getPreferredSize().height);
		//pack();
	}
	
	public void agent_update(AgentCommercial agentCommercial){
		agent_stats.get(agentCommercial.getName()).update(agentCommercial);
		tableModel.fireTableDataChanged();
		//agents.get(agentCommercial.getName()).update(agentCommercial);
		agent_stats.get(agentCommercial.getName()).setStatus("Alive");

		if (System.currentTimeMillis() - last_update >= 500) {
			for (Entry<String, Stats> e : Analyse.getInstance().agent_stats.entrySet()) {
				if (!agent_chart.containsKey(e.getKey())) {
					AgentChart chart = new AgentChart(e.getKey());
					agent_chart.put(e.getKey(), chart);
					panel_charts.add(chart);
				}
				agent_chart.get(e.getKey()).update(count, e.getValue());
			}
			count++;
			panel_charts.getParent().invalidate();
			panel_charts.getParent().revalidate();
			panel_charts.getParent().repaint();
			last_update = System.currentTimeMillis();
		}
	}
	
	public void agent_dead(AgentCommercial agentCommercial){
		agent_stats.get(agentCommercial.getName()).update(agentCommercial);
		tableModel.fireTableDataChanged();
		//agents.get(agentCommercial.getName()).update(agentCommercial);
		agent_stats.get(agentCommercial.getName()).setStatus("Dead");
	}

}
