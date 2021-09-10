package structures.inputoutput;
import structures.registers.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import structures.cachememory.*;
//import structures.memory.Memory;

public class Syscall{
    private Registers registers;
    private RegistersCO1 coregisters;
   // private Memory memory;
   private CacheControll l1;

    public Syscall(CacheControll l1, Registers registers, RegistersCO1 coregisters){
        //this.memory = memory;
        this.l1 = l1;
        this.registers = registers;
        this.coregisters = coregisters;
    }

    public void run(){
        if(registers.getRegister(2) == 1){
            printInt();
        }else if(registers.getRegister(2) == 2){
            printFloat();
        }else if(registers.getRegister(2) == 3){
            printDouble();
        }else if(registers.getRegister(2) == 4){
            printString();
        }else if(registers.getRegister(2) == 5){
            readInt();
        }else if(registers.getRegister(2) == 6){
            readFloat();
        }else if(registers.getRegister(2) == 7){
            readDouble();
        }else if(registers.getRegister(2) == 11){
            printChar();
        }else {
            System.out.println("Invalid syscall number: "+ registers.getRegister(2));
        }
    }

    private void printInt(){
        System.out.print(registers.getRegister(4));
    }

    private void printString(){
        char c = 'a';
        boolean endString = false;
        int index = 0;
        int position = registers.getRegister(4);
        if(position % 4 != 0){
            int tempPosition = position - (position % 4);
            //byte[] wordOfParts = memory.loadWordByPosition(tempPosition);
            byte[] wordOfParts = l1.load(tempPosition);
            byte[] partsOfWord = new byte[4 - (position % 4)];
            for(int i = 0; i < 4 - (position % 4); i++){
                partsOfWord[i] = wordOfParts[i];
            }
            for(int x = 4 - (position % 4) - 1; x >= 0; x--){
                c = (char) (Byte.toUnsignedInt(Byte.valueOf(partsOfWord[x])));
                System.out.print(c);
            }
            position = position + (4 - (position % 4));
        }
        //byte[] word = memory.loadWordByPosition(position);
        byte[] word = l1.load(position);
        while(endString == false){
            for(int x = 3; x >= 0; x--){
                c = (char) (Byte.toUnsignedInt(Byte.valueOf(word[x])));
                System.out.print(c);
                if(c == '\0'){
                    endString = true;
                    break;
                }
            }
            index += 4;
           // word = memory.loadWordByPosition(position + index);
           word = l1.load(position + index);
        }
    }

    private void printChar(){
        System.out.print((char)(registers.getRegister(4)));
    }

    private void printFloat(){
        System.out.print((float) (coregisters.getFloatRegister(12)));
    }

    private void printDouble(){
        System.out.print(coregisters.getDoubleRegister(12));
    }

    private void readInt(){
        BufferedReader input = new BufferedReader(
                                    new InputStreamReader(System.in));
        try {
            String inputString = input.readLine();
            int number = Integer.parseInt(inputString);
            registers.setRegister(2, number);
        } catch (IOException e) {
            System.out.println("Invalid number");
        }
    }

    private void readFloat(){
        BufferedReader input = new BufferedReader(
                                    new InputStreamReader(System.in));
        try {
            String inputString = input.readLine();
            float number = Float.parseFloat(inputString);
            byte[] bytes = ByteBuffer.allocate(4).putFloat(number).array();
            int numberFloat = ByteBuffer.wrap(bytes).getInt();
            coregisters.setSRegister(0, numberFloat);
        } catch (IOException e) {
            System.out.println("Invalid number");
        }
    }

    private void readDouble(){
        BufferedReader input = new BufferedReader(
                                    new InputStreamReader(System.in));
        try {
            String inputString = input.readLine();
            double number = Double.parseDouble(inputString);
            byte[] finalValue = ByteBuffer.allocate(8).putDouble(number).array();
            byte[] bytesOdd = {finalValue[0], finalValue[1], finalValue[2], finalValue[3]};
            byte[] bytesEven = {finalValue[4], finalValue[5], finalValue[6], finalValue[7]};
            int valueOdd = ByteBuffer.wrap(bytesOdd).getInt();
            int valueEven = ByteBuffer.wrap(bytesEven).getInt();
            coregisters.setDRegister(0, valueEven, valueOdd);

        } catch (IOException e) {
            System.out.println("Invalid number");
        }
    }
}
