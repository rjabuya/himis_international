select id,name from Department where name ="Ruhunu Pharmacy";

update stock
set `STOCK` = 0
where `DEPARTMENT_ID` = 51570;

-- select item.`NAME`, itembatch.`BATCHNO` ,itembatch.`ITEM_ID`, stock.`STOCK` from stock 
-- join itembatch on stock.`ITEMBATCH_ID` = itembatch.`ID` 
-- join item on itembatch.`ITEM_ID` = item.`ID`
-- where `STOCK`=420;