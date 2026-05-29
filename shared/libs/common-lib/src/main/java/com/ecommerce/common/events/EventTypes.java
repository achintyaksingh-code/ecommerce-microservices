package com.ecommerce.common.events;

public final class EventTypes {
    public static final String ORDER_CREATED = "order.created";
    public static final String PAYMENT_CONFIRMED = "payment.confirmed";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String ORDER_CANCELLED = "order.cancelled";

    private EventTypes() {
    }
}
