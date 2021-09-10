package structures.decoders;
import java.lang.reflect.Method;
import types.*;
import structures.auxiliaries.Auxiliary;
import structures.auxiliaries.CoFunctions;

public class Codecoder implements CoFunctions{
    
    private String[] nameCoregisters;
    private String[][] coFunctions = new String[8][8]; 

    public Codecoder(String[] nameCoregisters){
        this.nameCoregisters = nameCoregisters;
        coFunctions = Auxiliary.getCoTable();
    }

    public void runInstruction(byte[] word, String definition){
        int x = (int) ((word[3] & 0b00111000) >> 3);
        int y = (int) (word[3] & 0b00000111);
        runFunction((coFunctions[x][y] + definition), word);
    }

    public void runFunction(String func, byte[] code){
        Method testMethod;
        try {
            testMethod = Codecoder.class.getMethod(func, byte[].class);
            testMethod.invoke(this, code);
        } catch (Exception e) {
            System.out.println("Comprocessor1.");
            System.out.println("Invalid for Reflection API.");
            System.out.println("Code: " + code);
        }
    }

    public void mov_s(byte[] code){
        Rtype mov_s = new Rtype(code);
        String arg1 = nameCoregisters[mov_s.getShamt()];
        String arg2 = nameCoregisters[mov_s.getRD()];
        System.out.println("MOV.S " + arg1 + ", " + arg2);
    }

    public void add_s(byte[] code){
        Rtype add_s = new Rtype(code);
        String arg1 = nameCoregisters[add_s.getRD()];
        String arg2 = nameCoregisters[add_s.getRT()];
        String finalValue = nameCoregisters[add_s.getShamt()];
        System.out.println("ADD.S " + finalValue + ", " + arg1 + ", " + arg2);
    }

    public void mul_s(byte[] code){
        Rtype mul_s = new Rtype(code);
        String arg1 = nameCoregisters[mul_s.getRD()];
        String arg2 = nameCoregisters[mul_s.getRT()];
        String finalValue = nameCoregisters[mul_s.getShamt()];
        System.out.println("MUL.S " + finalValue + ", " + arg1 + ", " + arg2);
    }

    public void mov_d(byte[] code){
        Rtype mov_d = new Rtype(code);
        String arg1 = nameCoregisters[mov_d.getShamt()];
        String arg2 = nameCoregisters[mov_d.getRD()];
        System.out.println("MOV.D " + arg1 + ", " + arg2);
    }

    public void add_d(byte[] code){
        Rtype add_d = new Rtype(code);
        String arg1 = nameCoregisters[add_d.getRD()];
        String arg2 = nameCoregisters[add_d.getRT()];
        String finalValue = nameCoregisters[add_d.getShamt()];
        System.out.println("ADD.D " + finalValue + ", " + arg1 + ", " + arg2);
    }

    public void mul_d(byte[] code){
        Rtype mul_d = new Rtype(code);
        String arg1 = nameCoregisters[mul_d.getRD()];
        String arg2 = nameCoregisters[mul_d.getRT()];
        String finalValue = nameCoregisters[mul_d.getShamt()];
        System.out.println("MUL.D " + finalValue + ", " + arg1 + ", " + arg2);
    }

    public void div_d(byte[] code){
        Rtype div_d = new Rtype(code);
        String arg1 = nameCoregisters[div_d.getRD()];
        String arg2 = nameCoregisters[div_d.getRT()];
        String finalValue = nameCoregisters[div_d.getShamt()];
        System.out.println("DIV.D " + finalValue + ", " + arg1 + ", " + arg2);
    }

    public void cvt_s_d(byte[] code){
        Rtype cvt_s_d = new Rtype(code);
        String arg1 = nameCoregisters[cvt_s_d.getShamt()];
        String arg2 = nameCoregisters[cvt_s_d.getRD()];
        System.out.println("CVT.S.D " + arg1 + ", " + arg2);
    }

    public void cvt_d_w(byte[] code){
        Rtype cvt_d_w = new Rtype(code);
        String arg1 = nameCoregisters[cvt_d_w.getShamt()];
        String arg2 = nameCoregisters[cvt_d_w.getRD()];
        System.out.println("CVT.D.W " + arg1 + ", " + arg2);
    }
}
