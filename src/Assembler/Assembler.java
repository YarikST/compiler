package Assembler;

import Parse.Instructions.Program;
import Parse.Node;
import Parse.Parser;
import Parse.TableID;
import Vocabulary.BaseType;
import Vocabulary.Reference;
import Vocabulary.TypeToken;
import Vocabulary.Word;
import org.omg.CORBA.TIMEOUT;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin-iorigins on 04.04.17.
 */
public class Assembler {

    static class RegisterDectructor implements Cloneable {
        private HashMap<String, LinkedList<TableID.TableElement>> map;
        private ArrayList<String> registrs;

        public RegisterDectructor(ArrayList<String> registrs) {
            map = new HashMap<>(registrs.size());
            this.registrs = registrs;
            clear();
        }

        public void add(String reg, TableID.TableElement element) {
            map.get(reg).add(element);
            element.Reg = reg;
        }

        public void remove(TableID.TableElement element, String reg) {
            map.get(reg).remove(element);
            element.Reg = null;
        }

        public String reg(TableID.TableElement element) {
            Set<String> strings = map.keySet();
            for (String reg : strings) {
                LinkedList<TableID.TableElement> words = map.get(reg);
                if (words.contains(element)) {
                    return reg;
                }
            }
            return null;
        }

        public LinkedList<TableID.TableElement> reg(String reg) {
           return map.get(reg);
        }

        public ArrayList<String> getRegistrs() {
            return registrs;
        }

        public void clear() {
            for (String reg : registrs) {
                LinkedList<TableID.TableElement> reg1 = reg(reg);

                if (reg1!=null)
                for (TableID.TableElement element : reg1) {
                    element.Reg = null;
                }
                map.put(reg, new LinkedList<TableID.TableElement>());
            }
        }

        @Override
        protected RegisterDectructor clone() {
            RegisterDectructor self = null;
            try {
                self = (RegisterDectructor) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            self.map = (HashMap<String, LinkedList<TableID.TableElement>>) map.clone();
            self.registrs = (ArrayList<String>) registrs.clone();
            return self;
        }

        @Override
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();

            for (String reg : registrs) {
                System.out.println("reg: " + reg);
                for (TableID.TableElement tableElement : reg(reg)) {
                    System.out.println(";"+tableElement);
                }
            }

            return stringBuffer.toString();
        }
    }

    private Parser parser;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Pattern patternLable,patternCall,patternGoto;
    private Pattern patternArefmetical,patternConver, patternIf,patternReturn,patternParams;

    private RegisterDectructor registerDectructor;

    private TableID tableID;

    private String this_method_name;
    private ArrayList<String> inst_new_double;
    private int unique;


    private String pathFile, nameFile;
    private String pathDir;
    private boolean stop;

    private OutputStreamWriter logOut;
    public static void main(String[] args) throws IOException, BaseType.MismatchException, BaseType.ConvertException, Parser.ParserException {
       /* Parser.main(args);
        Assembler assembler = new Assembler("tests");
        assembler.assembler();
        assembler.writer.flush();
        assembler.writer.close();*/
    }

