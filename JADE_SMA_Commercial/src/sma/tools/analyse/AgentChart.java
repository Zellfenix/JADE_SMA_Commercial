package sma.tools.analyse;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

public class AgentChart extends JPanel implements DatasetChangeListener {
	private static final long serialVersionUID = 1L;

	private DefaultCategoryDataset dataset;
	private JFreeChart chart;
	private CategoryPlot plot;
	private LineAndShapeRenderer renderer;
	private ChartPanel chartPanel;

	public AgentChart(String name) {
		super(new BorderLayout());

		dataset = new DefaultCategoryDataset();
		dataset.addChangeListener(this);

		name = name.split("@")[0];
		chart = ChartFactory.createLineChart(
			name,                      // chart title
			"Type",                    // domain axis label
			"Value",                   // range axis label
			dataset,                   // data
			PlotOrientation.VERTICAL,  // orientation
			true,                      // include legend
			true,                      // tooltips
			false                      // urls
		);

		plot = (CategoryPlot) chart.getPlot();
		renderer = (LineAndShapeRenderer) plot.getRenderer();
		//renderer.setDrawShapes(true);
		renderer.setSeriesStroke(
			0, new BasicStroke(
				2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] {10.0f, 6.0f}, 0.0f
			)
		);
		renderer.setSeriesStroke(
    		1, new BasicStroke(
				2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] {10.0f, 6.0f}, 0.0f
			)
		);
		renderer.setSeriesStroke(
    		2, new BasicStroke(
				2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] {10.0f, 6.0f}, 0.0f
			)
		);
		renderer.setSeriesStroke(
    		3, new BasicStroke(
				2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] {10.0f, 6.0f}, 0.0f
			)
		);
		renderer.setSeriesStroke(
    		4, new BasicStroke(
				2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] {10.0f, 6.0f}, 0.0f
			)
		);

        chartPanel = new ChartPanel(chart);
        add(chartPanel);
        setPreferredSize(new Dimension(450,300));
	}

	public void update(int count, Stats stats) {
		/*try {
			if (count-20 >= 0)
				dataset.removeColumn(""+(count-20)%20);
		} catch (Exception e) {}*/
		dataset.setValue(stats.getSatisfaction(),		"satisfaction",	""+count);
		dataset.setValue(stats.getStock_production(),	"stock_prod",	""+count);
		dataset.setValue(stats.getPrice(),				"price",		""+count);
		dataset.setValue(stats.getStock_consumption(),	"stock_cons",	""+count);
		dataset.setValue(stats.getMoney(),				"money",		""+count);
	}

	@Override
	public void datasetChanged(DatasetChangeEvent event) {}

}
