//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

public class FileList extends AbstractList<Comparable[]> implements List<Comparable[]>, RandomAccess, Serializable {
    private static final String EXT = ".dat";
    private int length_of_file = 0;
    private transient RandomAccessFile file;
    private final String tableName;
    private final int recordSize;
    private int nRecords = 0;
    private int actual_byte = 0;

    public FileList(String _tableName, int _recordSize) {
        this.tableName = _tableName;
        this.recordSize = _recordSize;

        try {
            this.file = new RandomAccessFile(this.tableName + ".dat", "rw");
            this.file.seek(0L);
        } catch (Exception var4) {
            this.file = null;
            System.out.println("FileList.constructor: unable to open - " + String.valueOf(var4));
        }

    }

    private byte[] pack(Comparable[] tuple) {
        byte[] fixedBytes = new byte[this.recordSize];

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(tuple);
            oos.flush();
            if (bos.size() < this.recordSize) {
                System.out.println(bos.size());
                byte[] garbageBytes = new byte[this.recordSize - bos.size() - 2];
                oos.write(garbageBytes);
                oos.flush();
                System.out.println(garbageBytes.length);
                System.out.println(bos.size());
                System.out.println(bos.toByteArray().length);
            }

            if (bos.size() > this.recordSize) {
                throw new Exception("The byte size of tuple is more than record size please increase the size of record");
            } else {
                return bos.toByteArray();
            }
        } catch (Exception var6) {
            System.out.println(var6);
            return fixedBytes;
        }
    }

    private Comparable[] unpack(byte[] packedBytes) {
        try {
            System.out.println("Getting the object");
            System.out.println(packedBytes.length);
            ByteArrayInputStream bis = new ByteArrayInputStream(packedBytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            System.out.println(bis.available());
            Object o = ois.readObject();
            return (Comparable[])o;
        } catch (Exception var5) {
            System.out.println(var5.getMessage());
            var5.printStackTrace();
            return null;
        }
    }

    public boolean add(Comparable[] tuple) {
        byte[] record = this.pack(tuple);
        if (record.length != this.recordSize) {
            System.out.println(record);
            System.out.println("Record Size:" + this.recordSize);
            System.out.println("FileList.add: wrong record size " + record.length);
            return false;
        } else {
            try {
                this.file.seek((long)this.length_of_file);
                this.file.write(record);
                this.length_of_file += record.length;
                ++this.nRecords;
                return true;
            } catch (IOException var4) {
                System.out.println("FileList.add: error writing record - " + String.valueOf(var4));
                return false;
            }
        }
    }

    public Comparable[] get(int i) {
        byte[] record = new byte[this.recordSize];

        try {
            System.out.println(i);
            this.file.seek((long)(i * this.recordSize));
            this.file.read(record);
            return this.unpack(record);
        } catch (IOException var4) {
            System.out.println("FileList.get: error reading record - " + String.valueOf(var4));
            return null;
        }
    }

    public int size() {
        return this.nRecords;
    }

    public void close() {
        try {
            this.file.close();
        } catch (IOException var2) {
            System.out.println("FileList.close: unable to close - " + String.valueOf(var2));
        }

    }
}
