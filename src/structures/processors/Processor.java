package structures.processors;
import types.*;
import structures.registers.*;
import structures.auxiliaries.*;
import structures.cachememory.*;
import structures.inputoutput.*;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class Processor implements Functions{
    private CacheControll l1;
    private Registers registers;
    private RegistersCO1 coregisters;
    private Coprocessor coprocessor;
    private Syscall syscall;
    
    private int programCounter = 0x00400000;
    private LinkedList<Integer> delaySlot = new LinkedList<Integer>();
    private String[][] functions = Auxiliary.getOpTable();
    private String[][] functionsRtype = Auxiliary.getRTable();
    private String[][] functionFLTP = Auxiliary.getFLTPTable();
    private String[][] functionRegimm= Auxiliary.getRegimmTable();
    private boolean exit = false;

    public Processor(CacheControll l1) {
        this.l1 = l1;
        this.registers = new Registers();
        this.coregisters = new RegistersCO1();
        this.coprocessor = new Coprocessor(coregisters);
        this.syscall = new Syscall(l1, registers, coregisters);
    }

    public void start() {
        while (exit == false) {
            runInstruction();
        }
    }

    public void runInstruction(){
        byte[] word = l1.load(programCounter);
        programCounter += 4;
        int delaySlotsize = delaySlot.size();
        int x = (int) ((word[0] & 0b11100000) >>> 5 );
        int y = (int) ((word[0] & 0b00011100) >>> 2 );       
        if(functions[x][y].equals("rTypeFunctions")){
            int xRtype = (int) ((word[3] & 0b00111000) >>> 3);
            int yRtype = (int) (word[3] & 0b00000111);
            runFunction(functionsRtype[xRtype][yRtype], word);
        }else if(functions[x][y].equals("regimmFunctions")){
            int xRegimm = (int) ((word[1] & 0b00011000) >> 3);
            int yRegimm = (int) (word[1] & 0b00000111);
            runFunction(functionRegimm[xRegimm][yRegimm], word);
        }else if(functions[x][y].equals("FLTP")){
            int xFLTP = (int) (word[0] & 0b00000011);
            int yFLTP = (int) ((word[1] & 0b11100000) >>> 5);
            runFunction(functionFLTP[xFLTP][yFLTP], word);
        }else{
            runFunction(functions[x][y], word);
        }
        if(delaySlot.size() == delaySlotsize && delaySlot.size() > 0){
            programCounter = delaySlot.getFirst();
            delaySlot.removeFirst();
        }
    }

    public void runFunction(String func, byte[] code){
        Method testMethod;
        try {
            testMethod = Processor.class.getMethod(func, byte[].class);
            testMethod.invoke(this, code);
        } catch (Exception e) {
            MainType command = new MainType(code);
            System.out.println("Process.");
            System.out.println("Invalid for Reflection API.");
            System.out.println("Line 0x" + String.format("%x", programCounter - 4));
            System.out.println("Code: " + command.getCode());
        }
    }

    //R-types
    //==========================================================================
    
    public void add(byte[] code){
        Rtype add = new Rtype(code);
        int arg1 = registers.getRegister(add.getRS());
        int arg2 = registers.getRegister(add.getRT());
        registers.setRegister(add.getRD(), (arg1 + arg2));
    }

    public void addu(byte[] code){
        add(code);
    }

    public void sub(byte[] code){
        Rtype sub = new Rtype(code);
        int arg1 = registers.getRegister(sub.getRS());
        int arg2 = registers.getRegister(sub.getRT());
        registers.setRegister(sub.getRD(), (arg1 - arg2));
    }

    public void subu(byte[] code){
        sub(code);
    }

    public void and(byte[] code){
        Rtype and = new Rtype(code);
        int arg1 = registers.getRegister(and.getRS());
        int arg2 = registers.getRegister(and.getRT());
        byte[] byte1 = ByteBuffer.allocate(4).putInt(arg1).array();
        byte[] byte2 = ByteBuffer.allocate(4).putInt(arg2).array();
        byte[] newValue = new byte[4];
        for(int i = 0; i < 4; i++){
            newValue[i] = (byte) (byte1[i] & byte2[i]);
        }
        registers.setRegister(and.getRD(), ByteBuffer.wrap(newValue).getInt());
    }

    public void or(byte[] code){
        Rtype or = new Rtype(code);
        int arg1 = registers.getRegister(or.getRS());
        int arg2 = registers.getRegister(or.getRT());
        byte[] byte1 = ByteBuffer.allocate(4).putInt(arg1).array();
        byte[] byte2 = ByteBuffer.allocate(4).putInt(arg2).array();
        byte[] newValue = new byte[4];
        for(int i = 0; i < 4; i++){
            newValue[i] = (byte) (byte1[i] | byte2[i]);
        }
        registers.setRegister(or.getRD(), ByteBuffer.wrap(newValue).getInt());
    }

    public void xor(byte[] code){
        Rtype xor = new Rtype(code);
        int arg1 = registers.getRegister(xor.getRS());
        int arg2 = registers.getRegister(xor.getRT());
        byte[] byte1 = ByteBuffer.allocate(4).putInt(arg1).array();
        byte[] byte2 = ByteBuffer.allocate(4).putInt(arg2).array();
        byte[] newValue = new byte[4];
        for(int i = 0; i < 4; i++){
            newValue[i] = (byte) (byte1[i] ^ byte2[i]);
        }
        registers.setRegister(xor.getRD(), ByteBuffer.wrap(newValue).getInt());
    }

    public void mult(byte[] code){
        Rtype mult = new Rtype(code);
        int arg1 = registers.getRegister(mult.getRS());
        int arg2 = registers.getRegister(mult.getRT());
        long finalValue = (long) arg1 * arg2;
        String stringFinal = Long.toBinaryString(finalValue);
        while(stringFinal.length() < 64){
            stringFinal = "0" + stringFinal;
        }
        registers.setHi(Auxiliary.binaryStringToInt(stringFinal.substring(0, 32)));
        registers.setLo(Auxiliary.binaryStringToInt(stringFinal.substring(32, 64)));
    }

    public void div(byte[] code){
        Rtype div = new Rtype(code);
        int arg1 = registers.getRegister(div.getRS());
        int arg2 = registers.getRegister(div.getRT());
        registers.setHi((int) (arg1 % arg2));
        registers.setLo((int) (arg1 / arg2));
    }

    public void mflo(byte[] code){
        Rtype mflo = new Rtype(code);
        registers.setRegister(mflo.getRD(), registers.getLo());
    }

    public void mfhi(byte[] code){
        Rtype mfhi = new Rtype(code);
        registers.setRegister(mfhi.getRD(), registers.getHi());
    }

    public void slt(byte[] code){
        Rtype slt = new Rtype(code);
        int arg1 = registers.getRegister(slt.getRS());
        int arg2 = registers.getRegister(slt.getRT());
        if(arg1 < arg2){
            registers.setRegister(slt.getRD(),1);
        }else{
            registers.setRegister(slt.getRD(),0);
        }
    }

    public void sltu(byte[] code){
        slt(code);
    }

    public void sll(byte[] code){
        Rtype sll = new Rtype(code);
        int value = registers.getRegister(sll.getRT());
        int shamt = sll.getShamt();
        int newValue = value << shamt;
        registers.setRegister(sll.getRD(), newValue);
    }

    public void srl(byte[] code){
        Rtype srl = new Rtype(code);
        int value = registers.getRegister(srl.getRT());
        int shamt = srl.getShamt();
        int newValue = value >>> shamt;
        registers.setRegister(srl.getRD(), newValue);
    }

    public void sra(byte[] code){
        Rtype sra = new Rtype(code);
        int value = registers.getRegister(sra.getRT());
        int shamt = sra.getShamt();
        int newValue = value >> shamt;
        registers.setRegister(sra.getRD(), newValue);
    }

    public void jr(byte[] code){
        Rtype jr = new Rtype(code);  
        delaySlot.add(registers.getRegister(jr.getRS()));
    }

    public void jalr(byte[] code){
        Rtype jalr = new Rtype(code);
        int newProgramCounter = registers.getRegister(jalr.getRS()); 
        registers.setRegister(jalr.getRD(), (programCounter + 4));      
        delaySlot.add(newProgramCounter);
    }

    public void syscall(byte[] code){
        if (registers.getRegister(2) == 10) {
            System.out.println("\n -- program is finished running --");
            this.exit = true;
        }else {
            syscall.run();
        }
    }

    //I-types
    //==========================================================================

    public void addi(byte[] code){
        Itype addImed = new Itype(code);
        int arg = registers.getRegister(addImed.getRS());
        int immediate = ByteBuffer.wrap(addImed.getAddressImmediate(true)).getInt();
        registers.setRegister(addImed.getRT(), (arg + immediate));
    }

    public void lui(byte[] code){
        Itype lui = new Itype(code);
        byte[] value = lui.getAddressImmediate(false);
        value[0] = value[2];
        value[1] = value[3];
        value[2] = 0;
        value[3] = 0;
        int valueInt = ByteBuffer.wrap(value).getInt();
        registers.setRegister(lui.getRT(), valueInt);
    }

    public void ori(byte[] code){
        Itype ori = new Itype(code);
        int valueInt = registers.getRegister(ori.getRS());
        byte [] valueByte = ByteBuffer.allocate(4).putInt(valueInt).array();
        byte[] valueImed = ori.getAddressImmediate(false);
        byte[] valueByteFinal = new byte[4];
        valueByteFinal[0] = valueByte[0];
        valueByteFinal[1] = valueByte[1];
        valueByteFinal[2] = valueImed[2];
        valueByteFinal[3] = valueImed[3];
        int valueIntFinal = ByteBuffer.wrap(valueByteFinal).getInt();
        registers.setRegister(ori.getRT(), valueIntFinal);
    } 
    
    public void andi(byte[] code){
        Itype andi = new Itype(code);
        int valueInt = registers.getRegister(andi.getRS());
        byte [] valueByte = ByteBuffer.allocate(4).putInt(valueInt).array();
        byte[] valueImed = andi.getAddressImmediate(false);
        byte[] valueByteFinal = new byte[4];
        for(int index = 0; index < 4; index++){
            valueByteFinal[index] = (byte) (valueByte[index] & valueImed[index]);
        }
        int valueIntFinal = ByteBuffer.wrap(valueByteFinal).getInt();
        registers.setRegister(andi.getRT(), valueIntFinal);
    }
    
    public void addiu(byte[] code){
        addi(code);
    }

    public void slti(byte[] code){
        Itype slti = new Itype(code);
        int arg1 = registers.getRegister(slti.getRS());
        int arg2 = ByteBuffer.wrap(slti.getAddressImmediate(true)).getInt();
        if(arg1 < arg2){
            registers.setRegister(slti.getRT(),1);
        }else{
            registers.setRegister(slti.getRT(),0);
        }
    }

    public void beq(byte[] code){
        Itype beq= new Itype(code);
        int arg1 = registers.getRegister(beq.getRS());
        int arg2 = registers.getRegister(beq.getRT());
        int offset = ByteBuffer.wrap(beq.getAddressImmediate(true)).getInt();
        if(arg1 == arg2){
            delaySlot.add(programCounter + (offset * 4));
        }
    }

    public void bne(byte[] code){
        Itype bne= new Itype(code);
        int arg1 = registers.getRegister(bne.getRS());
        int arg2 = registers.getRegister(bne.getRT());
        int offset = ByteBuffer.wrap(bne.getAddressImmediate(true)).getInt();
        if(arg1 != arg2){
            delaySlot.add(programCounter + (offset * 4));
        }
    }

    public void blez(byte[] code){
        Itype blez= new Itype(code);
        int arg1 = registers.getRegister(blez.getRS());
        int offset = ByteBuffer.wrap(blez.getAddressImmediate(true)).getInt();
        if(arg1 <= 0){
            delaySlot.add(programCounter + (offset * 4));
        }
    }

    public void lw(byte[] code){
        Itype lw = new Itype(code);
        int address = registers.getRegister(lw.getRS());
        int offset = ByteBuffer.wrap(lw.getAddressImmediate(true)).getInt();
        byte[] value = l1.load(address + offset);
        registers.setRegister(lw.getRT(), ByteBuffer.wrap(value).getInt());
    }

    public void sw(byte[] code){
        Itype sw = new Itype(code);
        int value = registers.getRegister(sw.getRT());
        int address = registers.getRegister(sw.getRS());
        int offset = ByteBuffer.wrap(sw.getAddressImmediate(true)).getInt();
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        l1.store((address + offset), bytes);
    }

    public void lb(byte[] code){
        Itype lb = new Itype(code);
        int address = registers.getRegister(lb.getRS());
        int offset = ByteBuffer.wrap(lb.getAddressImmediate(true)).getInt();
        byte[] value = l1.load((address + offset));
        int posValue = (address + offset) % 4;
        byte[] newValue = new byte[4];
        newValue[0] = 0;
        newValue[1] = 0;
        newValue[2] = 0;
        newValue[3] = value[posValue];
        registers.setRegister(lb.getRT(), ByteBuffer.wrap(newValue).getInt());
    }

    //J-types
    //==========================================================================

    public void jump(byte[] code){
        Jtype jump= new Jtype(code);
        int address = Integer.parseInt(jump.getTargetAddress(), 2);
        delaySlot.add(address * 4);
    }

    public void jal(byte[] code){
        Jtype jal = new Jtype(code);
        int address = Integer.parseInt(jal.getTargetAddress(), 2);
        registers.setRegister(31, (programCounter + 4));
        delaySlot.add(address * 4);
    }

    //Coprocessor/Processor Operations - FLTP
    //==========================================================================

    public void mfc1(byte[] code){
        Rtype mfc1 = new Rtype(code);
        int value = coregisters.getSRegister(mfc1.getRD());
        registers.setRegister(mfc1.getRT(), value);
    }

    public void mtc1(byte[] code){
        Rtype mtc1 = new Rtype(code);
        int value = registers.getRegister(mtc1.getRT());
        coregisters.setSRegister(mtc1.getRD(), value);
    }

    public void s_definition(byte[] code){
        coprocessor.runInstruction(code, "_s");
    }

    public void d_definition(byte[] code){
        coprocessor.runInstruction(code, "_d");
    }

    public void w_definition(byte[] code){
        coprocessor.runInstruction(code, "_w");
    }

    public void lwc1(byte[] code){
        Itype lwc1 = new Itype(code);
        int address = registers.getRegister(lwc1.getRS());
        int offset = ByteBuffer.wrap(lwc1.getAddressImmediate(true)).getInt();
        byte[] value = l1.load(address + offset);
        coregisters.setSRegister(lwc1.getRT(), ByteBuffer.wrap(value).getInt());
    }

    public void swc1(byte[] code){
        Itype sw = new Itype(code);
        int value = coregisters.getSRegister(sw.getRT());
        int address = registers.getRegister(sw.getRS());
        int offset = ByteBuffer.wrap(sw.getAddressImmediate(true)).getInt();
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        l1.store((address + offset), bytes);
    }

    public void ldc1(byte[] code){
        Itype ldc1 = new Itype(code);
        int address =  registers.getRegister(ldc1.getRS());
        int offset = ByteBuffer.wrap(ldc1.getAddressImmediate(true)).getInt();
        byte[] even = l1.load(address + offset);
        byte[] odd = l1.load(address + offset + 4);
        int valueEven = ByteBuffer.wrap(even).getInt();
        int valueOdd = ByteBuffer.wrap(odd).getInt();
        coregisters.setDRegister(ldc1.getRT(), valueEven, valueOdd);
    }

    //REGIMM Operations
    //==========================================================================

    public void bgez(byte[] code){
        Itype bgez= new Itype(code);
        int arg1 = registers.getRegister(bgez.getRS());
        int offset = ByteBuffer.wrap(bgez.getAddressImmediate(true)).getInt();
        if(arg1 >= 0){
            delaySlot.add(programCounter + (offset * 4));
        }
    }
}