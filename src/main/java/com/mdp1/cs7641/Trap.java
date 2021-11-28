package com.mdp1.cs7641;

public class Trap {
    public enum TrapType {
        SMALL, LARGE
    }

    private Positions location;
    private double reward;
    private TrapType type;

    public Trap(int x, int y, double reward, TrapType type) {
        this.location = new Positions(x, y);
        this.reward = reward;
        this.type = type;
    }

    public Positions getLocation() {
        return this.location;
    }

    public double getReward() {
        return this.reward;
    }

}
