/**
 * 
 */
package sma.tools.analyse;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
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
	
	private HashMap<String, AgentRepresentation> agents;
	private HashMap<String, Stats> agent_stats;
	
	private JTabbedPane tabbedPane;
	private JPanel panel_stats;
	private JPanel panel_graphe;
	private TableModelDynamique tableModel;
	
	
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
		agents = new HashMap<String, AgentRepresentation>();
		agent_stats = new HashMap<String, Stats>();
		
		tabbedPane = new JTabbedPane();
		
		panel_stats = new JPanel();
		panel_stats.setLayout(new BorderLayout());
		tableModel = new TableModelDynamique();
		JTable jtable = new JTable(tableModel);
		panel_stats.add(jtable.getTableHeader(), BorderLayout.NORTH);
		panel_stats.add(jtable, BorderLayout.CENTER);
		
		panel_graphe = new JPanel();
		panel_graphe.setLayout(new CircleLayout());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabbedPane.addTab("Graphe", panel_graphe);
		tabbedPane.addTab("Statistiques", panel_stats);
		getContentPane().add(tabbedPane);
		setSize(new Dimension(300, 100));
		//pack();
		setVisible(true);
	}
	
	public void agent_setup(AgentCommercial agentCommercial){
		AgentRepresentation agentRepresentation = new AgentRepresentation();
		panel_graphe.add(agentRepresentation);
		agents.put(agentCommercial.getName(), agentRepresentation);
		
		agent_stats.put(agentCommercial.getName(), new Stats(agentCommercial));
		tableModel.setStats( new ArrayList<Stats>(agent_stats.values()) );
		
		panel_graphe.setSize(getPreferredSize().width, getPreferredSize().height);
		pack();
	}
	
	public void agent_update(AgentCommercial agentCommercial){
		agent_stats.get(agentCommercial.getName()).update(agentCommercial);
		tableModel.fireTableDataChanged();
		agents.get(agentCommercial.getName()).update(agentCommercial);
	}
	
}
