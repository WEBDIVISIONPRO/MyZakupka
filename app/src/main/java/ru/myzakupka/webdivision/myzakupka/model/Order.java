package ru.myzakupka.webdivision.myzakupka.model;

import java.io.Serializable;
import java.util.List;

import ru.myzakupka.webdivision.myzakupka.model.Dist;
import ru.myzakupka.webdivision.myzakupka.model.Pay;

/**
 * Created by ktagintsev on 21.03.2018.
 */

public class Order implements Serializable {
    public String name;
    public List<Item> items;
    public List<Pay> pays;
    public List<Dist> dists;
    public Stat stat;

    public double getTotal(){
        double total = 0;
        for(Item item: items){
            total = total + item.price;
        }
        return total;
    }

    @Override
    public String toString() {
        return  name;
    }
}
