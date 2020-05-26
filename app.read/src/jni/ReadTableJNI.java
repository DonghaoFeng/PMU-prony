package jni;


public class ReadTableJNI
{
    static
    {
        System.loadLibrary("RT");
    }


    public native static String readTable(String sys_name,String bob_name,String db_name,String tb_name);
}
