/**
 * 
 */
package sma.tools.analyse;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.HashMap;

import javax.management.ReflectionException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sma.agent.AgentCommercial;

/**
 * @author Jeremy
 * Classe d'analyse graphique de la simulation (A TEST)
 */
public class Analyse extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Analyse instance;
	
	private HashMap<String, AgentRepresentation> agents;
	private JPanel graphe;
	
	
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
		graphe = new JPanel();
		graphe.setLayout(new CircleLayout());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(graphe);
		setSize(new Dimension(300, 100));
		//pack();
		setVisible(true);
	}
	
	public void agent_setup(AgentCommercial agentCommercial){
		AgentRepresentation agentRepresentation = new AgentRepresentation();
		graphe.add(agentRepresentation);
		agents.put(agentCommercial.getName(), agentRepresentation);
		//
		graphe.setSize(getPreferredSize().width, getPreferredSize().height);
		pack();
	}
	
	public void agent_update(AgentCommercial agentCommercial){
		agents.get(agentCommercial.getName()).update(agentCommercial);
	}
	
}
