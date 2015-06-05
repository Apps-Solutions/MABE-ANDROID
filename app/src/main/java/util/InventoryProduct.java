package util;

import database.models.Product;

/**
 * Created by dmolinero on 14/07/14.
 */
public class InventoryProduct extends Product{
    private int quantity;

    public InventoryProduct(int id, String jde, String name, String brand, Double price, Double cost, float tax, int stock, int stock_central,
                            String code, String description, String category,String rival) {
        super(id, jde, name, brand, price, cost, tax, stock, stock_central, code, description, category,rival);

        this.quantity = 0;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {

        return quantity;
    }

    public static InventoryProduct passToInventoryProduct(Product obj){

        int id              = obj.getId() ;
        String jde          = obj.getJde();
        String name         = obj.getName();
        String brand        = obj.getBrand();
        String code         = obj.getCode();
        String description  = obj.getDescription();
        Double price        = obj.getPrice();
        Double cost         = obj.getCost();
        String category     = obj.getCategory();
        int stock           = obj.getStock();
        int stock_central   = obj.getStock_central();
        float tax           = obj.getTax();
        String rival        = obj.getRival();

        return new InventoryProduct(id,jde,name,brand,price,cost,tax,stock,stock_central,code,description,category,rival);
    }
}