    public Assembler(Parser parser,String pathFile,String nameFile,OutputStreamWriter logOut) {
        this.parser = parser;
        //code
        try {
            this.pathFile = pathFile;
            this.nameFile = nameFile;
            this.logOut = logOut;

            File file = new File(pathFile);
            pathDir = file.getParent();
            pathDir += "/" + nameFile + "Dir/";
            Files.createDirectories(Paths.get(pathDir));


            Node.writer = new BufferedWriter(new FileWriter(pathDir+ "Out.txt"));

            Program parse = parser.parse();
            parse.typeChecking();
            parse.gen();
            Node.writer.flush();
            Node.writer.close();

            reader = new BufferedReader(new FileReader(pathDir + "Out.txt"));
            writer = new BufferedWriter(new FileWriter(pathDir+"Asm.asm"));
        } catch (Parser.ParserException e) {
            stop = true;
            try {
                logOut.write(e.toString());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (BaseType.MismatchException e) {
            stop = true;
            try {
                logOut.write(e.toString());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (BaseType.ConvertException e) {
            stop = true;
            try {
                logOut.write(e.toString());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                logOut.flush();
                //logOut.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        //

        patternLable = Pattern.compile("((L\\d+):)|((method_(\\w+\\d*)+):)");
        patternCall = Pattern.compile("(?:((?:\\w+\\d*)+)= ||)call (method_(\\w+\\d*)),(\\d+)");
        patternGoto=Pattern.compile("goto (L\\d+)");

        patternArefmetical = Pattern.compile("((?:\\w+\\d*)+)(?:(?:\\[((?:\\w*\\d*)+)\\])|)=(?: (?:(?:((?:\\w*\\d*\\.*\\d*)+)(?:(?:\\[((?:\\w*\\d*)+)\\])||))||(\\(\\w+\\)))||)(?:(?: (.)||) ((?:\\w*\\d*\\.*\\d*)+)||)");

        // aref ok 200 Pattern.compile("((?:\\w+\\d*)+)(?:(?:\\[((?:\\w*\\d*)+)\\])|)=(?: (?:(?:((?:\\w*\\d*)+)(?:(?:\\[((?:\\w*\\d*)+)\\])||))||(\\(\\w+\\)))||)(?:(?: (.)||) ((?:\\w*\\d*)+)||)");
        //patternArefmetical =   Pattern.compile("((?:\\w+\\d*)+)(?:((?:\\[(?:(?:\\w*\\d*)+)\\])+)|)=(?: (?:(?:((?:\\w*\\d*)+)(?:((?:\\[(?:(?:\\w*\\d*)+)\\])+)||))||(\\(\\w+\\)))||)(?:(?: (.)||) ((?:\\w*\\d*)+)||)");
        patternConver = Pattern.compile("((?:\\w+\\d*)+)= (\\(\\w+\\)) ((?:\\w*\\d*)+)");
        patternIf = Pattern.compile("(if(?:false|)) ((?:\\w+\\d*)+)(?:(?:(<=|>=|<|>|==|!=)((?:\\w+\\d*)+))|) then goto (L\\d+)");
        patternReturn = Pattern.compile("return ((?:\\w*\\d*)+)");
        patternParams = Pattern.compile("params ((?:\\w*\\d*)+)");

        tableID = TableID.get();
        ArrayList<String> regs = new ArrayList<>();
        regs.add("eax");
        regs.add("ebx");
        regs.add("ecx");
        regs.add("edx");
        //regs.add("eax:edx");
        //regs.add("ecx:ebx");
        registerDectructor = new RegisterDectructor(regs);

        this_method_name = "global";
        inst_new_double = new ArrayList<>();

    }

    public void assembler() {

        if (stop){
            System.out.println("stop");
            return;
        }

        System.out.println("\t\t\t\t\t\t\t\t\tTABLE START\t\t");
        System.out.println("\t static Table");
        for (TableID.TableElement tableElement : TableID.get()) {
            System.out.println(tableElement);
            if (tableElement.tableID != null) {
                System.out.println("\t local Table "+tableElement.id);

                for (Iterator<TableID.TableElement>tableElementIterator=tableElement.tableID.thisIterator();tableElementIterator.hasNext();) {
                    System.out.println(tableElementIterator.next());
                }

                System.out.println("\t staticTable");
            }
        }
        System.out.println("\t\t\t\t\t\t\t\t\tTABLE END\t\t");

        LinkedList<BaseBloc> blocs = blocs();
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\tBase Blocs");
        System.out.println(blocs);
        Graph graph = graph(blocs);
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\tGraph");
        System.out.println(graph);
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\tList");
        System.out.println(graph.linkedList());
        /*System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\tInf");
        inf(blocs);
        for (BaseBloc bloc : blocs) {
            System.out.println("bloc: " + bloc);
            for (BaseBloc.Element element : bloc.getStrings()) {
                System.out.println("el: " + element);
                System.out.println("inf: " + element.infs);
            }
        }*/
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\tGen");
        gen(blocs);
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int i = 0;
            System.out.println("nasm");
            Process nasm = Runtime.getRuntime().exec("nasm -felf "+pathDir+"Asm.asm -o "+pathDir+"Asm.o");
            i = nasm.waitFor();
            System.out.println(i);
            nasm.destroy();
            System.out.println("gccAPI");
            Process gccAPI = Runtime.getRuntime().exec("gcc -m32 -c APIC++.cpp -o APIC++.o");
            i = gccAPI.waitFor();
            System.out.println(i);
            gccAPI.destroy();
            System.out.println("gcc");
            Process gcc = Runtime.getRuntime().exec("gcc -m32 -o "+pathDir+nameFile+" APIC++.o"+" "+pathDir+"Asm.o");
            i = gcc.waitFor();
            System.out.println(i);
            gcc.destroy();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            logOut.flush();
            //logOut.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private TableID.TableElement getElement(String group,TableID tableID){
        Word id = new Word(TypeToken.ID, group), temp = new Word(TypeToken.TEMP, group);
        TableID.TableElement tableElementID = tableID.get(id);
        TableID.TableElement tableElementTemp = tableID.get(temp);
        return tableElementID != null ? tableElementID : tableElementTemp;
    }

    /*
    private void inf(LinkedList<BaseBloc> blocs) {
        Pattern patternTable = Pattern.compile("method_(\\w+\\d*):");
        TableID tableID = TableID.get();


        LinkedList<TableID> tableIDS = new LinkedList<>();
        Iterator<BaseBloc> baseBlocIteratorTableID = blocs.descendingIterator();
        for (BaseBloc baseBloc;baseBlocIteratorTableID.hasNext();) {
            baseBloc = baseBlocIteratorTableID.next();
            Matcher matcher1 = patternTable.matcher(baseBloc.getStrings().getFirst().string);

            if (matcher1.matches()) {
                String group = matcher1.group(1);
                if (group != null) {
                    TableID.TableElement tableElement = tableID.get(new Word(TypeToken.ID, group));
                    tableIDS.add(tableElement.tableID);
                }
            }
        }


        Iterator<BaseBloc> baseBlocIterator = blocs.descendingIterator();
        for (BaseBloc baseBloc;baseBlocIterator.hasNext();){
            baseBloc = baseBlocIterator.next();
            Iterator<BaseBloc.Element> elementIterator = baseBloc.getStrings().descendingIterator();

            Matcher matcher1 = patternTable.matcher(baseBloc.getStrings().getFirst().string);

            if (matcher1.matches()) {
                String group = matcher1.group(1);
                if (group != null) {
                    TableID.TableElement tableElement = tableID.get(new Word(TypeToken.ID, group));
                    tableID = tableElement.tableID;
                }
            }

            for (BaseBloc.Element el; elementIterator.hasNext(); ) {
                el = elementIterator.next();

                String str = el.string;
                Matcher matcher = patternArefmetical.matcher(str);

                if (matcher.matches()) {
                    //System.out.println();
                    TableID.TableElement element = getElement(matcher.group(1), tableID);
                    el.infs.add(element.inf.clone());
                    element.inf.used = false;
                    for (int i = 3; i <= 7; i += 4) {
                        String group = matcher.group(i);
                        if (group != null) {
                            //System.out.print(group+" ");
                            TableID.TableElement tableElement = getElement(group, tableID);

                            if (tableElement != null) {
                                el.infs.add(tableElement.inf.clone());
                                tableElement.inf.used = true;
                            }
                        }
                    }
                } else {
                    System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+str);
                }

                System.out.println(el);
                System.out.println(el.infs);

            }
        }

        //test(blocs);
    }
    private void test(LinkedList<BaseBloc>baseBlocs) {
        System.out.println("test");
        TableID tableID = TableID.get();
        Pattern patternTable = Pattern.compile("method_(\\w+\\d*):");

        for (BaseBloc baseBloc : baseBlocs) {
            System.out.println("bloc: " + baseBlocs);

            Matcher matcher1 = patternTable.matcher(baseBloc.getStrings().getFirst().string);
            if (matcher1.matches()) {
                String group = matcher1.group(1);
                if (group != null) {
                    TableID.TableElement tableElement = tableID.get(new Word(TypeToken.ID, group));
                    tableID = tableElement.tableID;
                }
            }

            for (BaseBloc.Element element : baseBloc.getStrings()) {
                String str = element.string;
                Matcher matcherAref = patternArefmetical.matcher(str);;

                if (matcherAref.matches()) {
                    //System.out.println();
                    TableID.TableElement tabElement = getElement(matcherAref.group(1), tableID);
                    tabElement.inf = element.infs.pollLast();
                    System.out.println("inf: " + tabElement.inf);
                    for (int i = 3; i <= 7; i += 4) {
                        String group = matcherAref.group(i);
                        if (group != null) {
                            //System.out.print(group+" ");
                            TableID.TableElement tableElement = getElement(group, tableID);

                            if (tableElement != null) {
                                tableElement.inf = element.infs.pollLast();
                                System.out.println("inf: " + tableElement.inf);
                            }
                        }
                    }
                }
            }
        }
    }
    */

    private LinkedList<BaseBloc> blocs() {

        String str;
        LinkedList<BaseBloc> baseBlocs = new LinkedList<>();
        BaseBloc baseBloc = new BaseBloc();

        BaseBloc.Element el = new BaseBloc.Element();
        el.string = getLine();
        baseBloc.getStrings().add(el);

        while ((str = getLine()) != null) {

            if (patternLable.matcher(str).matches()) {
                if (!baseBloc.isEmpty()) {
                    baseBlocs.add(baseBloc);
                }
                baseBloc = new BaseBloc();
            }
            if (patternIf.matcher(str).matches()) {
                BaseBloc.Element e = new BaseBloc.Element();
                e.string = str;
                baseBloc.getStrings().add(e);

                baseBlocs.add(baseBloc);
                baseBloc = new BaseBloc();
                continue;
            }
            if (patternCall.matcher(str).matches()) {
                BaseBloc.Element e = new BaseBloc.Element();
                e.string = str;
                baseBloc.getStrings().add(e);

                baseBlocs.add(baseBloc);
                baseBloc = new BaseBloc();
                continue;
            }
            BaseBloc.Element e = new BaseBloc.Element();
            e.string = str;
            baseBloc.getStrings().add(e);
        }
        if (!baseBloc.isEmpty()) {
            baseBlocs.add(baseBloc);
        }
        return baseBlocs;
    }

    private Graph graph(LinkedList<BaseBloc> blocs) {

        for (BaseBloc baseBloc : blocs) {
            String last = baseBloc.getStrings().getLast().string;
            if (patternIf.matcher(last).matches() || patternCall.matcher(last).matches()) {
                Pattern pattern;
                Matcher matcher1;
                String lable;
                if (patternIf.matcher(last).matches()) {
                    pattern = patternIf;
                    matcher1 = pattern.matcher(last);
                    matcher1.matches();
                    lable = matcher1.group(5);
                } else {
                    pattern = patternCall;
                    matcher1 = pattern.matcher(last);
                    matcher1.matches();
                    lable = matcher1.group(2);
                }

                for (BaseBloc baseBloc2 : blocs) {

                    String first = baseBloc2.getStrings().getFirst().string;
                    Matcher matcher = patternLable.matcher(first);
                    if (matcher.matches()) {
                        String lable2;
                        if (matcher.group(2) == null) {
                            lable2 = matcher.group(4);
                        } else {
                            lable2 = matcher.group(2);
                        }

                        if (lable.equals(lable2)) {
                            baseBloc.getBlocsOut().add(baseBloc2);
                            baseBloc2.getBlocsIn().add(baseBloc);
                            break;
                        }
                    }
                }
            }
                BaseBloc left = null;
                for (BaseBloc baseBloc3 : blocs) {
                    if (baseBloc3.equals(baseBloc)) {
                        if (left == null) {
                            break;
                        }
                        left.getBlocsOut().add(baseBloc3);
                        baseBloc.getBlocsIn().add(left);
                        break;
                    } else {
                        left = baseBloc3;
                    }
                }

        }

        return new Graph(null, null, blocs.getFirst());
    }

    private String getLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void gen(LinkedList<BaseBloc> blocs) {

        write("global main\n" +
                "\n" +
                "extern exit\n" +
                "extern malloc\n" +
                "extern printf\n" +
                "extern puts\n" +
                "extern gets\n" +
                "extern scanf\n" +
                "extern i_c\n" +
                "extern c_i\n" +
                "extern sub_i\n" +
                "extern add_i\n" +
                "extern div_i\n" +
                "extern mul_i\n" +
                "extern sub_d\n" +
                "extern add_d\n" +
                "extern div_d\n" +
                "extern mul_d\n" +
                "extern i_f\n" +
                "extern f_i\n"+
                "extern in_i\n"+
                "extern in_c\n"+
                "extern in_d\n");

        write(";Сегмент данных\n" +
                "section .data");

        write(";out_in_formats");
        write("format_c: db \"%c\",10,0\n" +
                "format_s: db \"%s\",10,0\n" +
                "format_i: db \"%d\",10,0\n" +
                "format_d: db \"%f\",10,0\n");
        write("format_in_c: db \"%1s\",10,0\n" +
                "format_in_s: db \"%s\",10,0\n" +
                "format_in_i: db \"%d\",10,0\n" +
                "format_in_f: db \"%f\",10,0\n");

        Iterator<TableID.TableElement> tableElementIterator = tableID.thisIterator();
        for (TableID.TableElement element;tableElementIterator.hasNext();) {
            element = tableElementIterator.next();

            if (element.staticAddress != null) {
                if (element.size == 8) {
                    write(element.staticAddress + " resq 1");
                } else {
                    write(element.staticAddress + " resd 1");
                }
            }
        }

        write(";Сегмент кода:\n" +
                "section .text\n" +
                "\n" +
                ";Функция main:\n" +
                "main:\n" +
                ";;\n");

        write("push ebp\n" +
                "mov ebp, esp\n" +
                "sub esp," + 128+'\n');

        LinkedList<BaseBloc> linkedList  = new LinkedList();

        Iterator<BaseBloc> iterator = blocs.iterator();
        BaseBloc baseBloc = null;
        while (iterator.hasNext()) {
            baseBloc = iterator.next();
            Matcher matcherLable = patternLable.matcher(baseBloc.getStrings().getFirst().string);
            if (matcherLable.matches()) {
                if (matcherLable.group(2) != null) {
                    linkedList.add(baseBloc);
                } else {
                    method(null, linkedList);
                    linkedList.clear();
                    break;
                }
            } else {
                linkedList.add(baseBloc);
            }
        }

        write(";;\n" +
                "push 0;код завершення\n" +
                "call exit;завершення\n");

        //
        write(";data start method "+this_method_name);
        for (String s : inst_new_double) {
            //System.out.println("static variable "+s);
            write(s);
        }
        write(";data end "+this_method_name);
        inst_new_double.clear();
        //

        if (linkedList.isEmpty() && baseBloc != null) {
            linkedList.add(baseBloc);
            Matcher matcherLable = patternLable.matcher(baseBloc.getStrings().getFirst().string);
            matcherLable.matches();
            while (iterator.hasNext()) {
                baseBloc = iterator.next();
                Matcher matcherLableNext = patternLable.matcher(baseBloc.getStrings().getFirst().string);
                 if (matcherLableNext.matches()) {
                    if (matcherLableNext.group(3) != null) {
                        method(matcherLable.group(5), linkedList);
                        linkedList.clear();
                        linkedList.add(baseBloc);
                        matcherLable = matcherLableNext;
                    } else {
                        linkedList.add(baseBloc);
                    }
                } else {
                    linkedList.add(baseBloc);
                }
            }
            method(matcherLable.group(5), linkedList);
            linkedList.clear();

        } else  {
            method(null, linkedList);
            linkedList.clear();
        }

    }

    private void method(String nameMethod,LinkedList<BaseBloc>baseBlocs) {
        ArrayList<TableID.TableElement> args = new ArrayList<>();

      //  System.out.println("name "+nameMethod);
        if (nameMethod != null) {
            TableID.TableElement tableElement = tableID.get(new Word(TypeToken.ID, nameMethod));
            tableID = tableElement.tableID;

            this_method_name = nameMethod;

           // System.out.println(nameMethod);
           // System.out.println(baseBlocs);


            //memmoty new (local temp)
            int sizeMemmory =0;
            Iterator<TableID.TableElement> tableElementIterator = tableID.thisIterator();
            for (TableID.TableElement tabE;tableElementIterator.hasNext();) {
                tabE = tableElementIterator.next();
                if (tabE.address * -1 >= 0) {
                    sizeMemmory += tabE.size;
                    //System.out.println(tabE);
                    //System.out.println(sizeMemmory);
                }
            }
            write(new String("method_"+nameMethod+":" + "\nnop"));

            write("push ebp\n" +
                    "mov ebp, esp\n" +
                    "sub esp," + sizeMemmory+'\n');

        }


        for (BaseBloc baseBloc : baseBlocs) {
          //  System.out.println("for "+baseBlocs);
            for (BaseBloc.Element element : baseBloc.getStrings()) {
                String str = element.string;
                Matcher matcherCall = patternCall.matcher(str);
                Matcher matcherGoto = patternGoto.matcher(str);
                Matcher matcherAref = patternArefmetical.matcher(str);
                Matcher matcherIf = patternIf.matcher(str);
                Matcher matcherLable = patternLable.matcher(str);
                Matcher matcherReturn = patternReturn.matcher(str);
                Matcher matcherParams = patternParams.matcher(str);
                Matcher matcherConvert = patternConver.matcher(str);

                /*
                if (matcherAref.matches()) {
                    //System.out.println();
                    TableID.TableElement tabElement = getElement(matcherAref.group(1), tableID);
                    tabElement.inf = element.infs.pollLast();
                    for (int i = 3; i <= 7; i+=4) {
                        String group = matcherAref.group(i);
                        if (group != null) {
                            //System.out.print(group+" ");
                            TableID.TableElement tableElement = getElement(group, tableID);

                            if (tableElement != null) {
                                tableElement.inf=element.infs.pollLast();
                            }
                        }
                    }
                }*/


                //------------------------------------------------------------------------------------------------------
                if (matcherParams.matches()) {
                    String idS = matcherParams.group(1);
                    TableID.TableElement idE = getElement(idS, tableID);

                    args.add(idE);

                } else if (matcherCall.matches()) {

                    String rezStr = matcherCall.group(1);
                    String name = matcherCall.group(2);
                    int k = Integer.parseInt(matcherCall.group(4));
                    TableID.TableElement rez = null;
                    if (rezStr != null) {
                        rez = tableID.get(new Word(TypeToken.TEMP, rezStr));
                    }

                    TableID.TableElement arg[] = new TableID.TableElement[k];
                    for (int i = 0; i < k; i++) {
                        arg[i] = args.get(args.size() - 1 - i);
                    }

                    call(name, rez, arg);

                } else if (matcherReturn.matches()) {
                    String idS = matcherReturn.group(1);
                    TableID.TableElement idE = getElement(idS, tableID);

                    BaseType baseType = idE.id.getBaseType();

                    write(";\t return");


                    if (baseType.equals(BaseType.VOID)) {
                        //emptry
                    } else if (baseType.equals(BaseType.REAL)) {
                        String reg = registerDectructor.reg(idE);

                        if (reg != null) {
                            clearReg(reg);
                        }

                         if (idE.staticAddress != null) {
                            write("fld qword["+idE.staticAddress+"]");
                        } else {
                             write("fld qword[ebp+"+idE.address+"]");
                        }

                    } else {
                        String reg = registerDectructor.reg(idE);
                        if (reg != null) {
                            if (reg.equals("eax")) {
                                continue;
                            } else {
                                clearReg("eax");
                                write("mov eax, " + reg);
                                registerDectructor.add("eax", idE);
                            }
                        } else if (idE.staticAddress != null) {
                            clearReg("eax");
                            write("mov eax, [" + idE.staticAddress + "]");
                            registerDectructor.add("eax", idE);
                        } else {
                            clearReg("eax");

                            write("mov eax, [ebp+" + idE.address + "]");

                            registerDectructor.add("eax", idE);
                        }
                    }

                    write("jmp method_"+nameMethod+"_exit");

                } else if (matcherConvert.matches()) {
                    TableID.TableElement rez = getElement(matcherConvert.group(1), tableID);
                    String type = matcherConvert.group(2);
                    TableID.TableElement a = getElement(matcherConvert.group(3), tableID);

                    if (type.equals("(int)")) {
                        if (a.id.getBaseType().equals(BaseType.REAL)) {
                            call("f_i", rez, a);
                        } else {
                            call("c_i", rez, a);
                        }
                    } else if (type.equals("(float)")) {
                        call("i_f", rez, a);
                    } else if (type.equals("(char)")) {
                        call("i_c", rez, a);
                    }
                } else if (matcherAref.matches()) {

                    TableID.TableElement rez = getElement(matcherAref.group(1), tableID);
                    TableID.TableElement rezIndex = getElement(matcherAref.group(2), tableID);
                    TableID.TableElement a = getElement(matcherAref.group(3), tableID);
                    TableID.TableElement aIndex = getElement(matcherAref.group(4), tableID);
                    TableID.TableElement b = getElement(matcherAref.group(7), tableID);

                    String op = matcherAref.group(6);

                   /* if (op != null && op.equals("-")) {
                        System.out.println(rez);
                        System.out.println(a);
                        System.out.println(op);
                        System.out.println(b);
                        System.out.println();
                    }*/



                    if (op == null) {
                        if (a == null) {
                            set(rez,null,matcherAref.group(3),rezIndex,null);
                        } else {
                            set(rez, null,a, rezIndex, aIndex);
                        }
                    } else {
                        if (a == null) {
                            op = op.equals("+") ? null : "-";
                            set(rez, op, b, rezIndex, null);
                        } else {
                            arefmetical(rez, a, b, op);
                        }
                    }
                } else if (matcherIf.matches()) {

                    String a = matcherIf.group(2);
                    String op = matcherIf.group(3);
                    String b = matcherIf.group(4);
                    String l = matcherIf.group(5);
                    String ymova = matcherIf.group(1);


                    for (int i = 0; i < 6; i++) {
                       // System.out.println(matcherIf.group(i));
                    }

                    /*System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);
                    call("sub_i",rez,a,b);
                    System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/

                    if (op == null) {
                        TableID.TableElement elementA = getElement(a, tableID);
                        String reg = "eax";
                        if (elementA != null) {
                            loadRegister(elementA, null, null, reg);
                        } else {
                            clearReg(reg);
                            if (a.equals("true")) {
                                write("mov " + reg + ", 01000010");//1 true
                            } else {
                                write("mov " + reg + ", 00000010");//0 false
                            }
                        }
                        write("sahf");

                        if (ymova.equals("iffalse")) {
                            write("jnz " + l);
                        } else {
                            write("jz " + l);
                        }

                    } else {
                       /* if (ymova.equals("iffalse")) {
                            String c = a;
                            a = b;
                            b = c;
                        }*/

                        TableID.TableElement elementA = getElement(a, tableID);
                        TableID.TableElement elementB = getElement(b, tableID);

                        //arefmetical(null, elementA, elementB, "-");

                       /* String registerA = loadRegister(elementA, null, null, null);
                        String registerB = loadRegister(elementB, null, registerA, null);
                        write("cmp " +registerA+ ", " + registerB);

                        System.out.println("regA: "+registerA);
                        System.out.println("regB: "+registerB);
                        System.out.println("des: ");
                        System.out.println(registerDectructor);*/

                        int sizeMemmory = 0;
                        if (elementA.id.getBaseType().equals(BaseType.REAL)) {
                            loadArg(elementA, elementB);

                            write("fld qword[esp]");
                            write("add esp,8");
                            write("fld qword[esp]");
                            write("add esp,8");

                            write("fcompp\n" +
                                    "mov eax, 0\n" +
                                    "fstsw ax\n" +
                                    "sahf");
                        } else {
                            sizeMemmory = loadArg(elementA, elementB);
                            write("call sub_i");
                        }


                        clearDestructor();//block end temp delete

                        if (op.equals("<")) {
                            if (ymova.equals("iffalse")) {
                                write("jge " + l);
                            } else {
                                write("jl " + l);
                            }
                        } else if (op.equals(">")) {
                            if (ymova.equals("iffalse")) {
                                write("jle " + l);
                            } else {
                                write("jg " + l);
                            }
                        } else if (op.equals(">=")) {
                            if (ymova.equals("iffalse")) {
                                write("jl " + l);
                            } else {
                                write("jge " + l);
                            }
                        } else if (op.equals("<=")) {
                            if (ymova.equals("iffalse")) {
                                write("jg " + l);
                            } else {
                                write("jle " + l);
                            }
                        } else if (op.equals("==")) {
                            if (ymova.equals("iffalse")) {
                                write("jnz " + l);
                            } else {
                                write("jz " + l);
                            }
                        } else if (op.equals("!=")) {
                            if (ymova.equals("iffalse")) {
                                write("jz " + l);
                            } else {
                                write("jnz " + l);
                            }
                        }
                        write("add esp, "+sizeMemmory);
                    }

                } else if (matcherLable.matches()) {
                    clearDestructor();//block start temp delete
                    String group = matcherLable.group(2);
                    if (group!=null)
                    write(new String(str + "\nnop"));
                } else if (matcherGoto.matches()) {
                    write("jmp "+matcherGoto.group(1));
                } else {
                    throw new RuntimeException(str);
                }
                //------------------------------------------------------------------------------------------------------
            }
        }

        if (nameMethod != null) {
            write("method_" + nameMethod + "_exit:");
            clearDestructor();//save methof blosk
            write("mov esp, ebp\n" +
                    "pop ebp\n" +
                    "ret");

            write(";data start method "+nameMethod);
            for (String s : inst_new_double) {
                write(s);
            }
            write(";data end "+nameMethod);
            inst_new_double.clear();
        }
    }

    //methods

    //reg1->reg2
    private void registerSend(TableID.TableElement element, String reg2) {
        String reg1 = element.Reg;//registerDectructor.reg(element);//not null
        BaseType baseType = element.id.getBaseType();

        /*if (baseType.equals(BaseType.REAL)) {
            String r1 = reg1.substring(0, 3), r2 = reg1.substring(4, 3), r3 = reg2.substring(0, 3), r4 = reg2.substring(4, 7);
            write("mov " + r3 + ", " + r1);
            write("mov " + r4 + ", " + r2);
        } else {*/
            write("mov " + reg2 + ", " + reg1);
        /*}*/

        registerDectructor.remove(element, reg1);
        registerDectructor.add(reg2, element);
    }
    private String loadRegister(TableID.TableElement element, TableID.TableElement index, String regNot, String regYes) {

        String regThis = element.Reg;//registerDectructor.reg(element);
        BaseType baseType = element.id.getBaseType();

        if (regThis != null && index == null) {
            if (regYes != null) {
                if (!regThis.equals(regYes)) {
                    clearReg(regYes);
                    registerSend(element, regYes);
                    return regYes;
                } else {
                    return regYes;//regThis
                }
            }

            if (regNot != null) {
                if (regThis.equals(regNot)) {
                    String register = getRegister(baseType, regNot);
                    registerSend(element, register);
                    return register;
                } else {
                    return regThis;
                }
            }

            return regThis;
        } else {
            String register;
            if (regThis != null) {
                register = regThis;
            } else if (regYes != null) {
                register = regYes;
                clearReg(register);
            } else {
               // System.out.println("loadreg tyt");
               // System.out.println(registerDectructor);

                //write("\t 0");
                register = getRegister(baseType, regNot);
                //write("\t 1");
            }
            //System.out.println("loadreg 0 ("+register+")");
            //System.out.println(registerDectructor);
            //cod
            if (baseType instanceof Reference) {
                if (regThis == null) {
                    if (element.staticAddress != null) {
                        write("mov " + register + ", [" + element.staticAddress + "]");
                    } else {
                        write("mov " + register + ", [ebp+" + element.address + "]");
                    }
                    registerDectructor.add(register, element);
                }

                if (index != null) {
                    String regIndex = index.Reg; //registerDectructor.reg(index);
                   /* saveReg(register,element);//tyt code
                    registerDectructor.remove(element, register);*/
                   //
                   LinkedList<TableID.TableElement> list = registerDectructor.reg(register);
                    for (TableID.TableElement element1 : list) {
                       saveReg(register,element);
                    }
                    for (TableID.TableElement element1 : list) {
                        registerDectructor.remove(element, register);
                    }
                    //
                    //test
                    registerDectructor.remove(element, register);
                    //
                    if (regIndex != null) {
                        write("add " + register + ", " + regIndex);
                    } else if (index.staticAddress != null) {
                        write("add " + register + ", [" + index.staticAddress + "]");
                    } else {
                        write("add " + register + ", [ebp+" + index.address + "]");
                    }
                }

               // System.out.println("loadreg 1");
               // System.out.println(registerDectructor);

                return register;
            } else if (baseType.equals(BaseType.REAL)) {
                /*String r1 = register;
                *//*String r1 = register.substring(0, 3), r2 = register.substring(4, 7);
                if (element.staticAddress != null) {
                    write("mov " + r1 + ", [" + element.staticAddress + "]");
                    write("mov " + r2 + ", [" + element.staticAddress + 4 + "]");
                } else {*//*
                    write("mov " + r1 + ", [ebp+" + element.address + "]");
                  //  if (element.address > 0) {
                        write("mov " + r2 + ", [ebp+" + element.address + "+4" + "]");
                  //  } else {
                  //      write("mov " + r2 + ", [ebp+" + element.address + -4 + "]");
                  //  }
                *//*}*/
            } else {
                if (element.staticAddress != null) {
                    write("mov " + register + ", [" + element.staticAddress + "]");
                } else {
                    write("mov " + register + ", [ebp+" + element.address + "]");
                }
            }
            //end
            registerDectructor.add(register, element);
            return register;
        }

    }
    private String getRegister(BaseType type,String regNot) {

        /*if (type.equals(BaseType.REAL)) {
            LinkedList<TableID.TableElement> r1 = registerDectructor.reg("eax:edx");
            LinkedList<TableID.TableElement> r2 = registerDectructor.reg("ecx:ebx");
            if (regNot != null) {

                if (regNot.equals("eax:edx")) {
                    if (!r2.isEmpty()) {
                        clearReg("ecx:ebx");
                    }
                    return "ecx:ebx";
                } else {
                    if (!r1.isEmpty()) {
                        clearReg("eax:edx");
                    }
                    return "eax:edx";
                }

            } else {

                if (r1.isEmpty()) {
                    return "eax:edx";
                }
                if (r2.isEmpty()) {
                    return "ecx:ebx";
                }

                if (r1.size() > r2.size()) {
                    clearReg("eax:edx");
                    return "eax:edx";
                } else {
                    clearReg("ecx:ebx");
                    return "ecx:ebx";
                }
            }
        } else {*/
            LinkedList<TableID.TableElement> r1 = registerDectructor.reg("eax");
            LinkedList<TableID.TableElement> r2 = registerDectructor.reg("edx");
            LinkedList<TableID.TableElement> r3 = registerDectructor.reg("ecx");
            LinkedList<TableID.TableElement> r4 = registerDectructor.reg("ebx");
            if (regNot != null) {

                if (r1.isEmpty()&&!regNot.equals("eax")) {
                   // System.out.println("this not");
                  return "eax";
                }
                if (r2.isEmpty()&&!regNot.equals("edx")) {
                    return "edx";
                }
                if (r3.isEmpty()&&!regNot.equals("ecx")) {
                    return "ecx";
                }
                if (r4.isEmpty()&&!regNot.equals("ebx")) {
                    return "ebx";
                }

                if (regNot.equals("eax")) {
                    clearReg("ecx");
                    return "ecx";
                } else {
                   // System.out.println("this regNot.equals(\"eax\")");

                   // System.out.println("0*****************************************************************************************************************************");
                   // System.out.println(registerDectructor);
                   // System.out.println("1*****************************************************************************************************************************");

                   // write("tyt 0");
                    clearReg("eax");
                    /*for (TableID.TableElement el : registerDectructor.reg("eax")) {
                        saveReg("eax",el);
                    }*/
                  //  write("tyt 1");

                   // System.out.println("2*****************************************************************************************************************************");
                  //  System.out.println(registerDectructor);
                  //  System.out.println("3*****************************************************************************************************************************");


                    return "eax";
                }

            } else {

                if (r1.isEmpty()) {
                   // System.out.println("this");
                    return "eax";
                }
                if (r2.isEmpty()) {
                    return "edx";
                }
                if (r3.isEmpty()) {
                    return "ecx";
                }
                if (r4.isEmpty()) {
                    return "ebx";
                }

                clearReg("eax");
                return "eax";
            }
        /*}*/
    }


    //end

    /*
    * Змінна в регістрі якшо її не ма значить вона в памяті в коректнгму значенні
    * бо вигружається в память лише при очишці регістра та деструктора якшо зміна є в регістрі то вона
    * не коректна в памяті швидше за всього
    * */

    private void clearReg(String reg) {
        clearReg(reg, null);
    }

    private void clearReg(String reg, TableID.TableElement element) {
        LinkedList<TableID.TableElement> ids = registerDectructor.reg(reg);
        LinkedList<TableID.TableElement> idsRemove = new LinkedList<>();

        //System.out.println("clear reg: "+reg);
        write(";\tclear reg: "+reg);
        for (TableID.TableElement idE : ids) {
            //  System.out.println("elm: "+idE);
            // System.out.println("elmInf: "+idE.inf);
            if (!(idE.id.getBaseType() instanceof Reference)) {
                //if live active
                //System.out.println(idE);
                // System.out.println(idE.inf);

              // if (idE.inf.live) {
                    //  if (true) {
                    if (element != null && idE.equals(element)) {
                        continue;
                    }
                    saveReg(reg, idE);
                /*} else {
                    System.out.println("blzt");
                }*/
            }
            idsRemove.add(idE);
        }

        for (TableID.TableElement idE : idsRemove) {
            registerDectructor.remove(idE, reg);
        }
        write(";");
    }

    private void saveReg(String reg,TableID.TableElement idE) {
        String r1 = null, r2 = null;
        r1 = reg;
        /*r1 = reg.substring(0, 3);
        if (reg.indexOf(':')!=-1)
            r2 = reg.substring(4, 7);*/
        if (idE.staticAddress != null) {
            /*if (reg.equals("eax:edx") || reg.equals("ecx:ebx")) {
                write("mov dword["+idE.staticAddress+"], "+r1);
                write("mov dword[" + idE.staticAddress + 4 + "], " + r2);
            } else {*/
                write("mov dword["+idE.staticAddress+"], "+r1);
            /*}*/
        } else {
            /*if (reg.equals("eax:edx") || reg.equals("ecx:ebx")) {
                write("mov dword[ebp+"+idE.address+"], "+r1);
                // if (idE.address > 0) {
                write("mov dword[ebp+" + idE.address + "+4"  + "], " + r2);
                // } else {
                //     write("mov dword[ebp+" + idE.address + -4 + "], " + r2);
                //  }
            } else {*/
                write("mov dword[ebp+"+idE.address+"], "+r1);
            /*}*/
        }
    }

    private void clearDestructor() {
        for (String reg : registerDectructor.getRegistrs()) {
            clearReg(reg);
        }
        registerDectructor.clear();
    }

    private void clearDestructorAll() {
       // System.out.println("clearDestructorAll");
        for (String reg : registerDectructor.getRegistrs()) {
            LinkedList<TableID.TableElement> reg1 = registerDectructor.reg(reg);
            //write("\treg_clear: "+reg);
            //write("\tvalue: "+reg1);
            for (TableID.TableElement element : reg1) {
                //write("\t clear_all0");
                saveReg(reg, element);
                //write("\t clear_all1");
            }
            /*System.out.println("size_clear "+ reg1);
            for (TableID.TableElement element : reg1) {
                System.out.println(element);
                registerDectructor.remove(element, reg);
            }*/
           // System.out.println("size_clear "+ reg1);
            while (reg1.size() != 0) {
                TableID.TableElement element = reg1.pollFirst();
                element.Reg = null;
            //    System.out.println(element);
            }
           // System.out.println(reg1);

        }
        registerDectructor.clear();
    }
    private void clearDestructorAllLocal() {
       // System.out.println("clearDestructorAllLocal");
        for (String reg : registerDectructor.getRegistrs()) {
            LinkedList<TableID.TableElement> reg1 = registerDectructor.reg(reg);

        //    System.out.println("size_clear_local "+ reg1);
            while (reg1.size() != 0) {
                TableID.TableElement element = reg1.pollFirst();
                element.Reg = null;
         //       System.out.println(element);
            }
         //   System.out.println(reg1);

        }
        registerDectructor.clear();
    }

    private void znak(TableID.TableElement element, TableID.TableElement index) {
    }

    public void set(TableID.TableElement rez, String zn, Object value, TableID.TableElement rezIndex, TableID.TableElement vIndex) {
        BaseType baseType = rez.id.getBaseType();

       // System.out.println("set do "+rez);
       // System.out.println(registerDectructor);

        if (value instanceof String) {
            String reg = rez.Reg;//registerDectructor.reg(rez);

            if (reg != null) {
                clearReg(reg, rez);
            }

            String str_addr = null;
            if (baseType.equals(BaseType.REAL)) {
                if (rezIndex == null) {
                    str_addr = "scope_"+this_method_name + "_" + rez.id.getId().getLex()+"_unique_"+unique;
                } else {
                    str_addr = "scope_"+this_method_name + "_" + rez.id.getId().getLex() + "_" + rezIndex.id.getId().getLex()+"_unique_"+unique;
                }
                unique++;
                write("fld qword[" + str_addr + "]");
                inst_new_double.add(str_addr+" dq "+value);
            }


            if (baseType instanceof Reference) {
                reg = loadRegister(rez, rezIndex, null, null);

                if (baseType.equals(BaseType.REAL)) {
                    write("fstp qword["+reg+"]");
                } else {
                    write("mov [" + reg + "], " + value);
                }
            } else if (baseType.equals(BaseType.REAL)) {
                /*if (reg != null) {
                    String r1 = reg.substring(0, 3), r2 = reg.substring(4, 7);
                    write("mov " + r1 + ", [" +str_addr+"]");
                    write("mov " + r2 + ", [" +str_addr+"+4"+"]");

                } else*/ if (rez.staticAddress != null) {
                    write("fstp qword["+rez.staticAddress+"]");
                } else {
                    write("fstp qword[ebp+"+rez.address+"]");
                  //  if (rez.address > 0) {
                  //  } else {
                  //      write("mov dword[ebp+" + rez.address + -4 + "], " + b);
                  //  }
                }
            } else if (baseType.equals(BaseType.BOO)) {
                if (value.equals("true")) {
                    value = "01000010";
                } else {
                    value = "00000010";
                }

                if (reg != null) {
                    write("mov " + reg + ", " + value);
                } else if (rez.staticAddress != null) {
                    write("mov dword[" + rez.staticAddress + "], " + value);
                } else {
                    write("mov dword[ebp+" + rez.address + "], " + value);
                }

            } else {
                if (reg != null) {
                    write("mov " + reg + ", " + value);
                } else if (rez.staticAddress != null) {
                    write("mov dword[" + rez.staticAddress + "], " + value);
                } else {
                    write("mov dword[ebp+" + rez.address + "], " + value);
                }
            }
        } else {
            //
            TableID.TableElement elementValue = (TableID.TableElement) value;
            String regV = elementValue.Reg;
            String regR = rez.Reg;

            if (rezIndex != null || vIndex != null) {
               // System.out.println("set array");


                regR = loadRegister(rez, rezIndex, null, null);
                regV = loadRegister(elementValue, vIndex, regR, null);

               // System.out.println(regR);
              //  System.out.println(regV);

                if (vIndex != null) {
                    write("mov " + regV + ",[" + regV + "]");
                }

                if (rezIndex != null) {
                    write("mov [" + regR + "], " + regV);
                } else {
                    write("mov " + regR + ", " + regV);
                }

               // System.out.println(registerDectructor);

            } else {
                if (regV == null) {
                    regV = loadRegister(elementValue, null, null, null);
                }
              //  System.out.println("regV "+regV+", "+elementValue);

                if (regR != null) {
                    registerDectructor.remove(rez, regR);
                }
                registerDectructor.add(regV,rez);

               // System.out.println("y tyt");
               /* System.out.println("status");
                System.out.println("rez "+rez);
                System.out.println(registerDectructor);*/
            }
        }

        if (zn != null) {
            znak(rez, rezIndex);
        }

        clearDestructorAll();

        //write("\tset 0");
        //clearDestructorAll();//tyt code
        //write("\tset 1");

       // System.out.println("set pisla");
      //  System.out.println(registerDectructor);
    }

    private void arefmetical( TableID.TableElement  rez,  TableID.TableElement  a,  TableID.TableElement  b, String op) {
        BaseType baseType;
        if (rez != null) {
            baseType = rez.id.getBaseType();
        } else {
            if (a != null) {
                baseType = a.id.getBaseType();
            } else {
                baseType = b.id.getBaseType();
            }
        }

        switch (op) {
            case "+":
                if (baseType.equals(BaseType.REAL)) {
                   /* System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("add_d",rez,a,b);
                   /* System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                } else {
                    /*System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("add_i",rez,a,b);
                  /*  System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                }
                break;
            case "-":
                if (baseType.equals(BaseType.REAL)) {
                   /* System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("sub_d",rez,a,b);
                   /* System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                } else {
                   /* System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("sub_i",rez,a,b);
             /*       System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                }
                break;
            case "*":
                if (baseType.equals(BaseType.REAL)) {
                   /* System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("mul_d",rez,a,b);
                /*    System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                } else {
                    /*System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("mul_i",rez,a,b);
                   /* System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                }
                break;
            case "/":
                if (baseType.equals(BaseType.REAL)) {
                    /*System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("div_d",rez,a,b);
                   /* System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                } else {
                   /* System.out.println("status do");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                    call("div_i",rez,a,b);
                   /* System.out.println("status pisla");
                    System.out.println("rez "+rez);
                    System.out.println(registerDectructor);*/
                }
                break;
        }
    }

    private void call(String name, TableID.TableElement rez, TableID.TableElement... args) {

        /*System.out.println("clear");
        System.out.println(registerDectructor);
        clearDestructorAll();
        System.out.println(registerDectructor);*/

        clearDestructorAll();

        //
        if (name.equals("method_out_c")) {
            write("pushad");
            int sizeMemmory = loadArg(args)+4;

            write("push dword format_c");
            write("call printf");
            write("add esp, " + sizeMemmory);

            write("popad");
            return;
        }
        if (name.equals("method_out_i")) {
            write("pushad");
            int sizeMemmory = loadArg(args)+4;

            write("push dword format_i");
            write("call printf");
            write("add esp, " + sizeMemmory);

            write("popad");
            return;
        }
        if (name.equals("method_out_d")) {
            write("pushad");
            int sizeMemmory = loadArg(args)+4;

            write("push dword format_d");
            write("call printf");
            write("add esp, " + sizeMemmory);

            write("popad");
            return;
        }
        if (name.equals("method_out_s")) {
            RegisterDectructor clone=registerDectructor.clone();
            registerDectructor.clear();
            write("pushad");

           // System.out.println("test");
           // System.out.println(registerDectructor);
           // System.out.println(args[0]);
            loadRegister(args[0], null, null, "ecx");

            write("mov eax, dword[ecx]");//[ ][][][][]
            write("mov edx, 4");//k
            write("mov ebx, ecx");//add
            write("mov ecx, eax");//k_llop
            write("size_"+unique+":\n" +
                    "add ebx, 4\n" +
                    "push dword[ebx]\n" +
                    "add esp, 4\n" +
                    "imul edx, dword[ebx]\n" +
                    "loop size_"+unique+"\n");
            unique++;
            registerDectructor.clear();
            args[0].Reg = null;


            loadRegister(args[0], null, null, "ecx");
            write("imul eax, 4");
            write("add eax, 4");
            write("add ecx, eax");//address messege



            write("mov eax, 4\n" +
                    "mov ebx, 1\n" +
                    "int 0x80\n");


            write("popad");
            registerDectructor.clear();
            args[0].Reg = null;
            registerDectructor = clone;
            return;
        }
        if (name.equals("method_new")) {
            RegisterDectructor clone=registerDectructor.clone();
            registerDectructor.clear();
            write("pushad");

            write("mov ecx, 4");
            for (TableID.TableElement element : args) {
                loadRegister(element, null, null, "eax");
                write("imul ecx, eax");
            }

            write("add ecx, " + (args.length * 4));


            write("push ecx");
            write("call malloc");
            write("add esp, 4");

            registerDectructor.clear();
            for (TableID.TableElement element : args) {
                element.Reg = null;
            }

            int i = 0;
            for (TableID.TableElement element : args) {
                loadRegister(element, null, null, "ecx");
                write("mov dword[eax+"+i+"], ecx");
                i += 4;
            }


           // System.out.println('0');
           // System.out.println(registerDectructor);
            registerDectructor.add("eax", rez);
            //write("\ttyt 0");
            //write(rez.toString());
            saveReg("eax", rez);
            //write("\ttyt 1");
            registerDectructor.remove(rez, "eax");
           // System.out.println('1');
           // System.out.println(registerDectructor);




            write("popad");
            registerDectructor = clone;
            return;
        }
        //in
        if (name.equals("method_in_c")) {
            write("pushad");
            clearDestructorAllLocal();
            int sizeMemmory = loadArgRef(args)+4;

            write("push dword format_in_c");
            write("call scanf");
            write("add esp, " + sizeMemmory);

            write("popad");
            return;
            /*write("pushad");
            clearDestructorAllLocal();

            write("call in_c");
            TableID.TableElement element = args[0];
            if (element.staticAddress != null) {
                write("mov dword["+element.staticAddress+"], eax");
            } else {
                write("mov dword[ebp+"+element.address+"], eax");
            }

            write("popad");
            return;*/
        }
        if (name.equals("method_in_i")) {
            write("pushad");
            clearDestructorAllLocal();
            int sizeMemmory = loadArgRef(args)+4;

            write("push dword format_in_i");
            write("call scanf");
            write("add esp, " + sizeMemmory);

            write("popad");
            return;
            /*write("pushad");
            clearDestructorAllLocal();

            write("call in_i");
            TableID.TableElement element = args[0];
            if (element.staticAddress != null) {
                write("mov dword["+element.staticAddress+"], eax");
            } else {
                write("mov dword[ebp+"+element.address+"], eax");
            }

            write("popad");
            return;*/
        }
        if (name.equals("method_in_d")) {
            /*write("pushad");
            clearDestructorAllLocal();
            int sizeMemmory = loadArgRef(args)+4;

            write("push dword format_in_d");
            write("call scanf");
            write("add esp, " + sizeMemmory);

            write("popad");
            return;*/
            write("pushad");
            clearDestructorAllLocal();

            write("call in_d");
            TableID.TableElement element = args[0];
            if (element.staticAddress != null) {
                write("fstp qword["+element.staticAddress+"]");
            } else {
                write("fstp qword[ebp+"+element.address+"]");
            }

            write("popad");
            return;
        }
        if (name.equals("method_in_s")) {
            /*RegisterDectructor clone=registerDectructor.clone();
            registerDectructor.clear();
            write("pushad");

            System.out.println("test");
            System.out.println(registerDectructor);
            System.out.println(args[0]);
            loadRegister(args[0], null, null, "ecx");

            write("mov eax, dword[ecx]");//[ ][][][][]
            write("imul eax, 4");
            write("add eax, 4");
            write("add ecx, eax");//address messege

            registerDectructor.clear();
            args[0].Reg = null;

            write("push ecx");
            write("push dword format_in_s");
            write("call scanf");
            write("add esp, " + 8);

            write("popad");
            registerDectructor = clone;
            return;*/
            RegisterDectructor clone=registerDectructor.clone();
            registerDectructor.clear();
            write("pushad");

           // System.out.println("test");
           // System.out.println(registerDectructor);
           // System.out.println(args[0]);
            loadRegister(args[0], null, null, "ecx");

            write("mov eax, dword[ecx]");//[ ][][][][]
            write("imul eax, 4");
            write("add eax, 4");
            write("add ecx, eax");//address messege

            registerDectructor.clear();
            args[0].Reg = null;

            write("push ecx");
            write("call gets");
            write("add esp, " + 4);

            write("popad");
            registerDectructor = clone;
            return;

        }
        //

        //clearDestructor();
       RegisterDectructor clone=registerDectructor.clone();
        write("pushad");

        int sizeMemmory = loadArg(args);

        registerDectructor.clear();


        write("call "+name);
        write("add esp, "+sizeMemmory);

        if (rez != null) {
            BaseType baseType = rez.id.getBaseType();
            if (baseType.equals(BaseType.REAL)) {
                if (rez.Reg != null) {//
                    //clearReg(rez.Reg);
                    clone.remove(rez,rez.Reg);
                    registerDectructor.remove(rez, rez.Reg);

                }
                if (rez.staticAddress != null) {
                    write("fstp qword["+rez.staticAddress+"]");
                } else {
                    write("fstp  qword[ebp+"+rez.address+"]");
                }

            } else {
               /* System.out.println('0');
                System.out.println(registerDectructor);
                registerDectructor.add("eax", rez);*/
                if (rez.Reg!=null)
                    clone.remove(rez,rez.Reg);
                saveReg("eax",rez);
                registerDectructor.remove(rez, "eax");
                /*System.out.println('1');
                System.out.println(registerDectructor);*/

            }
        }

        clearDestructorAll();

       // clearDestructor();
        write("popad");
        registerDectructor = clone;
    }

    private int loadArg(TableID.TableElement... args) {
        int sizeMemmory = 0;

        for (int i = args.length-1; i >=0 ; i--) {
            TableID.TableElement idE = args[i];

            sizeMemmory += idE.id.getBaseType().size();

            BaseType baseType = idE.id.getBaseType();
            //-------------------------------------
            if (baseType instanceof Reference) {
                String reg = registerDectructor.reg(idE);
                if (reg != null) {
                    write("push "+reg);
                } else if (idE.staticAddress != null) {
                    write("push dword [" + idE.staticAddress + "]");
                } else {
                    write("push dword [ebp+" + idE.address + "]");
                }
            } else if (baseType.equals(BaseType.REAL)) {
               /* String reg = registerDectructor.reg(idE);
                if (reg != null) {
                    String r1 = null, r2 = null;
                    r1 = reg.substring(0, 3);
                    r2 = reg.substring(4, 7);

                    write("push "+r2);
                    write("push "+r1);

                } else*/ if (idE.staticAddress != null) {
                    write("push dword [" + idE.staticAddress + "+4"  + "]");
                    // if (idE.address > 0) {
                    write("push dword [" + idE.staticAddress + "]");
                    // } else {
                    //     write("push dword [" + idE.staticAddress + -4 + "]");
                    //  }
                } else {
                    write("push dword [ebp+" + idE.address + "+4"  + "]");
                    // if (idE.address > 0) {
                    write("push dword [ebp+" + idE.address + "]");
                    // } else {
                    //     write("push dword [ebp+" + idE.address + -4 + "]");
                    // }
                }

            } else {
                String reg = registerDectructor.reg(idE);
                if (reg != null) {
                    write("push "+reg);
                } else if (idE.staticAddress != null) {
                    write("push dword [" + idE.staticAddress + "]");
                } else {
                    write("push dword [ebp+" + idE.address + "]");
                }
            }
            //-------------------------------------
        }
        return sizeMemmory;
    }

    private int loadArgRef(TableID.TableElement... args) {
        int sizeMemmory = 0;

        for (int i = args.length-1; i >=0 ; i--) {
            TableID.TableElement idE = args[i];

            sizeMemmory += 4;

            BaseType baseType = idE.id.getBaseType();
            //-------------------------------------
            if (baseType instanceof Reference) {
                String reg = registerDectructor.reg(idE);
                if (reg != null) {
                    write("push "+reg);
                } else if (idE.staticAddress != null) {
                    write("push " + idE.staticAddress);
                } else {
                    write("mov eax, "+idE.address);
                    write("mov ecx, ebp");
                    write("add eax, ecx");
                    write("push eax");
                }
            } else {
                String reg = registerDectructor.reg(idE);
                if (reg != null) {
                    clearReg(reg);
                }
                if (idE.staticAddress != null) {
                    write("push " + idE.staticAddress );
                } else {
                    write("mov eax, "+idE.address);
                    write("mov ecx, ebp");
                    write("add eax, ecx");
                    write("push eax");
                }
            }
            //-------------------------------------
        }
        return sizeMemmory;
    }

    private void write(String s) {
        try {
            writer.write(s);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
