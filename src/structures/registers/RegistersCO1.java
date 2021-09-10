package structures.registers;
import java.util.Arrays;
import java.nio.ByteBuffer;

public class RegistersCO1{

    private int[] registers = new int[32];

    public RegistersCO1(){
        Arrays.fill(registers, 0x00000000);
    }

    public void setSRegister(int index, int value){
        registers[index] = value;
    }

    public int getSRegister(int index){
        return registers[index];
    }

    public void setDRegister(int index, int valueEven, int valueOdd){
        if(index % 2 == 0){
            registers[index] = valueEven;
            registers[index + 1] = valueOdd;
        }else{
            System.out.println("Invalid index to set Double.");
        }
    }

    public float getFloatRegister(int index){
        byte[] bytes = ByteBuffer.allocate(4).putInt(registers[index]).array();
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public double getDoubleRegister(int index){

        byte[] bytes1 = ByteBuffer.allocate(4).putInt(registers[index + 1]).array();
        byte[] bytes2 = ByteBuffer.allocate(4).putInt(registers[index]).array();
        byte[] bytesValue = {bytes1[0], bytes1[1], bytes1[2], bytes1[3], 
                            bytes2[0], bytes2[1], bytes2[2], bytes2[3]};
        return ByteBuffer.wrap(bytesValue).getDouble();
    }

    public String[] getAllNameRegisters(){
        String[] namesRegisters =  new String[32];
        for (int x = 0; x < 32; x++){
            namesRegisters[x] = "$f" + String.valueOf(x);
        }
        return namesRegisters;
    }

}
