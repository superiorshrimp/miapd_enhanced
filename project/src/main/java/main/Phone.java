package main;

import java.util.ArrayList;
import java.util.List;

public class Phone{
    private final String name;
    private final int price; //pln (xkom no discount)
    private final int features; //dual sim, ssd slot, 5g, stereo speakers... etc
    private final int ram; //GBs
    private final int cpu; //benchmark antutu
    private final int storage; //GBs
    private final int battery; //mah or sot - TODO: decide
    private final int charging; //Ws
    private final int software; //TODO: decide
    private static final int n = 7;
    private final ArrayList<Integer> attributes = new ArrayList<>();
    private final static ArrayList<String> labels = new ArrayList<>(List.of("price", "features", "ram", "cpu", "storage", "battery", "charging"));

    public Phone(String name, int price, int features, int ram, int cpu, int storage, int battery, int charging, int software){
        this.name = name;
        this.price = price;
        this.features = features;
        this.ram = ram;
        this.cpu = cpu;
        this.storage = storage;
        this.battery = battery;
        this.charging = charging;
        this.software = software;

        this.attributes.addAll(List.of(this.price, this.features, this.ram, this.cpu, this.storage, this.battery, this.charging));
    }

    public String getName(){return this.name;}
    public static int getNumberOfAttributes(){return n;}
    public ArrayList<Integer> getAttributes(){return this.attributes;}
    public static ArrayList<String> getLabels(){return labels;}

    public String toString(){
        return "Price" + this.price + "\n" +
                "Features" + this.features + "\n" +
                "RAM" + this.ram + "\n" +
                "CPU" + this.cpu + "\n" +
                "Storage" + this.storage + "\n" +
                "Battery" + this.battery + "\n" +
                "Charging" + this.charging + "\n" +
                "Software" + this.software + "\n";
    }
}
