import java.nio.file.Path;
import java.nio.file.Paths;

import structures.memory.Memory;
import structures.processors.Processor;
import structures.decoders.Decoder;
import structures.cachememory.*;

public class Minips {
    public static void main(String[] args) throws Exception {
        if(args[0].equals("decode")){
            String file = args[1];
            Path pathText = Paths.get("../" + file + ".text");
            Path pathData = Paths.get("../" + file + ".data");
            Path pathRodata = Paths.get("../" + file + ".rodata");
            Memory m = new Memory(pathText, pathData, pathRodata);  
            Decoder d = new Decoder(m);
            d.start();
        }else if(args[0].equals("run")){    
            if(args[1].equals("1")){
                String file = args[2];
                Path pathText = Paths.get("../" + file + ".text");
                Path pathData = Paths.get("../" + file + ".data");
                Path pathRodata = Paths.get("../" + file + ".rodata");
                Memory m = new Memory(pathText, pathData, pathRodata);  
                CacheControll l1 = new CacheNull(m);  
                Processor p = new Processor(l1);
                p.start();
            }else if (args[1].equals("2")){
                String file = args[2];
                Path pathText = Paths.get("../" + file + ".text");
                Path pathData = Paths.get("../" + file + ".data");
                Path pathRodata = Paths.get("../" + file + ".rodata");
                Memory m = new Memory(pathText, pathData, pathRodata);  
                CacheControll l1 = new CacheUnifiedDir(m, 1024, 32, null);  
                Processor p = new Processor(l1);
                p.start();
            }else if (args[1].equals("3")){
                String file = args[2];
                Path pathText = Paths.get("../" + file + ".text");
                Path pathData = Paths.get("../" + file + ".data");
                Path pathRodata = Paths.get("../" + file + ".rodata");
                Memory m = new Memory(pathText, pathData, pathRodata);  
                CacheControll l1 = new CacheSplitDir(m, 512, 32, null);  
                Processor p = new Processor(l1);
                p.start();
            }else if (args[1].equals("4")){
                String file = args[2];
                Path pathText = Paths.get("../" + file + ".text");
                Path pathData = Paths.get("../" + file + ".data");
                Path pathRodata = Paths.get("../" + file + ".rodata");
                Memory m = new Memory(pathText, pathData, pathRodata);  
                CacheControll l1 = new CacheSplitDir(m, 512, 32, null);  
                Processor p = new Processor(l1);
                p.start();
            }else if (args[1].equals("5")){
                String file = args[2];
                Path pathText = Paths.get("../" + file + ".text");
                Path pathData = Paths.get("../" + file + ".data");
                Path pathRodata = Paths.get("../" + file + ".rodata");
                Memory m = new Memory(pathText, pathData, pathRodata);  
                CacheControll l1 = new CacheSplitWayLRU(m, 512, 32, 4, null);
                Processor p = new Processor(l1);
                p.start();
            }else if (args[1].equals("6")){
                String file = args[2];
                Path pathText = Paths.get("../" + file + ".text");
                Path pathData = Paths.get("../" + file + ".data");
                Path pathRodata = Paths.get("../" + file + ".rodata");
                Memory m = new Memory(pathText, pathData, pathRodata);  
                CacheControll l2 = new CacheUnifiedWayLRU(m, 2048, 64, 8, null);
                CacheControll l1 = new CacheSplitWayLRU(m, 512, 64, 4, l2);
                Processor p = new Processor(l1);
                p.start();  
            }else{
                System.out.println("Invalid configuration.");
                System.out.println("Please, use: 1, 2, 3, 5 ou 6.");
            } 
        }else{
            System.out.println("Invalid command.");
            System.out.print("Please, use: java Minips run file / ");
            System.out.println("java Minips decode file.");
        }
    }
}