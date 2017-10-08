package MyClass;

import Parse.Parser;
import Vocabulary.BaseType;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin-iorigins on 03.03.17.
 */
public class Main {
    int mas[] = new int[get()];

    static {
        int a = -12345, b = -12345;
        int c = a * b;
       // System.out.println(c);

        if(2<2){
            System.out.println("<");
        }

        if(2>2){
            System.out.println(">");
        }
    }

    public static void main(String[] args) throws IOException, Parser.ParserException, BaseType.ConvertException, BaseType.MismatchException {
         Pattern patternLable,patternGoto,patternCall;

        double temp = 1234567.1234567891;
        double temp2 = 1234567.1234567892;
       /* System.out.println(temp);
        System.out.println(temp2);
        System.out.println(temp==temp2);*/

        patternLable = Pattern.compile("((L\\d+):)|((method_(\\w+\\d*)+):)");
        patternGoto = Pattern.compile("goto (L\\d+)");
        patternCall = Pattern.compile(".*call (method_\\w+\\d*),\\d+");


        //Pattern pattern =Pattern.compile("((?:\\w+\\d*)+)(?:(?:\\[((?:\\w*\\d*)+)\\])|)=(?: (?:(?:((?:\\w*\\d*\\.*\\d*)+)(?:(?:\\[((?:\\w*\\d*)+)\\])||))||(\\(\\w+\\)))||)(?:(?: (.)||) ((?:\\w*\\d*\\.*\\d*)+)||)");

        //Pattern.compile("((?:\\w+\\d*)+)=(?: (?:((?:\\w*\\d*)+)||(?:\\(\\w+\\)))||)(?:(?: (.)||) ((?:\\w*\\d*)+)||)");

        // Pattern pattern = Pattern.compile("(?:((?:\\w*\\d*)+)||(?:\\(\\w+\\)))||");

       // String str = "t3= t4";//1 3
       // String str = "r= (float) a0";//1 5 7
       // String str = "25";
      // String str = "t3= t2 + t1";//1 3 7
      // String str = "t3= 5 - 7";//1 3 7
      //  String str = "t3= + t1";//1 7
      // String str = "mas[5][5]= + t1";//1 7
      // String str = "mas[5][5]= mas[5][5]";//1 7
      //  String str = "mas[5][5]= mas[5][5] + t1";//1 3 7
      //  String str = "mas[5][5]= mas[5][t7] + t1";//1 3 7

      //  String str = "a= 5";//3
      //  String str = "a= - 5";//6 7
       // String str = "a= - b";//6 7
     //   String str = "a= mas[0][t5]";//3 4
     //   String str = "mas[0][t5]= a";//1 2 3
      // String str = "mas[t5]= 0";//1 2 3
      // String str = "mas[t5]= mas[t4]";//1 2 3 4
      // String str = "mas[t5]= 2.5";//1 2 3 4
      // String str = "mas= 2.5";//1 2 3 4
      // String str = "mas= 2.5 + 4.1";//1 2 3 4

        /*
        Pattern pattern =Pattern.compile("(if(?:false|)) ((?:\\w+\\d*)+)(?:(?:(<=|>=|<|>|==|!=)((?:\\w+\\d*)+))|(true|false)) then goto (L\\d+)");
        String str = "iffalse true then goto L2";//1 2 3 4
        System.out.println(pattern.matcher(str).matches());

        Matcher matcher = pattern.matcher(str);
        matcher.matches();
        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));
        System.out.println(matcher.group(4));
        System.out.println(matcher.group(5));
        System.out.println(matcher.group(6));
        System.out.println(matcher.group(7));

        System.out.println("test");

        double v = 2.525;
        double vv =(v % 1);
        double vvv = vv * 10e-5;
        int i = (int) vvv;

        System.out.println(vv);
        System.out.println(vvv);
        //System.out.println(i);
        System.out.println(get(2.525));
        */
       /* try
        {
            Process proc = Runtime.getRuntime().exec("nasm -felf /home/iorigins/Стільниця/компілер/code/aref.asm -o /home/iorigins/Стільниця/компілер/code/aref2.o");
            proc.waitFor();
            proc.destroy();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/

        Pattern pattern =Pattern.compile("goto (L\\d+)");
        String str = "goto L2";//1 2 3 4
        System.out.println(pattern.matcher(str).matches());

        Matcher matcher = pattern.matcher(str);
        matcher.matches();
        System.out.println(matcher.group(1));

        File file = new File("1/2/3");
        System.out.println(file.getParent());

    }

    public static  int get(double someNumber) {
        String s = String.valueOf(someNumber);
        return Integer.parseInt(s.substring(s.indexOf('.')+1));
    }

    int get(){
        return 0;}

}
