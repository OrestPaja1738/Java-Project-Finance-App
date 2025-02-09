package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class Asset{
	String name;
	double price;
	
	public Asset(String name,double price) {
		this.name = name;
		this.price = price;
	}
	//getters
	public String getAssetName() {
		return name;
	}
	public double getAssetPrice() {
		return price;
	}
}

class Stock extends Asset{
	double openingPrice;
	double previousClosingPrice;
	
	public Stock(String name,double price,double openingPrice,double previousClosingPrice) {
		super(name,price);
		this.openingPrice = openingPrice;
		this.previousClosingPrice = previousClosingPrice;
	}
	//getters
	public double getOpeningPrice() {
		return openingPrice;
	}
	public double getPreviousClosingPrice() {
		return previousClosingPrice;
	}
	//Volatility calculation
	public double calculateVolatility() {
		return ((openingPrice - previousClosingPrice) / previousClosingPrice) * 100;
	}
}

class Bond extends Asset{
	double interestRate;
	int maturityYears;
	
	public Bond(String name,double price,double interestRate,int maturityYears) {
		super(name,price);
		this.interestRate = interestRate;
		this.maturityYears = maturityYears;
	}
	//getters
	public double getInterestRate() {
		return interestRate;
	}
	public int getMaturityYears() {
		return maturityYears;
	}
	//Amount of interest rate that has been generated calculation method
	public double calculateGeneratedInterest() {
		return this.price * (interestRate / 100);
	}
}

public class Main extends Application {
	//Start
	ArrayList<Stock> stocksList = new ArrayList<>();
	ArrayList<Bond> bondsList = new ArrayList<>();
	
