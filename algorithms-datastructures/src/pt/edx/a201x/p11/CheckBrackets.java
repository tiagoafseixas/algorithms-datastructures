package pt.edx.a201x.p11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Stack;

class Bracket {
    Bracket(char type, int position) {
        this.type = type;
        this.position = position;
    }

    boolean Match(char c) {
        if (this.type == '[' && c == ']')
            return true;
        if (this.type == '{' && c == '}')
            return true;
        if (this.type == '(' && c == ')')
            return true;
        return false;
    }

    char type;
    int position;
}

class CheckBrackets {

    private static boolean TEST_MODE_ON = true;

    private static void log(final String s) {
        if (TEST_MODE_ON == true) {
            System.out.println(s);
        }
    }

    static class CheckBracketsTests {
        static final void test() {
            curlyBRaceCloseNoMatch();
            squareBracketsMatch();
            squareBracketsAndCurlyBracesMatch();
            squareBracketsWithParenthesesInsideMatch();
            multipleParenthesesMatch();
            oneOpeningCurlyBraceNoMatch();
            textWithParenthesesAndSquareBracketsNoMatch();
        }

        static final void curlyBRaceCloseNoMatch() {
            final String testName = "curlyBRaceCloseNoMatch";
            log(">" + testName);

            final String s = "}";
            CheckBracketsService.check(s);

            log("<" + testName);
        }

        static final void squareBracketsMatch() {
            final String testName = "squareBracketsMatch";
            log(">" + testName);

            final String s = "[]";
            CheckBracketsService.check(s);

            log("<" + testName);
        }

        static final void squareBracketsAndCurlyBracesMatch() {
            final String testName = "squareBracketsAndCurlyBracesMatch";
            log(">" + testName);

            final String s = "{}[]";
            CheckBracketsService.check(s);

            log("<" + testName);
        }

        static final void squareBracketsWithParenthesesInsideMatch() {
            final String testName = "squareBracketsWithParenthesesInsideMatch";
            log(">" + testName);

            final String s = "[()]";
            CheckBracketsService.check(s);

            log("<" + testName);
        }

        static final void multipleParenthesesMatch() {
            final String testName = "multipleParenthesesMatch";
            log(">" + testName);

            final String s = "(())";
            CheckBracketsService.check(s);

            log("<" + testName);
        }

        static final void oneOpeningCurlyBraceNoMatch() {
            final String testName = "oneOpeningCurlyBraceNoMatch";
            log(">" + testName);

            final String s = "{";
            CheckBracketsService.check(s);

            log("<" + testName);
        }

        static final void textWithParenthesesAndSquareBracketsNoMatch() {
            final String testName = "textWithParenthesesAndSquareBracketsNoMatch";
            log(">" + testName);

            final String s = "foo(bar[i)";
            CheckBracketsService.check(s);

            log("<" + testName);
        }
    }

    static class CheckBracketsService {
        static final void check(final String text) {
            Stack<Bracket> opening_brackets_stack = new Stack<Bracket>();
            Bracket b;
            boolean success = true;
            int errorIndex = 0;
            for (int position = 0; position < text.length(); ++position) {
                char next = text.charAt(position);

                if (next == '(' || next == '[' || next == '{') {
                    opening_brackets_stack.add(new Bracket(next, position + 1));
                }

                if (next == ')' || next == ']' || next == '}') {
                    if(opening_brackets_stack.isEmpty()) {
                        success = false;
                        errorIndex = position + 1;
                        break;
                    }

                    b = opening_brackets_stack.pop();
                    if(false == b.Match(next)) {
                        success = false;
                        errorIndex = position + 1;
                        break;
                    }
                }
            }

            if(success && opening_brackets_stack.isEmpty()) {
                System.out.println("Success");
            } else  if (success && false == opening_brackets_stack.isEmpty()) {
                System.out.println(opening_brackets_stack.pop().position);
            } else {
                System.out.println(errorIndex);
            }
        }
    }

    public static void main(String[] args) throws IOException {

        if(TEST_MODE_ON) {
            CheckBracketsTests.test();
        } else {
            final InputStreamReader input_stream = new InputStreamReader(System.in);
            final BufferedReader reader = new BufferedReader(input_stream);
            final String text = reader.readLine();
            CheckBracketsService.check(text);
        }
    }
}
