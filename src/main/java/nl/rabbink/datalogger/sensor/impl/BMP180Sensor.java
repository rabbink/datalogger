package nl.rabbink.datalogger.sensor.impl;

import nl.rabbink.datalogger.sensor.Sensor;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

public enum BMP180Sensor implements Sensor {

    INSTANCE;

    public static final int BMP180_I2C_ADDR = 0x77;

    public static final int CAL_AC1 = 0xAA;
    public static final int CAL_AC2 = 0xAC;
    public static final int CAL_AC3 = 0xAE;
    public static final int CAL_AC4 = 0xB0;
    public static final int CAL_AC5 = 0xB2;
    public static final int CAL_AC6 = 0xB4;
    public static final int CAL_B1 = 0xB6;
    public static final int CAL_B2 = 0xB8;
    public static final int CAL_MB = 0xBA;
    public static final int CAL_MC = 0xBC;
    public static final int CAL_MD = 0xBE;
    public static final int CONTROL = 0xF4;
    public static final int DATA_REG = 0xF6;
    public static final byte READTEMPCMD = 0x2E;
    public static final int READPRESSURECMD = 0xF4;

    private int cal_AC1 = 0;
    private int cal_AC2 = 0;
    private int cal_AC3 = 0;
    private int cal_AC4 = 0;
    private int cal_AC5 = 0;
    private int cal_AC6 = 0;
    private int cal_B1 = 0;
    private int cal_B2 = 0;
    private int cal_MB = 0;
    private int cal_MC = 0;
    private int cal_MD = 0;

    private I2CDevice bmp180Device;

    private BMP180Sensor() {
        try {
            final I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

            bmp180Device = bus.getDevice(BMP180_I2C_ADDR);
            readCalibrationData();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public synchronized double readTemperature() throws IOException {
        bmp180Device.write(CONTROL, READTEMPCMD);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        int rawTemperature = readU16(DATA_REG);
        return convertTemperature(rawTemperature);
    }

    private void readCalibrationData() throws IOException {
        cal_AC1 = readS16(CAL_AC1);
        cal_AC2 = readS16(CAL_AC2);
        cal_AC3 = readS16(CAL_AC3);
        cal_AC4 = readU16(CAL_AC4);
        cal_AC5 = readU16(CAL_AC5);
        cal_AC6 = readU16(CAL_AC6);
        cal_B1 = readS16(CAL_B1);
        cal_B2 = readS16(CAL_B2);
        cal_MB = readS16(CAL_MB);
        cal_MC = readS16(CAL_MC);
        cal_MD = readS16(CAL_MD);
    }

    private int readU16(int address) throws IOException {
        int hibyte = bmp180Device.read(address);
        return (hibyte << 8) + bmp180Device.read(address + 1);
    }

    private int readS16(int address) throws IOException {
        int hibyte = bmp180Device.read(address);
        if (hibyte > 127) {
            hibyte -= 256;
        }
        return (hibyte * 256) + bmp180Device.read(address + 1);
    }

    private double convertTemperature(int rawTemperature) {
        double temperature = 0.0;
        double x1 = ((rawTemperature - cal_AC6) * cal_AC5) / 32768;
        double x2 = (cal_MC * 2048) / (x1 + cal_MD);
        double b5 = x1 + x2;
        temperature = ((b5 + 8) / 16) / 10.0;
        return temperature;
    }

}
