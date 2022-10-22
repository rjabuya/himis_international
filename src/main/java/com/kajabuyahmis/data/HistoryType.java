
/*
 * Dr M H B Ariyaratne
 * buddhika.ari@gmail.com
 */
package com.kajabuyahmis.data;

/**
 *
 * @author Buddhika
 */
public enum HistoryType {
    Sale,
    Issue,
    Order,
    GoodReceive,
    Stock,
    ChannelBooking,
    ChannelBalanceReset,
    @Deprecated
    ChannelBookingCancel,
    ChannelDeposit,
    ChannelDepositCancel,
    ChannelCreditNote,
    ChannelCreditNoteCancel,
    ChannelDebitNote,
    ChannelDebitNoteCancel,
    AgentBalanceUpdateBill,
    CollectingCentreBalanceUpdateBill,
    CollectingCentreDeposit,
    CollectingCentreDepositCancel,
    CollectingCentreCreditNote,
    CollectingCentreCreditNoteCancel,
    CollectingCentreDebitNote,
    CollectingCentreDebitNoteCancel,
    CollectingCentreBilling,
    MonthlyRecord,   
}
