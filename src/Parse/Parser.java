package Parse;

import Parse.Expression.*;
import Parse.Instructions.*;
import Parse.Instructions.Call;
import Vocabulary.*;
import Vocabulary.Integer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by admin-iorigins on 14.03.17.
 */
public class Parser {
    private Lex lex;
    private Token token;

    private TableID tableID;
    private TableMethods tableMethods;

    private Stack<BaseType> returnTypeMethodStack;
    private Stack<Return> returnStack;
    private Stack<Boolean> whileStack;

    private int offset = 0;

    private int brace;

    public Parser(Lex lex){
        this.lex = lex;
        token = lex.getToken();

        TableID.push();
        tableID=TableID.get();

        TableMethods.push();
        tableMethods = TableMethods.get();

        returnTypeMethodStack = new Stack<>();
        returnStack = new Stack<>();
        whileStack = new Stack<>();
    }

    public static void main(String[] args) throws FileNotFoundException, BaseType.MismatchException, ParserException, BaseType.ConvertException {
      //  Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/main.txt")));
      // Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testArrayMain.txt")));
      //  Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testArray2.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testSetMain.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/firstProgram.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testDouble.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/aref32ok.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/aref64ok.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testArg.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testVariable.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/minANDmax.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/minANDmax64.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testBoolean.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testChar.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testOut.txt")));
      //    Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testNew.txt")));
          Parser parser = new Parser(new Lex(new FileInputStream("/home/iorigins/Стільниця/компілер/code/testIn.txt")));
        Program parse = null;
        try {
            System.out.println("parse");
            parse = parser.parse();
            System.out.println("typeChecking");
            parse.typeChecking();
            System.out.println("gen");

            //
            System.out.println("\t\t\t\t\t\t\t\t\tTABLE START do gen\t\t");

            System.out.println("\t staticTable");
            for (TableID.TableElement tableElement : parser.tableID) {
                System.out.println(tableElement);
                if (tableElement.tableID != null) {
                    System.out.println("\t localTable "+tableElement.id);
                    for (Iterator<TableID.TableElement>tableElementIterator=tableElement.tableID.thisIterator();tableElementIterator.hasNext();) {
                        System.out.println(tableElementIterator.next());
                    }

                    System.out.println("\t staticTable");
                }
            }

            System.out.println("\t\t\t\t\t\t\t\t\tTABLE END do gen\t\t");
            //

            parse.gen();

            System.out.println("\t\t\t\t\t\t\t\t\tTABLE START\t\t");

            System.out.println("\t staticTable");
            for (TableID.TableElement tableElement : parser.tableID) {
                System.out.println(tableElement);
                // System.out.println(tableElement+"\t\t\t\t\t\t"+tableElement.tableID);
                if (tableElement.tableID != null) {
                    System.out.println("\t localTable "+tableElement.id);

                    for (Iterator<TableID.TableElement>tableElementIterator=tableElement.tableID.thisIterator();tableElementIterator.hasNext();) {
                        System.out.println(tableElementIterator.next());
                       // System.out.println(tableElementIterator.next()+"\t\t\t\t\t\t"+tableElement.tableID);
                    }

                    System.out.println("\t staticTable");
                }
            }

            System.out.println("\t\t\t\t\t\t\t\t\tTABLE END\t\t");

        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    public static class ParserException extends Exception {
        private Token tokenExpected, tokenReceived;

        public ParserException(Token tokenExpected, Token tokenReceived) {
            this.tokenExpected = tokenExpected;
            this.tokenReceived = tokenReceived;
        }

        public ParserException(String message, Token tokenExpected, Token tokenReceived) {
            super(message);
            this.tokenExpected = tokenExpected;
            this.tokenReceived = tokenReceived;
        }

        public Token getTokenExpected() {
            return tokenExpected;
        }

        public Token getTokenReceived() {
            return tokenReceived;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (tokenExpected != null) {
                builder.append("Expected " + tokenExpected);
            }
            if (tokenReceived != null) {
                builder.append("  Received " + tokenReceived);
            }

            if (getMessage() != null) {
                builder.append(" meessages " + getMessage());
            }

            return builder.toString();
        }
    }

    public Program parse() throws ParserException {
        ArrayList<Method> methods = new ArrayList<>();
        ArrayList<Instruction> descriptions = new ArrayList<>();
        Method method;
        Instruction description;
        do {
            if (match(TypeToken.ID)) {
                ID idCall = id();
                next(getToken(TypeToken.BracketA));
                Expression[] arg = arg();
                next(getToken(TypeToken.BracketB));
                next(getToken(TypeToken.Semicolon));
                Call call = call(idCall, arg);
                descriptions.add(call);
                continue;
            }


            BaseType type = type();
            if (match(TypeToken.MasA)) {
                type = arrayType(type);
            }
            ID id = newID(type);


            if (match(TypeToken.BracketA)) {

                TableID.push();
                tableID = TableID.get();

                //4 zmishenna (call) 4 ebp
                offset = 4+4;//arg register not stack

                TableID.TableElement tableElement = tableID.get(id.getId());
                tableElement.typeAddress = "Code";
                tableElement.tableID = tableID;


                next(getToken(TypeToken.BracketA));
                Expression[] arg = arg();
                next(getToken(TypeToken.BracketB));

                    ID ids[] = new ID[arg.length];
                    for (int i = 0; i < ids.length; i++) {
                        ids[i] = (ID) arg[i];
                    }
                    method = method(type, id,ids);
                    methods.add(method);

                    TableID.pop();
                    tableID = TableID.get();


            } else {
                TableID.TableElement tableElement = tableID.get(id.getId());
                tableElement.typeAddress = "Static";
              //  tableElement.typeAddress = "Stack";
                tableElement.size = id.getBaseType().size();
                tableElement.staticAddress = id.getId().getLex();
              //  tableElement.address = offset;incOffset(id.getBaseType());


                description = description(type, id);
                descriptions.add(description);
            }


            if (match(TypeToken.ID)) {
                ID idCall = id();
                next(getToken(TypeToken.BracketA));
                Expression[] arg = arg();
                next(getToken(TypeToken.BracketB));
                next(getToken(TypeToken.Semicolon));
                Call call = call(idCall, arg);
                descriptions.add(call);
                continue;
            }

        } while (match(TypeToken.BASE_TYPE));


        next(token);
        if (brace != 0) {
            throw new ParserException("Brace eroor", null, null);
        }

        return new Program(methods,descriptions);
    }

    private BaseType arrayType(BaseType baseType) throws ParserException {
        do {
            next(getToken(TypeToken.MasA));
            next(getToken(TypeToken.MasB));
            baseType = new Array(baseType);
        } while (match(TypeToken.MasA));

        return new Reference(baseType);
    }

    private BaseType type() throws ParserException {
        Token thisToken = token;
        next(getToken(TypeToken.BASE_TYPE));
        BaseType baseType = (BaseType) thisToken;
        return baseType;
    }

    private ID id() throws ParserException {
        Token thisToken = token;
        next(getToken(TypeToken.ID));

        Word word = (Word) thisToken;
        if (word.getLex().equals("out_c")) {
            return new ID(BaseType.CHAR, word);
        }
        if (word.getLex().equals("out_i")) {
            return new ID(BaseType.INT, word);
        }
        if (word.getLex().equals("out_d")) {
            return new ID(BaseType.REAL, word);
        }
        if (word.getLex().equals("out_s")) {
            return new ID(BaseType.CHAR, word);
        }
        if (word.getLex().equals("new")) {
            return new ID(BaseType.INT, word);
        }
        if (word.getLex().equals("in_c")) {
            return new ID(BaseType.CHAR, word);
        }
        if (word.getLex().equals("in_i")) {
            return new ID(BaseType.INT, word);
        }
        if (word.getLex().equals("in_d")) {
            return new ID(BaseType.REAL, word);
        }
        if (word.getLex().equals("in_s")) {
            return new ID(BaseType.CHAR, word);
        }

        ID id = tableID.get((Word) thisToken).id;
        if (id == null) {

            throw new ParserException("ID is not described", getToken(TypeToken.ID), thisToken);
        }
        return id;
    }

    private ID newID(BaseType baseType) throws ParserException {
        Token thisToken = token;
        next(getToken(TypeToken.ID));

        if (match(TypeToken.MasA)) {
            baseType = arrayType(baseType);
        }
        ID id = new ID(baseType, (Word) thisToken);
        if (tableID.isElement((Word) thisToken)) {
            throw new ParserException("double descriptions id", new Word(TypeToken.ID, id.getId().getLex()), baseType);
        }

        TableID.TableElement tableElement = new TableID.TableElement(id);
        tableID.put((Word) thisToken,tableElement);
        return id;

    }

    /*
    private ID[] argMethod() throws ParserException{

        if (match(TypeToken.BASE_TYPE)) {
            BaseType type = type();
            ID id = newID(type);

            TableID.TableElement tableElement = tableID.get(id.getId());
            tableElement.typeAddress = "Stack";
            tableElement.size = id.getBaseType().size() * 8;
            tableElement.address = offset;

            incOffset(id.getBaseType());


            if (match(TypeToken.Coma)) {
                next(getToken(TypeToken.Coma));
                ArrayList<ID> ids = new ArrayList<>();
                for (ID id1 : argMethod()) {
                    ids.add(id1);
                }

                if (ids.isEmpty()) {
                    throw new ParserException(new Word(TypeToken.ID,"id"), token);
                }

                ids.add(id);
                return ids.toArray(new ID[]{});
            }

            return new ID[]{id};
        }

        return new ID[]{};
    }

    private Expression[] argCall() throws ParserException {
        if (!match(TypeToken.BracketB)) {
            Expression exp = expression();
            if (match(TypeToken.Coma)) {
                next(getToken(TypeToken.Coma));
                ArrayList<Expression> expressions = new ArrayList<>();

                for (Expression expression : argCall()) {
                    expressions.add(expression);
                }

                if (expressions.isEmpty()) {
                    throw new ParserException(null, token);
                }

                expressions.add(exp);
                return expressions.toArray(new Expression[]{});
            }
            return new Expression[]{exp};
        }
        return new Expression[]{};
    }
    */

    private Expression[] arg() throws ParserException {
        if (match(TypeToken.BASE_TYPE)) {
            BaseType type = type();
            ID id = newID(type);

            TableID.TableElement tableElement = tableID.get(id.getId());
            tableElement.typeAddress = "Stack";
            tableElement.size = id.getBaseType().size();
            tableElement.address = offset;

            incOffset(id.getBaseType());


            if (match(TypeToken.Coma)) {
                next(getToken(TypeToken.Coma));
                ArrayList<ID> ids = new ArrayList<>();
                for (Expression id1 : arg()) {
                    ids.add((ID) id1);
                }

                if (ids.isEmpty()) {
                    throw new ParserException(new Word(TypeToken.ID,"id"), token);
                }

                ids.add(id);
                return ids.toArray(new ID[]{});
            }

            return new ID[]{id};
        } else if (!match(TypeToken.BracketB)) {
            Expression exp = expression();
            if (match(TypeToken.Coma)) {
                next(getToken(TypeToken.Coma));
                ArrayList<Expression> expressions = new ArrayList<>();

                for (Expression expression : arg()) {
                    expressions.add(expression);
                }

                if (expressions.isEmpty()) {
                    throw new ParserException(null, token);
                }

                expressions.add(exp);
                return expressions.toArray(new Expression[]{});
            }
            return new Expression[]{exp};
        }
        return new Expression[]{};

        //return new ID[]{};=|
    }


    private Method method(BaseType type,ID id,ID ids[]) throws ParserException {
        //incOffset(type);memmory retyrn type descruption parse
        // memmory args argMethod
        next(getToken(TypeToken.BraceA));
        returnTypeMethodStack.push(type);
        returnStack.push(null);
        Instruction inst = listInstruction();
        returnTypeMethodStack.pop();
        Return pop = returnStack.pop();
        next(getToken(TypeToken.BraceB));

        boolean b = pop == null ? false : true;
        Method method = new Method(type, id, ids, inst,b);
        tableMethods.put(method.id().getId(),method);

        //System.out.println(method);

        return method;
    }

    private Expression value() throws ParserException {
        Expression expression = null;
        if (match(TypeToken.ID)) {
            ID id = id();
            if (match(TypeToken.MasA)) {
                return accessArray(id);
            }

            if (match(TypeToken.BracketA)) {
                next(getToken(TypeToken.BracketA));
                Parse.Expression.Call call = new Parse.Expression.Call(call(id, arg()));
                next(getToken(TypeToken.BracketB));
                return call;
            }

            return id;
        }
        if (match(TypeToken.INT)) {
            expression = new Constant(BaseType.INT, token);
        }

        if (match(TypeToken.REAL)) {
            expression = new Constant(BaseType.REAL, token);
        }

        if (match(TypeToken.Char)) {
            expression = new Constant(BaseType.CHAR, token);
        }

        if (match(TypeToken.TRUE)||match(TypeToken.FALSE)) {
            expression = new Constant(BaseType.BOO, token);
        }

        if (expression == null) {
            throw new ParserException(null, token);
        }

        next(token);
        return expression;

    }

    private Expression exp1() throws ParserException {
        Expression expression = exp2();
        Token op = token;

        if (match(TypeToken.AND)) {
            next(op);
            expression= new And(expression, exp1());
        }
        if (match(TypeToken.OR)) {
            next(op);
            expression= new Or(expression, exp1());
        }
        if (match(TypeToken.NOT)) {
            next(op);
            expression= new Not(expression);
        }
        return expression;
    }

    private Expression exp2() throws ParserException {
        Expression expression=exp3();
        if (match(TypeToken.More) || match(TypeToken.Less) || match(TypeToken.More_Exactly) || match(TypeToken.Less_Exactly) || match(TypeToken.Exactly) || match(TypeToken.Not_Exactly)) {
            Expression expression2;
            Token op = token;
            next(token);
            expression2 = exp3();//5>5>5
            expression= new Rel(op, expression, expression2);
        }
        return expression;
    }

    private Expression exp3() throws ParserException {
        Expression expression = exp4();
        while (match(TypeToken.Addition) || match(TypeToken.Subtraction)) {
            Expression expression2;
            Token op = token;
            next(token);
            expression2 = exp3();
            expression = new ArefmetychniOperations(op, expression, expression2);
        }
        return expression;
    }

    private Expression exp4() throws ParserException {
        Expression expression = exp5();
        while (match(TypeToken.Multiplication) || match(TypeToken.Division)) {
            Expression expression2;
            Token op = token;
            next(token);
            expression2 = exp4();
            expression = new ArefmetychniOperations(op, expression, expression2);
        }
        return expression;
    }

    private Expression exp5() throws ParserException {
        if (match(TypeToken.Subtraction)||match(TypeToken.Addition)) {
            Expression expression;
            Token op = this.token;
            next(token);
            expression = exp5();
            return new UnaryArefmetychni(expression.getBaseType(), op, expression);
        }

        if (match(TypeToken.BracketA)) {
            Expression expression;
            next(getToken(TypeToken.BracketA));
            if (match(TypeToken.BASE_TYPE)) {
                BaseType type = type();
                next(getToken(TypeToken.BracketB));
                expression = new UnaryArefmetychni(type, new Word(TypeToken.Convert, "(" + type + ")"),exp1());
            } else {
                expression = exp1();
                next(getToken(TypeToken.BracketB));
            }
            return expression;
        }
        return value();
    }

    private Expression expression() throws ParserException {
        return exp1();
    }

    private Call call(ID id,Expression arg[]) throws ParserException {
        Call call = new Call(id, arg);
        return call;
    }

    private AccessArray accessArray(ID id) throws ParserException {

        int k = 1;
        int k2 = -1;

        BaseType baseType = ((Reference)id.getBaseType()).getBaseType();
        Expression expression = new Constant(BaseType.INT, new Integer(0));


        while (true) {

            next(getToken(TypeToken.MasA));
            Expression expressionIndex = expression();
            baseType = ((Array) baseType).getBaseType();
            next(getToken(TypeToken.MasB));
            k++;
            k2++;

          //  System.out.println(expression);
          //  System.out.println(expressionIndex);

            if (match(TypeToken.MasA)) {
                expression = new ArefmetychniOperations(getToken(TypeToken.Addition), expression,
                        new ArefmetychniOperations(getToken(TypeToken.Multiplication), expressionIndex,
                                new AccessArray(BaseType.INT, id, new Constant(BaseType.INT, new Integer(k2)))));
            } else {
                expression = new ArefmetychniOperations(getToken(TypeToken.Addition), expression,
                        expressionIndex);
            }
            if (!match(TypeToken.MasA)) {
             //   System.out.println(expression);

                expression = new ArefmetychniOperations(getToken(TypeToken.Addition), expression, new Constant(BaseType.INT, new Integer(k)));

                return new AccessArray(baseType,id, expression);
            }

        }
    }

    private Instruction instructionAssigned(ID id) throws ParserException {
        AccessArray accessArray = null;
        if (match(TypeToken.MasA)) {
            accessArray = accessArray(id);
        }

        next(getToken(TypeToken.Assigned));

        Expression expression = expression();

        Instruction inst1;
        if (accessArray == null) {
             inst1 = new Set(id, expression);
        } else {
             inst1 = new SetElement(expression, accessArray);
        }

        if (match(TypeToken.Assigned)) {
            next(getToken(TypeToken.Assigned));
            if (expression.getTypeExpression() != getToken(TypeToken.ID)) {
                throw new ParserException(getToken(TypeToken.ID), expression.getTypeExpression());
            }
            Instruction instruction = instructionAssigned((ID) expression);
            return new ListInstruction(instruction, inst1);
        }
        return inst1;
    }

    private Instruction description(BaseType type,ID id) throws ParserException {
        Instruction assigned = null;


            if (match(TypeToken.Assigned)) {
                assigned = instructionAssigned(id);
            } else {
                assigned = BaseType.assignedDefault(id);
            }


        if (match(TypeToken.Coma)) {
            next(getToken(TypeToken.Coma));
            ID id2 = newID(type);

            //2
            TableID.TableElement tableElement = tableID.get(id2.getId());
            tableElement.typeAddress = tableID.get(id.getId()).typeAddress;

            tableElement.size = id.getBaseType().size();

            if (tableElement.typeAddress.equals("Stack")) {
                TableID.TableElement lastTableElementStack = Parser.getLastTableElementStack(TableID.get());
                if (lastTableElementStack != null) {
                    tableElement.address = -(lastTableElementStack.address*-1 + tableElement.size);
                } else {
                    tableElement.address = tableElement.size * -1;
                }
                incOffset(id2.getBaseType());
            } else {
                tableElement.staticAddress = tableElement.id.getId().getLex();
            }
            /*
            //tableElement.typeAddress = "Stack";
            tableElement.size = id.getBaseType().size();
            //tableElement.address = -offset;
            TableID.TableElement lastTableElementStack = Parser.getLastTableElementStack(TableID.get());
            if (lastTableElementStack != null) {
                //tableElement.address = -(lastTableElementStack.address*-1 + lastTableElementStack.size);
                tableElement.address = -(lastTableElementStack.address*-1 + tableElement.size);
            } else {
                tableElement.address=-4;
            }
            incOffset(id2.getBaseType());
            */

            //System.out.println("\t\t"+id2+" "+tableElement);

            Instruction description = description(type,id2);
            return new ListInstruction(new Description(type, id, assigned), description);
        } else {
            next(getToken(TypeToken.Semicolon));
        }

        return new Description(id.getBaseType(), id, assigned);
    }

    private Instruction inst() throws ParserException {
        if (match(TypeToken.BASE_TYPE)) {
            BaseType type = type();
            ID id = newID(type);

            TableID.TableElement tableElement = tableID.get(id.getId());
            tableElement.size = id.getBaseType().size();
            tableElement.typeAddress = "Stack";
           // tableElement.address = -offset;
            TableID.TableElement lastTableElementStack = Parser.getLastTableElementStack(TableID.get());
            if (lastTableElementStack != null) {
                //tableElement.address = -(lastTableElementStack.address*-1 + lastTableElementStack.size);
                tableElement.address = -(lastTableElementStack.address*-1 + tableElement.size);
            } else {
                tableElement.address = tableElement.size * -1;
            }
            incOffset(id.getBaseType());


            return description(type,id);
        }
        if (match(TypeToken.ID)) {
            ID id = id();
            if (match(TypeToken.MasA) || match(TypeToken.Assigned)) {
                Instruction instruction = instructionAssigned(id);
                next(getToken(TypeToken.Semicolon));
                return instruction;
            }
            next(getToken(TypeToken.BracketA));
            Call call = call(id,arg());
            next(getToken(TypeToken.BracketB));
            next(getToken(TypeToken.Semicolon));
            return call;
        }
        if (match(TypeToken.IF)) {
            next(getToken(TypeToken.IF));next(getToken(TypeToken.BracketA));
            Expression expression = expression();
            next(getToken(TypeToken.BracketB));
            next(getToken(TypeToken.BraceA));
            //returnStack.push(null);
            ListInstruction listInst = listInstruction();
            Return aReturn = returnStack.pop();
            returnStack.push(null);// ok null
            next(getToken(TypeToken.BraceB));

            if (match(TypeToken.ELSE)) {
                next(getToken(TypeToken.ELSE));
                next(getToken(TypeToken.BraceA));
                //returnStack.push(null);
                ListInstruction listInst2 = listInstruction();
                Return aReturn1 = returnStack.pop();
                if (aReturn != null && aReturn1 != null) {
                    returnStack.push(aReturn);
                }
                next(getToken(TypeToken.BraceB));
                return new Else(expression, listInst, listInst2);
            }
            return new If(expression, listInst);
        }
        if (match(TypeToken.WHILE)) {
            next(getToken(TypeToken.WHILE));next(getToken(TypeToken.BracketA));
            Expression expression = expression();
            next(getToken(TypeToken.BracketB));
            next(getToken(TypeToken.BraceA));
            returnStack.push(null);
            whileStack.push(new Boolean(true));
            ListInstruction listInst = listInstruction();
            whileStack.pop();
            next(getToken(TypeToken.BraceB));
            return new While(expression, listInst);
        }
        if (match(TypeToken.BREAK)) {
            next(getToken(TypeToken.BREAK));
            if (whileStack.peek()) {
                next(getToken(TypeToken.Semicolon));
                return new Break();
            } else {
                throw new ParserException(null, (Word.BREAK));
            }
        }

        if (match(TypeToken.RETURN)) {
            next(getToken(TypeToken.RETURN));
            Expression expression = expression();
            BaseType type = returnTypeMethodStack.peek();
            if (returnStack.peek() == null) {
                returnStack.push(new Return(expression,type));
                next(getToken(TypeToken.Semicolon));
            } else {
                throw new ParserException(null, (Word.RETURN));
            }
            return new Return(expression,type);
        }
        return null;//empty inst
    }

    private ListInstruction listInstruction() throws ParserException{
        ListInstruction listInstruction = new ListInstruction();

            Instruction instruction = inst();
            while (instruction != null) {
                listInstruction.put(instruction);
                instruction = inst();
            }

        return listInstruction;
    }
    //------------------------------------------------------------------------------------------------------------------

    private void next(Token token) throws ParserException {
        if (!token.equals(this.token)) {
            throw new ParserException(token, this.token);
        }
        this.token = lex.getToken();
        if (token.equals(getToken(TypeToken.BraceA))) {
            brace++;
        } else if (token.equals(getToken(TypeToken.BraceB))) {
            brace--;
        }
    }

    private boolean match(int type) {
        return token.getType() == type;
    }

    private Token getToken(int id) {
        return new Token(id);
    }

    private void incOffset(BaseType baseType) {
        offset += baseType.size();
    }


    public static TableID.TableElement getLastTableElementStack(TableID tableID) {
        Iterator<TableID.TableElement> tableElementIterator = tableID.thisIterator();
        TableID.TableElement last = null;
        int addr = 0;
        for (TableID.TableElement thisE; tableElementIterator.hasNext();) {
            thisE = tableElementIterator.next();
            if (thisE.typeAddress.equals("Stack") && (thisE.address*-1) >= addr) {

                last = thisE;
                addr = thisE.address*-1;
            }
        }
       // if (last!=null)
        //System.out.println("\t\t"+last+" "+TableID.get());
        return last;
    }

}