	@Override
	public void start(Stage primaryStage) {
		//Start
		
		//loading from memory
		loadStocks();
		loadBonds();
		
		GridPane stockGrid = new GridPane();
		stockGrid.setHgap(10);
        stockGrid.setVgap(10);
        stockGrid.setPadding(new Insets(10));
        
        //creating labels and text fields for stock class
        Label stockNameLabel = new Label("Stock name");
        TextField stockNameField = new TextField();
        Label stockPriceLabel = new Label("Stock price");
        TextField stockPriceField = new TextField();
        Label openingPriceLabel = new Label("Opening price");
        TextField openingPriceField = new TextField();
        Label closingPriceLabel = new Label("Previous closing price");
        TextField closingPriceField = new TextField();
        
        //setting in a grid
        stockGrid.add(stockNameLabel, 0, 0);
        stockGrid.add(stockNameField, 1, 0);
        stockGrid.add(stockPriceLabel, 0, 1);
        stockGrid.add(stockPriceField, 1, 1);
        stockGrid.add(openingPriceLabel, 0, 2);
        stockGrid.add(openingPriceField, 1, 2);
        stockGrid.add(closingPriceLabel, 0, 3);
        stockGrid.add(closingPriceField, 1, 3);
        
        //putting display labels of stock
        Label highestVolLabel = new Label("Highest Volatility");
        Label highestVolNameLabel = new Label();
        Label highestVolValueLabel = new Label();
        
        Label lowestVolLabel = new Label("Lowest Volatility");
        Label lowestVolNameLabel = new Label();
        Label lowestVolValueLabel = new Label();
        
        //setting in a grid
        stockGrid.add(highestVolLabel, 0, 4);
        stockGrid.add(highestVolNameLabel, 1, 4);
        stockGrid.add(highestVolValueLabel, 2, 4);

        stockGrid.add(lowestVolLabel, 0, 5);
        stockGrid.add(lowestVolNameLabel, 1, 5);
        stockGrid.add(lowestVolValueLabel, 2, 5);

        //Buttons for stock
        Button addStockButton = new Button("Add Stock");
        Button displayStockButton = new Button("Display");
        Button saveStockButton = new Button("Save");
        Button displayStockChartButton = new Button("Display Chart");
        Button clearStockButton = new Button("Clear");
        Button displayStockPriceChartButton = new Button("Price Display Chart");
        
        addStockButton.setMinWidth(80);
        displayStockButton.setMinWidth(80);
        saveStockButton.setMinWidth(80);
        displayStockChartButton.setMinWidth(120);
        clearStockButton.setMinWidth(80);
        displayStockPriceChartButton.setMinWidth(140);
        
        //Button functionality start
        addStockButton.setOnAction(e ->{
        	try {
        		String name = stockNameField.getText();
        		double price = Double.parseDouble(stockPriceField.getText());
        		double open = Double.parseDouble(openingPriceField.getText());
        		double close = Double.parseDouble(closingPriceField.getText());
        		stocksList.add(new Stock(name,price,open,close));
        	}
        	catch(NumberFormatException ex) {
        		System.out.println("Invalid input!");
        	}
        });
        displayStockButton.setOnAction(e->{
        	if(stocksList.isEmpty()) {
        		System.out.println("Empty stock list!");
        		return;
        	}
        	else {
        		//displaying highest volatility
        		Stock highest = stocksList.get(0);
        		for(Stock s : stocksList) {
        			if(s.calculateVolatility() > highest.calculateVolatility()) {
        				highest = s;
        			}
        		}
        		highestVolNameLabel.setText(highest.getAssetName());
        		highestVolValueLabel.setText(String.format("%.2f%%", highest.calculateVolatility()));
        		
        		//displaying lowest volatility
        		Stock lowest = stocksList.get(0);
        		for(Stock s : stocksList) {
        			if(s.calculateVolatility() < lowest.calculateVolatility()) {
        				lowest = s;
        			}
        		}
        		lowestVolNameLabel.setText(lowest.getAssetName());
        		lowestVolValueLabel.setText(String.format("%.2f%%", lowest.calculateVolatility()));
        		
        	}
        });
        saveStockButton.setOnAction(e -> {
            try (PrintWriter writer = new PrintWriter(new FileWriter("stocks.txt"))) {
                for (Stock s : stocksList) {
                    writer.println(s.getAssetName() + "," + s.getAssetPrice() + "," +
                                   s.getOpeningPrice() + "," + s.getPreviousClosingPrice());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        clearStockButton.setOnAction(e -> {
            stockNameField.clear();
            stockPriceField.clear();
            openingPriceField.clear();
            closingPriceField.clear();
        });
        displayStockChartButton.setOnAction(e-> {
        	 	if (stocksList.isEmpty()) {
        	        System.out.println("No stock data available for chart.");
        	        return;
        	    }
        	    
        	    Stage chartStage = new Stage();
        	    chartStage.setTitle("Stock Volatility Chart");
        	    
        	    CategoryAxis xAxis = new CategoryAxis();
        	    xAxis.setLabel("Stock Name");
        	    NumberAxis yAxis = new NumberAxis();
        	    yAxis.setLabel("Volatility (%)");
        	    
        	    BarChart<String, Number> stockChart = new BarChart<>(xAxis, yAxis);
        	    XYChart.Series<String, Number> series = new XYChart.Series<>();
        	    series.setName("Stock Volatility");
        	    
        	    for (Stock s : stocksList) {
        	        series.getData().add(new XYChart.Data<>(s.getAssetName(), s.calculateVolatility()));
        	    }
        	    
        	    stockChart.getData().add(series);
        	    
        	    VBox vbox = new VBox(stockChart);
        	    Scene scene = new Scene(vbox, 600, 400);
        	    chartStage.setScene(scene);
        	    chartStage.show();
        });
        displayStockPriceChartButton.setOnAction(e->{
        	Stage stage = new Stage();
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Stock s : stocksList) {
                series.getData().add(new XYChart.Data<>(s.getAssetName(), s.getAssetPrice()));
            }
            barChart.getData().add(series);
            Scene scene = new Scene(barChart, 600, 400);
            stage.setScene(scene);
            stage.setTitle("Stock Price Chart");
            stage.show();
        });
        
        
        HBox stockButtons = new HBox(10, addStockButton, displayStockButton,clearStockButton, saveStockButton,displayStockChartButton,displayStockPriceChartButton);
        stockButtons.setAlignment(Pos.CENTER);
        VBox stockSection = new VBox(20, stockGrid, stockButtons);
        stockSection.setPrefWidth(400);
        
        //Bond start
        GridPane bondGrid = new GridPane();
        bondGrid.setHgap(10);
        bondGrid.setVgap(10);
        bondGrid.setPadding(new Insets(10));
        
        //creating labels and text fields for bond class
        Label bondNameLabel = new Label("Bond Name");
        TextField bondNameField = new TextField();
        Label bondPriceLabel = new Label("Bond Price");
        TextField bondPriceField = new TextField();
        Label interestRateLabel = new Label("Interest Rate");
        TextField interestRateField = new TextField();
        Label maturityYearsLabel = new Label("Maturity Years");
        TextField maturityYearsField = new TextField();
        
        //setting in a grid
        bondGrid.add(bondNameLabel, 0, 0);
        bondGrid.add(bondNameField, 1, 0);
        bondGrid.add(bondPriceLabel, 0, 1);
        bondGrid.add(bondPriceField, 1, 1);
        bondGrid.add(interestRateLabel, 0, 2);
        bondGrid.add(interestRateField, 1, 2);
        bondGrid.add(maturityYearsLabel, 0, 3);
        bondGrid.add(maturityYearsField, 1, 3);
        
        //putting display labels for bond
        Label highestInterestLabel = new Label("Highest Interest Bond");
        Label highestInterestNameLabel = new Label();
        Label highestInterestValueLabel = new Label();
        Label lowestInterestLabel = new Label("Lowest Interest Bond");
        Label lowestInterestNameLabel = new Label();
        Label lowestInterestValueLabel = new Label();
        
        bondGrid.add(highestInterestLabel, 0, 4);
        bondGrid.add(highestInterestNameLabel, 1, 4);
        bondGrid.add(highestInterestValueLabel, 2, 4);
        bondGrid.add(lowestInterestLabel, 0, 5);
        bondGrid.add(lowestInterestNameLabel, 1, 5);
        bondGrid.add(lowestInterestValueLabel, 2, 5);
        
        // Buttons for Bond
        Button addBondButton = new Button("Add Bond");
        Button displayBondButton = new Button("Display");
        Button saveBondButton = new Button("Save");
        Button displayBondChartButton = new Button("Display Bond Chart");
        Button clearBondButton = new Button("Clear");
        Button displayBondPriceChartButton = new Button("Price Display Chart");
        
        addBondButton.setMinWidth(80);
        displayBondButton.setMinWidth(80);
        saveBondButton.setMinWidth(80);
        displayBondChartButton.setMinWidth(120);
        clearBondButton.setMinWidth(80);
        displayBondPriceChartButton.setMinWidth(170);
        
        //Button functionality start
        addBondButton.setOnAction(e -> {
        	try {
        		String name = bondNameField.getText();
        		double price = Double.parseDouble(bondPriceField.getText());
        		double interest = Double.parseDouble(interestRateField.getText());
        		int years = Integer.parseInt(maturityYearsField.getText());
        		bondsList.add(new Bond(name, price, interest, years));
        	} catch (NumberFormatException ex) {
        		System.out.println("Invalid input!");
        	}
        });
        displayBondButton.setOnAction(e ->{
        	//COMPLETE DISPLAY BUTTON FOR BOND
        	if(bondsList.isEmpty()) {
        		System.out.println("Empty bond list!");
        		return;
        	}
        	else {
        		Bond highest = bondsList.get(0);
        		for(Bond b : bondsList) {
        			if(b.calculateGeneratedInterest() > highest.calculateGeneratedInterest()) {
        				highest = b;
        			}
        		}
        		highestInterestNameLabel.setText(highest.getAssetName());
        		highestInterestValueLabel.setText(String.format("%.2f%%",highest.calculateGeneratedInterest()));
        		
        		Bond lowest = bondsList.get(0);
        		for(Bond b : bondsList) {
        			if(b.calculateGeneratedInterest() < lowest.calculateGeneratedInterest()) {
        				lowest = b;
        			}
        		}
        		lowestInterestNameLabel.setText(lowest.getAssetName());
        		lowestInterestValueLabel.setText(String.format("%.2f%%", lowest.calculateGeneratedInterest()));
        	}
        });
        saveBondButton.setOnAction(e -> {
            try (PrintWriter writer = new PrintWriter(new FileWriter("bonds.txt"))) {
                for (Bond b : bondsList) {
                    writer.println(b.getAssetName() + "," + b.getAssetPrice() + "," +
                                   b.getInterestRate() + "," + b.getMaturityYears());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        clearBondButton.setOnAction(e -> {
            bondNameField.clear();
            bondPriceField.clear();
            interestRateField.clear();
            maturityYearsField.clear();
        });
        displayBondChartButton.setOnAction(e ->{
        	if (bondsList.isEmpty()) {
                System.out.println("No bond data available for chart.");
                return;
            }
            
            Stage chartStage = new Stage();
            chartStage.setTitle("Bond Interest Chart");
            
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Bond Name");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Generated Interest ($)");
            
            BarChart<String, Number> bondChart = new BarChart<>(xAxis, yAxis);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Generated Interest");
            
            for (Bond b : bondsList) {
                series.getData().add(new XYChart.Data<>(b.getAssetName(), b.calculateGeneratedInterest()));
            }
            
            bondChart.getData().add(series);
            
            VBox vbox = new VBox(bondChart);
            Scene scene = new Scene(vbox, 600, 400);
            chartStage.setScene(scene);
            chartStage.show();
        });
        displayBondPriceChartButton.setOnAction(e->{
        	Stage stage = new Stage();
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Bond b : bondsList) {
                series.getData().add(new XYChart.Data<>(b.getAssetName(), b.getAssetPrice()));
            }
            barChart.getData().add(series);
            Scene scene = new Scene(barChart, 600, 400);
            stage.setScene(scene);
            stage.setTitle("Bond Price Chart");
            stage.show();
        });

        HBox bondButtons = new HBox(10, addBondButton, displayBondButton, clearBondButton,saveBondButton, displayBondChartButton,displayBondPriceChartButton);
        bondButtons.setAlignment(Pos.CENTER);
        VBox bondSection = new VBox(20, bondGrid, bondButtons);
        bondSection.setPrefWidth(400);

        // Combine Stock and Bond Sections
        HBox mainLayout = new HBox(20, stockSection, bondSection);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        
        // Set up the scene
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Finance App");
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public void loadStocks() {
		 // Attempt to read from the "stocks.txt" file
	    try (BufferedReader reader = new BufferedReader(new FileReader("stocks.txt"))) {
	        String line;
	        
	        // Read each line of the file
	        while ((line = reader.readLine()) != null) {
	            // Split the line by commas to get the individual values
	            String[] values = line.split(",");
	            
	            // Ensure the line contains exactly 4 values
	            if (values.length == 4) {
	                String name = values[0];
	                double price = Double.parseDouble(values[1]);
	                double openingPrice = Double.parseDouble(values[2]);
	                double closingPrice = Double.parseDouble(values[3]);
	                
	                // Create a new Stock object and add it to the stocksList
	                Stock stock = new Stock(name, price, openingPrice, closingPrice);
	                stocksList.add(stock);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (NumberFormatException e) {
	        System.out.println("Error in parsing stock data. Ensure the data is correctly formatted.");
	    }
	}
	public void loadBonds() {
		// Attempt to read from the "bonds.txt" file
	    try (BufferedReader reader = new BufferedReader(new FileReader("bonds.txt"))) {
	        String line;

	        // Read each line of the file
	        while ((line = reader.readLine()) != null) {
	            // Split the line by commas to get the individual values
	            String[] values = line.split(",");

	            // Ensure the line contains exactly 4 values
	            if (values.length == 4) {
	                String name = values[0];
	                double price = Double.parseDouble(values[1]);
	                double interestRate = Double.parseDouble(values[2]);
	                int maturityYears = Integer.parseInt(values[3]);

	                // Create a new Bond object and add it to the bondsList
	                Bond bond = new Bond(name, price, interestRate, maturityYears);
	                bondsList.add(bond);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (NumberFormatException e) {
	        System.out.println("Error in parsing bond data. Ensure the data is correctly formatted.");
	    }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}