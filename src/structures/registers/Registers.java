package structures.registers;

import java.util.Arrays;

public class Registers {

    private final static String[] nameRegisters = 
    { "$zero", "$at",
      "$v0", "$v1",
      "$a0", "$a1", "$a2", "$a3", "$t0",
      "$t1","$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
      "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",
      "$t8", "$t9",
      "$k0", "$k1",
      "$gp", "$sp", "$fp", "$ra" };
    private int[] registers = new int[32];
    private int hi = 0x00000000;
    private int lo = 0x00000000;
    
    public Registers(){
        Arrays.fill(registers, 0x00000000);
        registers[29] = 0x7fffeffc;
    }

    public void setRegister(int index, int value){
        if(index != 0){
            registers[index] = value;
        }
    }

    public int getRegister(int index){
        return registers[index];
    }

    public int getHi(){
        return hi;
    }

    public void setHi(int value){
        hi = value;
    }

    public int getLo(){
        return lo;
    }

    public void setLo(int value){
        lo = value;
    }

    public String[] getAllNameRegisters(){
        return nameRegisters;
    }
}
