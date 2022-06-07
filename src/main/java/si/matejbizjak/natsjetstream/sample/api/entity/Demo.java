package si.matejbizjak.natsjetstream.sample.api.entity;

import java.time.LocalDateTime;
import java.util.Optional;

public class Demo {

    private String name;
    private Double doubleNumber;
    private LocalDateTime dateTime;
    private int intNumber;
    private InnerDemo innerDemo;

    public Demo() {
    }

    public Demo(String name, Double doubleNumber, LocalDateTime dateTime, int intNumber, InnerDemo innerDemo) {
        this.name = name;
        this.doubleNumber = doubleNumber;
        this.dateTime = dateTime;
        this.intNumber = intNumber;
        this.innerDemo = innerDemo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDoubleNumber() {
        return doubleNumber;
    }

    public void setDoubleNumber(Double doubleNumber) {
        this.doubleNumber = doubleNumber;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getIntNumber() {
        return intNumber;
    }

    public void setIntNumber(int intNumber) {
        this.intNumber = intNumber;
    }

    public InnerDemo getInnerDemo() {
        return innerDemo;
    }

    public void setInnerDemo(InnerDemo innerDemo) {
        this.innerDemo = innerDemo;
    }

    public static class InnerDemo {
        private String name2;
        private float floatNumber;

        public InnerDemo() {
        }

        public InnerDemo(String name2, float floatNumber) {
            this.name2 = name2;
            this.floatNumber = floatNumber;
        }

        public String getName2() {
            return name2;
        }

        public void setName2(String name2) {
            this.name2 = name2;
        }

        public float getFloatNumber() {
            return floatNumber;
        }

        public void setFloatNumber(float floatNumber) {
            this.floatNumber = floatNumber;
        }
    }
}
