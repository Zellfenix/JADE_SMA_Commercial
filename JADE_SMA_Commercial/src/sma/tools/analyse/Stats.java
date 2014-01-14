package sma.tools.analyse;

import sma.agent.AgentCommercial;
import sma.agent.Product;

public class Stats {

	private String name;
	
	private Product production;
	private double stock_production;
	private double stock_max_production;
	private double price;
	
	private Product consumption;
	private double stock_consumption;
	private double stock_max_consumption;
	
	private double money;
	private double satisfaction;
	
	public Stats(AgentCommercial agent) {
		update(agent);
	}

	public void update(AgentCommercial agent){
		name = agent.getName();
		
		production = agent.getProduction();
		stock_production = agent.getStock_production();
		stock_max_production = agent.getStock_max_production();
		price = agent.getPrice();
		
		consumption = agent.getConsumption();
		stock_consumption = agent.getStock_consumption();
		stock_max_consumption = agent.getStock_max_consumption();
		
		money = agent.getMoney();
		satisfaction = agent.getSatisfaction();
	}
	
	public String getName() {
		return name;
	}

	public Product getProduction() {
		return production;
	}

	public double getStock_production() {
		return stock_production;
	}

	public double getStock_max_production() {
		return stock_max_production;
	}

	public double getPrice() {
		return price;
	}

	public Product getConsumption() {
		return consumption;
	}

	public double getStock_consumption() {
		return stock_consumption;
	}

	public double getStock_max_consumption() {
		return stock_max_consumption;
	}

	public double getMoney() {
		return money;
	}

	public double getSatisfaction() {
		return satisfaction;
	}
	
}
