package com.wpoch.stockticker.model;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

/**
 * Represents the price on a given date.
 */
@Value
@Builder
public class DatePrice {
    Date date;
    double price;
}
