package carsandco.garage;

import java.util.ArrayList;
import java.util.List;

public class WorkList {
	  protected List<Item> items = new ArrayList<Item>();
	  protected double total = 0;

	  public List<Item> getItems() {
	    return items;
	  }
	  public void setAddresses(List<Item> items) {
	    this.items = items;
	  }
	  
	  public double getTotal(){
		  if (total == 0) {
			  for (Item item: items){
				  total += item.getSubtotal();
			  }
		  }
		  return total;
	  }
	  
	  public void setTotal(double total) {
		  this.total = total;
	  }

	  @Override
	  public String toString() {
	    return "Work List [items=" + items + " total=" + getTotal() + "]";
	  }

}
