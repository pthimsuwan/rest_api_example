package com.test.Sample.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class Rate {

    @Getter
    @Setter
    private String days;

    @Getter
    @Setter
    private String times;

    @Getter
    @Setter
    private String tz;

    @Getter
    @Setter
    private BigDecimal price;
}
