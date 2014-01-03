package sma.tools.analyse;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import sma.agent.AgentCommercial;


public class AgentRepresentation extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JLabel satifaction;
	private JLabel production;
	private JLabel production_stock;
	private JLabel consumption;
	private JLabel consumption_stock;
	private JLabel money;
	private JLabel name;
	
	private JToolTip tip;

	public AgentRepresentation() {

		//setLayout(new GridLayout(3, 2));
		setLayout(new CircleLayout(true));
		
		satifaction = new JLabel();
		satifaction.setText("100");
		
		production = new JLabel();
		production.setText("???");
		
		production_stock = new JLabel();
		production_stock.setText("???");
		
		consumption = new JLabel();
		consumption.setText("???");
		
		consumption_stock = new JLabel();
		consumption_stock.setText("???");
		
		money = new JLabel();
		money.setText("???$");
		
		name = new JLabel();
		name.setText("<Name>");
		//name.setHorizontalAlignment(JLabel.CENTER);
		
		add(production);
		add(consumption);
		add(consumption_stock);
		add(production_stock);
		add(satifaction);
		//add(name);
		add(money);
	}
	
	public void update(AgentCommercial agentCommercial){
		System.out.println("Mise a jour des informations de "+agentCommercial.getName());
		
		satifaction.setText(String.format("%.2f", agentCommercial.getSatisfaction()));
		production.setText(""+agentCommercial.getProduction());
		production_stock.setText((int)agentCommercial.getStock_production()+"/"+(int)agentCommercial.getStock_max_production()+" ");
		consumption.setText(""+agentCommercial.getConsumption());
		consumption_stock.setText((int)agentCommercial.getStock_consumption()+"/"+(int)agentCommercial.getStock_max_consumption()+" ");
		money.setText(agentCommercial.getMoney()+"$");
		name.setText(agentCommercial.getLocalName());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawOval(0, 0, getPreferredSize().width-1, getPreferredSize().height-1);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(135, 135);
	}
	
}
