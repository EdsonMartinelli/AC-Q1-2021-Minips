package structures.auxiliaries;

public interface Functions {

    //R-types
    //--------------------------------------------------------------------------

    public void add(byte[] code);

    public void sub(byte[] code);

    public void subu(byte[] code);

    public void addu(byte[] code);

    public void and(byte[] code);

    public void or(byte[] code);

    public void xor(byte[] code);

    public void mult(byte[] code);

    public void div(byte[] code);

    public void mflo(byte[] code);

    public void mfhi(byte[] code);

    public void slt(byte[] code);

    public void sltu(byte[] code);

    public void sll(byte[] code);

    public void srl(byte[] code);

    public void sra(byte[] code);

    public void jr(byte[] code);

    public void jalr(byte[] code);

    public void syscall(byte[] code);

    //I-types
    //--------------------------------------------------------------------------
    public void addi(byte[] code);

    public void lui(byte[] code);

    public void ori(byte[] code);
    
    public void andi(byte[] code);
    
    public void addiu(byte[] code);

    public void slti(byte[] code);

    public void beq(byte[] code);

    public void bne(byte[] code);

    public void blez(byte[] code);

    public void lw(byte[] code);

    public void sw(byte[] code);

    public void lb(byte[] code);

    //J-types
    //--------------------------------------------------------------------------
    public void jump(byte[] code);

    public void jal(byte[] code);

    //Coprocessor/Processor Operations - FLTP
    //==========================================================================

    public void mfc1(byte[] code);

    public void mtc1(byte[] code);

    public void s_definition(byte[] code);

    public void d_definition(byte[] code);

    public void w_definition(byte[] code);

    public void lwc1(byte[] code);

    public void ldc1(byte[] code);

    //REGIMM Operations
    //==========================================================================

    public void bgez(byte[] code);
    
}
