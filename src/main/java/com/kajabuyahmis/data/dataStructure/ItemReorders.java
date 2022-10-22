/*
 * Open Hospital Management Information System
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data.dataStructure;

import com.kajabuyahmis.entity.Item;
import com.kajabuyahmis.entity.pharmacy.Reorder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Buddhika
 */
public class ItemReorders {
    Item item;
    List<Reorder> reorders;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Reorder> getReorders() {
        if(reorders==null){
            reorders= new ArrayList<>();
        }
        return reorders;
    }

    public void setReorders(List<Reorder> reorders) {
        this.reorders = reorders;
    }
    
    
    
}
