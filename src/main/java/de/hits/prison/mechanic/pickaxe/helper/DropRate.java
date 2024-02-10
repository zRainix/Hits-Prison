package de.hits.prison.mechanic.pickaxe.helper;

public class DropRate {

    double ashDropRate, shardsDropRate, expDropRate;

    public DropRate(double ashDropRate, double shardsDropRate, double expDropRate) {
        this.ashDropRate = ashDropRate;
        this.shardsDropRate = shardsDropRate;
        this.expDropRate = expDropRate;
    }

    public double getAshDropRate() {
        return ashDropRate;
    }

    public void setAshDropRate(double ashDropRate) {
        this.ashDropRate = ashDropRate;
    }

    public double getShardsDropRate() {
        return shardsDropRate;
    }

    public void setShardsDropRate(double shardsDropRate) {
        this.shardsDropRate = shardsDropRate;
    }

    public double getExpDropRate() {
        return expDropRate;
    }

    public void setExpDropRate(double expDropRate) {
        this.expDropRate = expDropRate;
    }
}
