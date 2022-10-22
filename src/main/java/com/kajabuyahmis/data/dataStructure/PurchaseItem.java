/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data.dataStructure;

import com.kajabuyahmis.entity.Item;
import com.kajabuyahmis.entity.pharmacy.ItemBatch;
import com.kajabuyahmis.entity.pharmacy.PharmaceuticalBillItem;

/**
 *
 * @author safrin
 */
public class PurchaseItem {

    private Item item;
    private PharmaceuticalBillItem pharmaceuticalBillItem;
    private ItemBatch itemBatch;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public PharmaceuticalBillItem getPharmaceuticalBillItem() {
        return pharmaceuticalBillItem;
    }

    public void setPharmaceuticalBillItem(PharmaceuticalBillItem pharmaceuticalBillItem) {
        this.pharmaceuticalBillItem = pharmaceuticalBillItem;
    }

    public ItemBatch getItemBatch() {
        return itemBatch;
    }

    public void setItemBatch(ItemBatch itemBatch) {
        this.itemBatch = itemBatch;
    }
}
