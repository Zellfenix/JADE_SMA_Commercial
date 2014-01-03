package sma.tools.analyse;

import sma.agent.AgentCommercial;
import sma.agent.Products;

public class Stats {

	private String name;
	
	private Products production;
	private float stock_production;
	private float stock_max_production;
	private float price;
	
	private Products consumption;
	private float stock_consumption;
	private float stock_max_consumption;
	
	private float money;
	private float satisfaction;
	
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

	public Products getProduction() {
		return production;
	}

	public float getStock_production() {
		return stock_production;
	}

	public float getStock_max_production() {
		return stock_max_production;
	}

	public float getPrice() {
		return price;
	}

	public Products getConsumption() {
		return consumption;
	}

	public float getStock_consumption() {
		return stock_consumption;
	}

	public float getStock_max_consumption() {
		return stock_max_consumption;
	}

	public float getMoney() {
		return money;
	}

	public float getSatisfaction() {
		return satisfaction;
	}
	
}
