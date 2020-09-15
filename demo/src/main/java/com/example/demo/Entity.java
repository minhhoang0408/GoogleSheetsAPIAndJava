package com.example.demo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Entity {
    private int id;
    private String name;
    private Integer loan;
    private int loanPackage;

    public Entity(int id, String name, Integer loan, int loanPackage){
        this.id = id;
        this.loan = loan;
        this.name = name;
        this.loanPackage = loanPackage;

    }
}
