package com.fcs.invoke.assembly;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class ConstPool {

    LongVector items;
    int numOfItems;
    Map<String, ConstInfo> strings;
    ConstInfo[] constInfoCache;
    int[] constInfoIndexCache;
    int thisClassInfo;
    private static final int CACHE_SIZE = 32;
    public static final int CONST_Class = 7;
    public static final int CONST_Fieldref = 9;
    public static final int CONST_Methodref = 10;
    public static final int CONST_InterfaceMethodref = 11;
    public static final int CONST_String = 8;
    public static final int CONST_Integer = 3;
    public static final int CONST_Float = 4;
    public static final int CONST_Long = 5;
    public static final int CONST_Double = 6;
    public static final int CONST_NameAndType = 12;
    public static final int CONST_Utf8 = 1;

    private static int hashFunc(int a, int b) {
        int h = -2128831035;
        int prime = 16777619;
        h = (h ^ a & 255) * 16777619;
        h = (h ^ b & 255) * 16777619;
        h = h >> 5 ^ h & 31;
        return h & 31;
    }

    public ConstPool(String thisclass) {
        this.items = new LongVector();
        this.numOfItems = 0;
        this.addItem((ConstInfo)null);
        this.strings = new HashMap();
        this.constInfoCache = new ConstInfo[32];
        this.constInfoIndexCache = new int[32];
    }

    public ConstPool(DataInputStream in) throws IOException {
        this.strings = new HashMap();
        this.constInfoCache = new ConstInfo[32];
        this.constInfoIndexCache = new int[32];
        this.thisClassInfo = 0;
        this.read(in);
    }

    void prune() {
        this.strings = new HashMap();
        this.constInfoCache = new ConstInfo[32];
        this.constInfoIndexCache = new int[32];
    }

    public int getSize() {
        return this.numOfItems;
    }

    public int getThisClassInfo() {
        return this.thisClassInfo;
    }

    void setThisClassInfo(int i) {
        this.thisClassInfo = i;
    }

    ConstInfo getItem(int n) {
        return this.items.elementAt(n);
    }

    public int getTag(int index) {
        return this.getItem(index).getTag();
    }

    public int getNameAndTypeName(int index) {
        NameAndTypeInfo ntinfo = (NameAndTypeInfo)this.getItem(index);
        return ntinfo.memberName;
    }

    public int getNameAndTypeDescriptor(int index) {
        NameAndTypeInfo ntinfo = (NameAndTypeInfo)this.getItem(index);
        return ntinfo.typeDescriptor;
    }

    public int getMemberClass(int index) {
        MemberrefInfo minfo = (MemberrefInfo)this.getItem(index);
        return minfo.classIndex;
    }

    public int getMemberNameAndType(int index) {
        MemberrefInfo minfo = (MemberrefInfo)this.getItem(index);
        return minfo.nameAndTypeIndex;
    }

    public int getFieldrefClass(int index) {
        FieldrefInfo finfo = (FieldrefInfo)this.getItem(index);
        return finfo.classIndex;
    }

    public int getFieldrefNameAndType(int index) {
        FieldrefInfo finfo = (FieldrefInfo)this.getItem(index);
        return finfo.nameAndTypeIndex;
    }

    public String getFieldrefName(int index) {
        FieldrefInfo f = (FieldrefInfo)this.getItem(index);
        if(f == null) {
            return null;
        } else {
            NameAndTypeInfo n = (NameAndTypeInfo)this.getItem(f.nameAndTypeIndex);
            return n == null?null:this.getUtf8Info(n.memberName);
        }
    }

    public String getFieldrefType(int index) {
        FieldrefInfo f = (FieldrefInfo)this.getItem(index);
        if(f == null) {
            return null;
        } else {
            NameAndTypeInfo n = (NameAndTypeInfo)this.getItem(f.nameAndTypeIndex);
            return n == null?null:this.getUtf8Info(n.typeDescriptor);
        }
    }

    public int getMethodrefClass(int index) {
        MethodrefInfo minfo = (MethodrefInfo)this.getItem(index);
        return minfo.classIndex;
    }

    public int getMethodrefNameAndType(int index) {
        MethodrefInfo minfo = (MethodrefInfo)this.getItem(index);
        return minfo.nameAndTypeIndex;
    }

    public String getMethodrefName(int index) {
        MethodrefInfo minfo = (MethodrefInfo)this.getItem(index);
        if(minfo == null) {
            return null;
        } else {
            NameAndTypeInfo n = (NameAndTypeInfo)this.getItem(minfo.nameAndTypeIndex);
            return n == null?null:this.getUtf8Info(n.memberName);
        }
    }

    public String getMethodrefType(int index) {
        MethodrefInfo minfo = (MethodrefInfo)this.getItem(index);
        if(minfo == null) {
            return null;
        } else {
            NameAndTypeInfo n = (NameAndTypeInfo)this.getItem(minfo.nameAndTypeIndex);
            return n == null?null:this.getUtf8Info(n.typeDescriptor);
        }
    }

    public int getInterfaceMethodrefClass(int index) {
        InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)this.getItem(index);
        return minfo.classIndex;
    }

    public int getInterfaceMethodrefNameAndType(int index) {
        InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)this.getItem(index);
        return minfo.nameAndTypeIndex;
    }

    public String getInterfaceMethodrefName(int index) {
        InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)this.getItem(index);
        if(minfo == null) {
            return null;
        } else {
            NameAndTypeInfo n = (NameAndTypeInfo)this.getItem(minfo.nameAndTypeIndex);
            return n == null?null:this.getUtf8Info(n.memberName);
        }
    }

    public String getInterfaceMethodrefType(int index) {
        InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)this.getItem(index);
        if(minfo == null) {
            return null;
        } else {
            NameAndTypeInfo n = (NameAndTypeInfo)this.getItem(minfo.nameAndTypeIndex);
            return n == null?null:this.getUtf8Info(n.typeDescriptor);
        }
    }

    public Object getLdcValue(int index) {
        ConstInfo constInfo = this.getItem(index);
        Object value = null;
        if(constInfo instanceof StringInfo) {
            value = this.getStringInfo(index);
        } else if(constInfo instanceof FloatInfo) {
            value = new Float(this.getFloatInfo(index));
        } else if(constInfo instanceof IntegerInfo) {
            value = new Integer(this.getIntegerInfo(index));
        } else if(constInfo instanceof LongInfo) {
            value = new Long(this.getLongInfo(index));
        } else if(constInfo instanceof DoubleInfo) {
            value = new Double(this.getDoubleInfo(index));
        } else {
            value = null;
        }

        return value;
    }

    public int getIntegerInfo(int index) {
        IntegerInfo i = (IntegerInfo)this.getItem(index);
        return i.value;
    }

    public float getFloatInfo(int index) {
        FloatInfo i = (FloatInfo)this.getItem(index);
        return i.value;
    }

    public long getLongInfo(int index) {
        LongInfo i = (LongInfo)this.getItem(index);
        return i.value;
    }

    public double getDoubleInfo(int index) {
        DoubleInfo i = (DoubleInfo)this.getItem(index);
        return i.value;
    }

    public String getStringInfo(int index) {
        StringInfo si = (StringInfo)this.getItem(index);
        return this.getUtf8Info(si.string);
    }

    public String getUtf8Info(int index) {
        Utf8Info utf = (Utf8Info)this.getItem(index);
        return utf.string;
    }

    public int isConstructor(String classname, int index) {
        return 0;
    }

    private int addItem(ConstInfo info) {
        this.items.addElement(info);
        return this.numOfItems++;
    }

    public int copy(int n, ConstPool dest, Map<?, ?> classnames) {
        if(n == 0) {
            return 0;
        } else {
            ConstInfo info = this.getItem(n);
            return info.copy(this, dest, classnames);
        }
    }

    int addConstInfoPadding() {
        return this.addItem(new ConstInfoPadding());
    }

    public int addNameAndTypeInfo(String name, String type) {
        return this.addNameAndTypeInfo(this.addUtf8Info(name), this.addUtf8Info(type));
    }

    public int addNameAndTypeInfo(int name, int type) {
        int h = hashFunc(name, type);
        ConstInfo ci = this.constInfoCache[h];
        if(ci != null && ci instanceof NameAndTypeInfo && ci.hashCheck(name, type)) {
            return this.constInfoIndexCache[h];
        } else {
            NameAndTypeInfo item = new NameAndTypeInfo(name, type);
            this.constInfoCache[h] = item;
            int i = this.addItem(item);
            this.constInfoIndexCache[h] = i;
            return i;
        }
    }

    public int addFieldrefInfo(int classInfo, String name, String type) {
        int nt = this.addNameAndTypeInfo(name, type);
        return this.addFieldrefInfo(classInfo, nt);
    }

    public int addFieldrefInfo(int classInfo, int nameAndTypeInfo) {
        int h = hashFunc(classInfo, nameAndTypeInfo);
        ConstInfo ci = this.constInfoCache[h];
        if(ci != null && ci instanceof FieldrefInfo && ci.hashCheck(classInfo, nameAndTypeInfo)) {
            return this.constInfoIndexCache[h];
        } else {
            FieldrefInfo item = new FieldrefInfo(classInfo, nameAndTypeInfo);
            this.constInfoCache[h] = item;
            int i = this.addItem(item);
            this.constInfoIndexCache[h] = i;
            return i;
        }
    }

    public int addMethodrefInfo(int classInfo, String name, String type) {
        int nt = this.addNameAndTypeInfo(name, type);
        return this.addMethodrefInfo(classInfo, nt);
    }

    public int addMethodrefInfo(int classInfo, int nameAndTypeInfo) {
        int h = hashFunc(classInfo, nameAndTypeInfo);
        ConstInfo ci = this.constInfoCache[h];
        if(ci != null && ci instanceof MethodrefInfo && ci.hashCheck(classInfo, nameAndTypeInfo)) {
            return this.constInfoIndexCache[h];
        } else {
            MethodrefInfo item = new MethodrefInfo(classInfo, nameAndTypeInfo);
            this.constInfoCache[h] = item;
            int i = this.addItem(item);
            this.constInfoIndexCache[h] = i;
            return i;
        }
    }

    public int addInterfaceMethodrefInfo(int classInfo, String name, String type) {
        int nt = this.addNameAndTypeInfo(name, type);
        return this.addInterfaceMethodrefInfo(classInfo, nt);
    }

    public int addInterfaceMethodrefInfo(int classInfo, int nameAndTypeInfo) {
        int h = hashFunc(classInfo, nameAndTypeInfo);
        ConstInfo ci = this.constInfoCache[h];
        if(ci != null && ci instanceof InterfaceMethodrefInfo && ci.hashCheck(classInfo, nameAndTypeInfo)) {
            return this.constInfoIndexCache[h];
        } else {
            InterfaceMethodrefInfo item = new InterfaceMethodrefInfo(classInfo, nameAndTypeInfo);
            this.constInfoCache[h] = item;
            int i = this.addItem(item);
            this.constInfoIndexCache[h] = i;
            return i;
        }
    }

    public int addStringInfo(String str) {
        return this.addItem(new StringInfo(this.addUtf8Info(str)));
    }

    public int addIntegerInfo(int i) {
        return this.addItem(new IntegerInfo(i));
    }

    public int addFloatInfo(float f) {
        return this.addItem(new FloatInfo(f));
    }

    public int addLongInfo(long l) {
        int i = this.addItem(new LongInfo(l));
        this.addItem(new ConstInfoPadding());
        return i;
    }

    public int addDoubleInfo(double d) {
        int i = this.addItem(new DoubleInfo(d));
        this.addItem(new ConstInfoPadding());
        return i;
    }

    public int addUtf8Info(String utf8) {
        Utf8Info info = (Utf8Info)this.strings.get(utf8);
        if(info != null) {
            return info.index;
        } else {
            info = new Utf8Info(utf8, this.numOfItems);
            this.strings.put(utf8, info);
            return this.addItem(info);
        }
    }

    public Set<String> getClassNames() {
        HashSet result = new HashSet();
        LongVector v = this.items;
        int size = this.numOfItems;

        for(int i = 1; i < size; ++i) {
            String className = v.elementAt(i).getClassName(this);
            if(className != null) {
                result.add(className);
            }
        }

        return result;
    }

    public void renameClass(String oldName, String newName) {
        LongVector v = this.items;
        int size = this.numOfItems;

        for(int i = 1; i < size; ++i) {
            ConstInfo ci = v.elementAt(i);
            ci.renameClass(this, oldName, newName);
            ci.makeHashtable(this);
        }

    }

    public void renameClass(Map<?, ?> classnames) {
        LongVector v = this.items;
        int size = this.numOfItems;

        for(int i = 1; i < size; ++i) {
            ConstInfo ci = v.elementAt(i);
            ci.renameClass(this, classnames);
            ci.makeHashtable(this);
        }

    }

    private void read(DataInputStream in) throws IOException {
        int n = in.readUnsignedShort();
        this.items = new LongVector(n);
        this.numOfItems = 0;
        this.addItem((ConstInfo)null);

        while(true) {
            int i;
            do {
                --n;
                if(n <= 0) {
                    i = 1;

                    while(true) {
                        ConstInfo info = this.items.elementAt(i++);
                        if(info == null) {
                            return;
                        }

                        info.makeHashtable(this);
                    }
                }

                i = this.readOne(in);
            } while(i != 5 && i != 6);

            this.addItem(new ConstInfoPadding());
            --n;
        }
    }

    private int readOne(DataInputStream in) throws IOException {
        int tag = in.readUnsignedByte();
        Object info;
        switch(tag) {
            case 1:
                info = new Utf8Info(in, this.numOfItems);
                this.strings.put(((Utf8Info)info).string, info);
                break;
            case 2:
            default:
                throw new IOException("invalid constant type: " + tag);
            case 3:
                info = new IntegerInfo(in);
                break;
            case 4:
                info = new FloatInfo(in);
                break;
            case 5:
                info = new LongInfo(in);
                break;
            case 6:
                info = new DoubleInfo(in);
                break;
            case 7:
                info = new ClassInfo(in, this.numOfItems);
                break;
            case 8:
                info = new StringInfo(in);
                break;
            case 9:
                info = new FieldrefInfo(in);
                break;
            case 10:
                info = new MethodrefInfo(in);
                break;
            case 11:
                info = new InterfaceMethodrefInfo(in);
                break;
            case 12:
                info = new NameAndTypeInfo(in);
        }

        this.addItem((ConstInfo)info);
        return tag;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeShort(this.numOfItems);
        LongVector v = this.items;
        int size = this.numOfItems;

        for(int i = 1; i < size; ++i) {
            v.elementAt(i).write(out);
        }

    }

    public void print() {
        this.print(new PrintWriter(System.out, true));
    }

    public void print(PrintWriter out) {
        int size = this.numOfItems;

        for(int i = 1; i < size; ++i) {
            out.print(i);
            out.print(" ");
            this.items.elementAt(i).print(out);
        }

    }
}
