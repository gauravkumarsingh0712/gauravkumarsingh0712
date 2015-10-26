
package com.mirraw.android.models.PaymentOptionsDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentOptions {

    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.PayPal PayPal;
    @SerializedName("Bank Deposit")
    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.BankDeposit BankDeposit;
    @SerializedName("Credit / Debit Card")
    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.CreditDebitCard CreditDebitCard;
    @SerializedName("Net Banking")
    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.NetBanking NetBanking;
    @SerializedName("Credit Card")
    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.CreditCard CreditCard;
    @SerializedName("Cash On Delivery")
    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.CashOnDelivery CashOnDelivery;
    @SerializedName("Cash Before Delivery")
    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.CashBeforeDelivery CashBeforeDelivery;
    @SerializedName("Payu Money")
    @Expose
    private com.mirraw.android.models.PaymentOptionsDetail.PayuMoney PayuMoney;

    /**
     * @return The PayPal
     */
    public com.mirraw.android.models.PaymentOptionsDetail.PayPal getPayPal() {
        return PayPal;
    }

    /**
     * @param PayPal The PayPal
     */
    public void setPayPal(com.mirraw.android.models.PaymentOptionsDetail.PayPal PayPal) {
        this.PayPal = PayPal;
    }

    /**
     * @return The BankDeposit
     */
    public com.mirraw.android.models.PaymentOptionsDetail.BankDeposit getBankDeposit() {
        return BankDeposit;
    }

    /**
     * @param BankDeposit The Bank Deposit
     */
    public void setBankDeposit(com.mirraw.android.models.PaymentOptionsDetail.BankDeposit BankDeposit) {
        this.BankDeposit = BankDeposit;
    }

    /**
     * @return The CreditDebitCard
     */
    public com.mirraw.android.models.PaymentOptionsDetail.CreditDebitCard getCreditDebitCard() {
        return CreditDebitCard;
    }

    /**
     * @param CreditDebitCard The Credit / Debit Card
     */
    public void setCreditDebitCard(com.mirraw.android.models.PaymentOptionsDetail.CreditDebitCard CreditDebitCard) {
        this.CreditDebitCard = CreditDebitCard;
    }

    /**
     * @return The NetBanking
     */
    public com.mirraw.android.models.PaymentOptionsDetail.NetBanking getNetBanking() {
        return NetBanking;
    }

    /**
     * @param NetBanking The Net Banking
     */
    public void setNetBanking(com.mirraw.android.models.PaymentOptionsDetail.NetBanking NetBanking) {
        this.NetBanking = NetBanking;
    }

    /**
     * @return The PayuMoney
     */
    public com.mirraw.android.models.PaymentOptionsDetail.PayuMoney getPayuMoney() {
        return PayuMoney;
    }

    /**
     * @param PayuMoney The Payu Money
     */
    public void setPayuMoney(com.mirraw.android.models.PaymentOptionsDetail.PayuMoney PayuMoney) {
        this.PayuMoney = PayuMoney;
    }


    /**
     * @return The CreditCard
     */
    public com.mirraw.android.models.PaymentOptionsDetail.CreditCard getCreditCard() {
        return CreditCard;
    }

    /**
     * @param CreditCard The Credit Card
     */
    public void setCreditCard(com.mirraw.android.models.PaymentOptionsDetail.CreditCard CreditCard) {
        this.CreditCard = CreditCard;
    }

    /**
     * @return The CashOnDelivery
     */
    public com.mirraw.android.models.PaymentOptionsDetail.CashOnDelivery getCashOnDelivery() {
        return CashOnDelivery;
    }

    /**
     * @param CashOnDelivery The Cash On Delivery
     */
    public void setCashOnDelivery(com.mirraw.android.models.PaymentOptionsDetail.CashOnDelivery CashOnDelivery) {
        this.CashOnDelivery = CashOnDelivery;
    }

    /**
     * @return The CashBeforeDelivery
     */
    public com.mirraw.android.models.PaymentOptionsDetail.CashBeforeDelivery getCashBeforeDelivery() {
        return CashBeforeDelivery;
    }

    /**
     * @param CashBeforeDelivery The Cash Before Delivery
     */
    public void setCashBeforeDelivery(com.mirraw.android.models.PaymentOptionsDetail.CashBeforeDelivery CashBeforeDelivery) {
        this.CashBeforeDelivery = CashBeforeDelivery;
    }

}
