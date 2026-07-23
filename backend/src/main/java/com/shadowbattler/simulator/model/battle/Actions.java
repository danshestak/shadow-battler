package com.shadowbattler.simulator.model.battle;

public final class Actions {
    private Actions() {}

    // bit 0-3: the specific action id
    // bit 4: flag for a switch
    // bit 5: flag for charged attack

    public static final byte NONE = 0;
    public static final byte FAST_ATTACK = 1;
    public static final byte STUN = 2;

    public static final byte CHARGED_ATTACK0 = 32; // 33
    public static final byte CHARGED_ATTACK1 = 32 | 1; // 34

    public static final byte SWITCH0 = 16; // 17
    public static final byte SWITCH1 = 16 | 1; // 18
    public static final byte SWITCH2 = 16 | 2; // 19

    public static boolean isChargedAttack(byte action) {
        return (action & 32) != 0;
    }

    public static boolean isSwitch(byte action) {
        return (action & 16) != 0;
    }

    public static int getId(byte action) {
        return action & 15;
    }

    public static byte getSwitch(int n) {
        return (byte)(16 | n);
    }

    public static byte getCharged(int n) {
        return (byte)(32 | n);
    }
}
