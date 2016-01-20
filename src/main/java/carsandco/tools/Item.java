package carsandco.tools;

public class Item {
	protected String name;
	  protected double price;
	  protected double amount;
	  protected double subtotal = 0;
	  
	  public String getName() {
		  return name;
	  }
	  
	  public double getPrice() {
		  return price;
	  }
	  
	  public double getAmount() {
		  return amount;
	  }
	  
	  public double getSubtotal() {
		  if (subtotal == 0) {
			  subtotal = price * amount;
		  }
		  return subtotal;
	  }
	  
	  public void setName(String name) {
		  this.name = name;
	  }
	  
	  public void setPrice(double price) {
		  this.price = price;
	  }
	  
	  public void setAmount(double amount) {
		  this.amount = amount;
	  }
	  
	  public void setSubtotal(double subtotal) {
		  this.subtotal = subtotal;
	  }

	  @Override
	  public String toString() {
	    return "\n    Name: " + name + ""
	    		+ "\n    Price: " + price + "€"
	    		+ "\n    Amount: " + amount
	    		+ "\n    Subtotal: " + getSubtotal() + "€"
	    		+ "\n\n    ------\n";
	  }

}
