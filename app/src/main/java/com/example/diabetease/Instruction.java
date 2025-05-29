package com.example.diabetease;

public class Instruction {
    private int stepNumber;
    private String text;

    public Instruction(int stepNumber, String text) {
        this.stepNumber = stepNumber;
        this.text = text;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getText() {
        return text;
    }
}

