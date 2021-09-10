package structures.processors;
import types.*;
import structures.auxiliaries.*;
import structures.registers.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class Coprocessor implements CoFunctions{

    private RegistersCO1 coregisters;
    private String coFunctions[][] = Auxiliary.getCoTable();

    public Coprocessor(RegistersCO1 coregisters){
        this.coregisters = coregisters;
    }

    public void runInstruction(byte[] word, String definition){
        int x = (int) ((word[3] & 0b00111000) >>> 3);
        int y = (int) (word[3] & 0b00000111);
        runFunction((coFunctions[x][y] + definition), word); 
    }

    public void runFunction(String func, byte[] code){
        Method testMethod;
        try {
            testMethod = Coprocessor.class.getMethod(func, byte[].class);
            testMethod.invoke(this, code);
        } catch (Exception e) {
            MainType command = new MainType(code);
            System.out.println("Coprocessor1.");
            System.out.println("Invalid for Reflection API.");
            System.out.println("Code: " + command.getCode());
        }
    }

    public void mov_s(byte[] code){
        FRtype mov_s = new FRtype(code);
        coregisters.setSRegister(mov_s.getFD(),coregisters.getSRegister(mov_s.getFS()));  
    }

    public void add_s(byte[] code){
        FRtype add_s = new FRtype(code);
        float arg1 = coregisters.getFloatRegister(add_s.getFS());
        float arg2 = coregisters.getFloatRegister(add_s.getFT());  
        byte[] bytes = ByteBuffer.allocate(4).putFloat(arg1 + arg2).array();
        int finalValue = ByteBuffer.wrap(bytes).getInt();
        coregisters.setSRegister(add_s.getFD(), finalValue);
    }

    public void mul_s(byte[] code){
        FRtype mul_s = new FRtype(code);
        float arg1 = coregisters.getFloatRegister(mul_s.getFS());
        float arg2 = coregisters.getFloatRegister(mul_s.getFT()); 
        byte[] bytes = ByteBuffer.allocate(4).putFloat(arg1 * arg2).array();
        int finalValue = ByteBuffer.wrap(bytes).getInt();
        coregisters.setSRegister(mul_s.getFD(), finalValue);
    }

    public void mov_d(byte[] code){
        FRtype mov_s = new FRtype(code);
        coregisters.setSRegister(mov_s.getFD(), coregisters.getSRegister(mov_s.getFS()));
        coregisters.setSRegister(mov_s.getFD() + 1, coregisters.getSRegister(mov_s.getFS() + 1));
    }

    public void add_d(byte[] code){
        FRtype add_d = new FRtype(code);
        double arg1 = coregisters.getDoubleRegister(add_d.getFS());
        double arg2 = coregisters.getDoubleRegister(add_d.getFT());
        byte[] finalValue = ByteBuffer.allocate(8).putDouble(arg1 + arg2).array();
        byte[] bytesOdd = {finalValue[0], finalValue[1], finalValue[2], finalValue[3]};
        byte[] bytesEven = {finalValue[4], finalValue[5], finalValue[6], finalValue[7]};
        int valueOdd =  ByteBuffer.wrap(bytesOdd).getInt();
        int valueEven = ByteBuffer.wrap(bytesEven).getInt();
        coregisters.setDRegister(add_d.getFD(), valueEven, valueOdd);
    }

    public void mul_d(byte[] code){
        FRtype mul_d = new FRtype(code);
        double arg1 = coregisters.getDoubleRegister(mul_d.getFS());
        double arg2 = coregisters.getDoubleRegister(mul_d.getFT());
        byte[] finalValue = ByteBuffer.allocate(8).putDouble(arg1 * arg2).array();
        byte[] bytesOdd = {finalValue[0], finalValue[1], finalValue[2], finalValue[3]};
        byte[] bytesEven = {finalValue[4], finalValue[5], finalValue[6], finalValue[7]};
        int valueOdd =  ByteBuffer.wrap(bytesOdd).getInt();
        int valueEven = ByteBuffer.wrap(bytesEven).getInt();
        coregisters.setDRegister(mul_d.getFD(), valueEven, valueOdd);
    }

    public void div_d(byte[] code){
        FRtype div_d = new FRtype(code);
        double arg1 = coregisters.getDoubleRegister(div_d.getFS());
        double arg2 = coregisters.getDoubleRegister(div_d.getFT());
        byte[] finalValue = ByteBuffer.allocate(8).putDouble(arg1 / arg2).array();
        byte[] bytesOdd = {finalValue[0], finalValue[1], finalValue[2], finalValue[3]};
        byte[] bytesEven = {finalValue[4], finalValue[5], finalValue[6], finalValue[7]};
        int valueOdd = ByteBuffer.wrap(bytesOdd).getInt();
        int valueEven = ByteBuffer.wrap(bytesEven).getInt();
        coregisters.setDRegister(div_d.getFD(), valueEven, valueOdd);
    }

    public void cvt_s_d(byte[] code){
        FRtype cvt_s_d = new FRtype(code);
        double arg1 = coregisters.getDoubleRegister(cvt_s_d.getFS());
        float arg1Float = (float) arg1; // arredondamento < -----
        byte[] bytesValue =  ByteBuffer.allocate(4).putFloat(arg1Float).array();
        int finalValue = ByteBuffer.wrap(bytesValue).getInt();
        coregisters.setSRegister(cvt_s_d.getFD(), finalValue);
    }

    public void cvt_d_w(byte[] code){
        FRtype cvt_d_w = new FRtype(code);
        float arg1Float = coregisters.getSRegister(cvt_d_w.getFS());
        double arg1Double = (double) arg1Float; // arredondamento < -----
        byte[] finalValue = ByteBuffer.allocate(8).putDouble(arg1Double).array();
        byte[] bytesOdd = {finalValue[0], finalValue[1], finalValue[2], finalValue[3]};
        byte[] bytesEven = {finalValue[4], finalValue[5], finalValue[6], finalValue[7]};
        int valueOdd = ByteBuffer.wrap(bytesOdd).getInt();
        int valueEven = ByteBuffer.wrap(bytesEven).getInt();
        coregisters.setDRegister(cvt_d_w.getFD(), valueEven, valueOdd);
    }

}
