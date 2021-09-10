package structures.decoders;
import types.*;
import structures.memory.Memory;
import structures.registers.*;
import structures.auxiliaries.*;
import java.lang.reflect.*;

public class Decoder implements Functions{

    private Memory memory;
    private Registers registers;
    private RegistersCO1 coregisters;
    private String[] nameRegisters;
    private String[] nameCoregisters;
    private Codecoder codecoder;

    private int programCounter = 0x00400000;
    private String[][] functions = Auxiliary.getOpTable();
    private String[][] functionsRtype = Auxiliary.getRTable();
    private String[][] functionFLTP = Auxiliary.getFLTPTable();
    private String[][] functionRegimm= Auxiliary.getRegimmTable();

    public Decoder(Memory memory) {
        this.memory = memory;
        this.registers = new Registers();
        this.coregisters = new RegistersCO1();
        this.nameRegisters = registers.getAllNameRegisters();
        this.nameCoregisters = coregisters.getAllNameRegisters();
        this.codecoder = new Codecoder(nameCoregisters);
    }

    public void start() {
        for(int count = 0; count < memory.getNumberOfInstruction(); count++){  
            byte[] word = memory.loadWordByPosition(programCounter);
            codeDescription(word);        
            int x = (int) ((word[0] & 0b11100000) >> 5 );
            int y = (int) ((word[0] & 0b00011100) >> 2 );
            if(functions[x][y].equals("rTypeFunctions")){
                int xRtype = (int) ((word[3] & 0b00111000) >>> 3);
                int yRtype = (int) (word[3] & 0b00000111);
                if(word[0] == 0 && word[1] == 0 && word[2] == 0 && word[3] == 0){
                    System.out.println("NOP");
                }else{
                    runFunction(functionsRtype[xRtype][yRtype], word);
                }
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
            programCounter += 4;   
        }
    }

    public void runFunction(String func, byte[] code){
        Method testMethod;
        try {
            testMethod = Decoder.class.getMethod(func, byte[].class);
            testMethod.invoke(this, code);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void codeDescription(byte[] code){
        System.out.print(String.format("%08x", programCounter));
        System.out.print("     ");
        MainType codeString = new MainType(code);
        System.out.print(String.format("%08x",
                             Auxiliary.binaryStringToInt(codeString.getCode())));
        System.out.print("     ");
    }

    //R-types
    //==========================================================================

    public void add(byte[] code) {
        Rtype add = new Rtype(code);
        String arg1 = nameRegisters[add.getRS()];
        String arg2 = nameRegisters[add.getRT()];
        String finalRegister = nameRegisters[add.getRD()];
        System.out.println("ADD " + finalRegister + ", " + arg1 + ", " + arg2);
    }

    public void addu(byte[] code) {
        Rtype addu = new Rtype(code);
        String arg1 = nameRegisters[addu.getRS()];
        String arg2 = nameRegisters[addu.getRT()];
        String finalRegister = nameRegisters[addu.getRD()];
        System.out.println("ADDU " + finalRegister + ", " + arg1 + ", " + arg2);
    }

    public void sub(byte[] code) {
        Rtype sub = new Rtype(code);
        String arg1 = nameRegisters[sub.getRS()];
        String arg2 = nameRegisters[sub.getRT()];
        String finalRegister = nameRegisters[sub.getRD()];
        System.out.println("SUB " + finalRegister + ", " + arg1 + ", " + arg2);
        
    }

    public void subu(byte[] code) {
        Rtype subu = new Rtype(code);
        String arg1 = nameRegisters[subu.getRS()];
        String arg2 = nameRegisters[subu.getRT()];
        String finalRegister = nameRegisters[subu.getRD()];
        System.out.println("SUBU " + finalRegister + ", " + arg1 + ", " + arg2);
    }

    public void syscall(byte[] code) {
        System.out.println("SYSCALL");       
    }

    public void jr(byte[] code) {
        Rtype jr = new Rtype(code);
        System.out.println("JR " + nameRegisters[jr.getRS()]);        
    }

    public void srl(byte[] code) {
        Rtype srl = new Rtype(code);
        String valueString = nameRegisters[srl.getRT()];
        int shamt = srl.getShamt();
        String finalRegister = nameRegisters[srl.getRD()];
        System.out.println("SRL " + finalRegister + ", " + valueString +
                                                                     ", " + shamt);
    }

    public void sra(byte[] code) {
        Rtype sra = new Rtype(code);
        String valueString = nameRegisters[sra.getRT()];
        int shamt = sra.getShamt();
        String finalRegister = nameRegisters[sra.getRD()];
        System.out.println("SRL " + finalRegister + ", " + valueString +
                                                                     ", " + shamt);
    }

    public void sll(byte[] code) {
        Rtype sll = new Rtype(code);
        String valueString = nameRegisters[sll.getRT()];
        int shamt = sll.getShamt();
        String finalRegister = nameRegisters[sll.getRD()];
        System.out.println("SLL " + finalRegister + ", " + valueString +   
                                                                     ", " + shamt);
    }

    public void slt(byte[] code) {
        Rtype slt = new Rtype(code);
        String arg1 = nameRegisters[slt.getRS()];
        String arg2 = nameRegisters[slt.getRT()];
        String finalRegister = nameRegisters[slt.getRD()];
        System.out.println("SLT " + finalRegister + ", " + arg1 + ", " + arg2);
    }

    public void and(byte[] code){
        Rtype and = new Rtype(code);
        String arg1 = nameRegisters[and.getRS()];
        String arg2 = nameRegisters[and.getRT()];
        String finalRegister = nameRegisters[and.getRD()];
        System.out.println("AND " + finalRegister + ", " + arg1 + ", " + arg2);
        
    }

    public void or(byte[] code){
        Rtype or = new Rtype(code);
        String arg1 = nameRegisters[or.getRS()];
        String arg2 = nameRegisters[or.getRT()];
        String finalRegister = nameRegisters[or.getRD()];
        System.out.println("OR " + finalRegister + ", " + arg1 + ", " + arg2);
    }

    public void xor(byte[] code){
        Rtype xor = new Rtype(code);
        String arg1 = nameRegisters[xor.getRS()];
        String arg2 = nameRegisters[xor.getRT()];
        String finalRegister = nameRegisters[xor.getRD()];
        System.out.println("XOR " + finalRegister + ", " + arg1 + ", " + arg2);
    }

    public void mult(byte[] code){
        Rtype mult = new Rtype(code);
        String arg1 = nameRegisters[mult.getRS()];
        String arg2 = nameRegisters[mult.getRT()];
        System.out.println("MULT " + arg1 + ", " + arg2);
    }

    public void div(byte[] code){
        Rtype div = new Rtype(code);
        String arg1 = nameRegisters[div.getRS()];
        String arg2 = nameRegisters[div.getRT()];
        System.out.println("DIV " + arg1 + ", " + arg2);
    }

    public void mflo(byte[] code){
        Rtype mflo = new Rtype(code);
        System.out.println("MFLO " + nameRegisters[mflo.getRD()]);
    }

    public void mfhi(byte[] code){
        Rtype mfhi = new Rtype(code);
        System.out.println("MFHI " + nameRegisters[mfhi.getRD()]);
    }

    public void sltu(byte[] code){
        Rtype sltu = new Rtype(code);
        String arg1 = nameRegisters[sltu.getRS()];
        String arg2 = nameRegisters[sltu.getRT()];
        String finalRegister = nameRegisters[sltu.getRD()];
        System.out.println("SLTU " + finalRegister + ", " + arg1 + ", " + arg2);
    }

    public void jalr(byte[] code){
        Rtype jalr = new Rtype(code);
        String arg1 = nameRegisters[jalr.getRS()];
        String finalRegister = nameRegisters[jalr.getRD()];
        System.out.println("JALR " + finalRegister + ", " + arg1);

    }

    //I-types
    //==========================================================================

    public void addi(byte[] code) {
        Itype addi = new Itype(code);
        String arg1 = nameRegisters[addi.getRS()];
        int immediate = Auxiliary.binaryStringToInt(addi.getAddressImmediateString());
        String finalRegister = nameRegisters[addi.getRT()];
        System.out.println("ADDI " + finalRegister + ", " + arg1 + ", " + immediate);
    }

    public void lui(byte[] code) {
        Itype lui = new Itype(code);
        int value = Auxiliary.binaryStringToInt(lui.getAddressImmediateString());
        String  finalRegister = nameRegisters[lui.getRT()];
        System.out.println("LUI " + finalRegister + ", " + value);
    }

    public void ori(byte[] code) {
        Itype ori = new Itype(code);
        String initialRegister = nameRegisters[ori.getRS()];
        int value = Auxiliary.binaryStringToInt( ori.getAddressImmediateString());
        String finalRegister = nameRegisters[ori.getRT()];
        System.out.println("ORI " + finalRegister + ", "+ initialRegister +
                                                                     ", " + value);
    }  

    public void andi(byte[] code) {
        Itype andi = new Itype(code);
        String immediate = andi.getAddressImmediateString();
        while(immediate.length() < 32){
            immediate = "0" + immediate;
        }
        System.out.println("ANDI " + nameRegisters[andi.getRT()] + ", " +
                                        nameRegisters[andi.getRS()] + ", 0x" + 
                                                String.format("%x", immediate));
    }
    
    public void addiu(byte[] code) {
        Itype addiu= new Itype(code);
        String arg = nameRegisters[addiu.getRS()];
        int immediate = Auxiliary.binaryStringToInt(addiu.getAddressImmediateString());
        String finalRegister = nameRegisters[addiu.getRT()];
        System.out.println("ADDIU " + finalRegister + ", " + arg + ", " + immediate);
    }

    public void beq(byte[] code) {
        Itype beq= new Itype(code);
        String arg1 = nameRegisters[beq.getRS()];
        String arg2 = nameRegisters[beq.getRT()];
        int offset = Auxiliary.binaryStringToInt(beq.getAddressImmediateString());
        System.out.println("BEQ " + arg1 + ", " + arg2 + ", " + offset);
    }

    public void bne(byte[] code) {
        Itype bne= new Itype(code);
        String arg1 = nameRegisters[bne.getRS()];
        String arg2 = nameRegisters[bne.getRT()];
        int offset = Auxiliary.binaryStringToInt(bne.getAddressImmediateString());
        System.out.println("BNE " + arg1 + ", " + arg2 + ", " + offset);
    }

    public void sw(byte[] code){
        Itype sw = new Itype(code);
        String value = nameRegisters[sw.getRT()];
        String address = nameRegisters[sw.getRS()];
        int offset = Auxiliary.binaryStringToInt(sw.getAddressImmediateString());
        System.out.println("SW " + value + ", " +  offset + "(" + address + ")" +
                                                    " # " + String.format("%08x", offset));
    }

    public void lw(byte[] code){
        Itype lw = new Itype(code);
        String value = nameRegisters[lw.getRT()];
        String address = nameRegisters[lw.getRS()];
        int offset = Auxiliary.binaryStringToInt(lw.getAddressImmediateString());
        System.out.println("LW " + value + ", " +  offset + "(" + address + ")" +
                                                    " # " + String.format("%08x", offset));
    }

    public void blez(byte[] code){
        Itype blez= new Itype(code);
        String arg1 = nameRegisters[blez.getRS()];
        int offset = Auxiliary.binaryStringToInt(blez.getAddressImmediateString());
        System.out.println("BLEZ " + arg1 + ", " + offset);
    }

    public void slti(byte[] code){
        Itype slti= new Itype(code);
        String arg1 = nameRegisters[slti.getRS()];
        String arg2 = nameRegisters[slti.getRT()];
        int offset = Auxiliary.binaryStringToInt(slti.getAddressImmediateString());
        System.out.println("BEQ " + arg2 + ", " + arg1 + ", " + offset);
    }

    public void lb(byte[] code){
        Itype lb = new Itype(code);
        String value = nameRegisters[lb.getRT()];
        String address = nameRegisters[lb.getRS()];
        int offset = Auxiliary.binaryStringToInt(lb.getAddressImmediateString());
        System.out.println("LB " + value + ", " +  offset + "(" + address + ")" +
                                                     " # " + String.format("%08x", offset));
    }

    //J-types
    //==========================================================================

    public void jump(byte[] code) {
        Jtype jump= new Jtype(code);
        int address = Integer.parseInt(jump.getTargetAddress(), 2);
        System.out.println("J 0x" + String.format("%x",address) +
                                 " # 0x" + String.format("%08x",(address * 4)));
    }

    public void jal(byte[] code) {
        Jtype jal = new Jtype(code);
        int address = Integer.parseInt(jal.getTargetAddress(), 2);
        System.out.println("JAL 0x" + String.format("%x",address) +
                                 " # 0x" + String.format("%08x",(address * 4)));
    }

    //Coprocessor/Processor Operations - FLTP
    //==========================================================================

    public void mfc1(byte[] code){
        Rtype mfc1 = new Rtype(code);
        String arg1 = nameRegisters[mfc1.getRT()];
        String farg2 = nameCoregisters[mfc1.getRD()];
        System.out.println("MFC1 " + arg1 + ", " + farg2);
    }

    public void mtc1(byte[] code){
        Rtype mtc1 = new Rtype(code);
        String arg1 = nameRegisters[mtc1.getRT()];
        String farg2 = nameCoregisters[mtc1.getRD()];
        System.out.println("MFC1 " + arg1 + ", " + farg2);
    }

    public void s_definition(byte[] code){
        codecoder.runInstruction(code, "_s");
    }

    public void d_definition(byte[] code){
        codecoder.runInstruction(code, "_d");
    }

    public void w_definition(byte[] code){
        codecoder.runInstruction(code, "_w");
    }

    public void lwc1(byte[] code){
        Itype lwc1 = new Itype(code);
        String value = nameCoregisters[lwc1.getRT()];
        String address = nameRegisters[lwc1.getRS()];
        int offset = Auxiliary.binaryStringToInt(lwc1.getAddressImmediateString());
        System.out.println("LWC1 " + value + ", " +  offset + "(" + address + ")" +
                                                    " # " + String.format("%08x", offset) );
    }

    public void ldc1(byte[] code){
        System.out.println(" ");
        Itype ldc1 = new Itype(code);
        String value = nameCoregisters[ldc1.getRT()];
        String address = nameRegisters[ldc1.getRS()];
        int offset = Auxiliary.binaryStringToInt(ldc1.getAddressImmediateString());
        System.out.println("LDC1 " + value + ", " +  offset + "(" + address + ")" +
                                                    " # " + String.format("%08x", offset) );
    }

    //REGIMM Operations
    //==========================================================================

    public void bgez(byte[] code) {
        Itype bgez = new Itype(code);
        String arg1 = nameRegisters[bgez.getRS()];
        int offset = Auxiliary.binaryStringToInt(bgez.getAddressImmediateString());
        System.out.println("BGEZ " + arg1 + ", " + offset); 
    }

}
