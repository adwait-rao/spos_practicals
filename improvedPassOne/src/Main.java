import java.io.*;
import java.util.*;

// Class to represent a Symbol Table entry
class SymbolTableEntry {
    String symbol;
    int address;
    boolean isDefined;

    public SymbolTableEntry(String symbol, int address, boolean isDefined) {
        this.symbol = symbol;
        this.address = address;
        this.isDefined = isDefined;
    }

    @Override
    public String toString() {
        return String.format("%-10s\t%-8d\t%-8s", symbol, address, isDefined ? "Y" : "N");
    }
}

// Class to represent a Literal Table entry
class LiteralTableEntry {
    String literal;
    int address;
    boolean isAssigned;

    public LiteralTableEntry(String literal, int address, boolean isAssigned) {
        this.literal = literal;
        this.address = address;
        this.isAssigned = isAssigned;
    }

    @Override
    public String toString() {
        return String.format("%-15s\t%-8d\t%-8s", literal, address, isAssigned ? "Y" : "N");
    }
}

public class Main {
    private static final Map<String, Integer> OPTAB = new HashMap<>();
    private static final Map<String, Integer> REGTAB = new HashMap<>();
    private static final Map<String, Integer> CONDTAB = new HashMap<>();
    private static final Map<String, Integer> ADTAB = new HashMap<>();

    private Map<String, SymbolTableEntry> symbolTable = new HashMap<>();
    private Map<String, LiteralTableEntry> literalTable = new HashMap<>();
    private List<String> intermediateCode = new ArrayList<>();
    private int locationCounter = 0;

    public Main() {
        initializeTables();
    }

    private void initializeTables() {
        // Initialize OPTAB
        OPTAB.put("STOP", 0);
        OPTAB.put("ADD", 1);
        OPTAB.put("SUB", 2);
        OPTAB.put("MULT", 3);
        OPTAB.put("MOVER", 4);
        OPTAB.put("MOVEM", 5);
        OPTAB.put("COMP", 6);
        OPTAB.put("BC", 7);
        OPTAB.put("DIV", 8);
        OPTAB.put("READ", 9);
        OPTAB.put("PRINT", 10);

        // Initialize REGTAB
        REGTAB.put("AREG", 1);
        REGTAB.put("BREG", 2);
        REGTAB.put("CREG", 3);
        REGTAB.put("DREG", 4);

        // Initialize CONDTAB
        CONDTAB.put("LT", 1);
        CONDTAB.put("LE", 2);
        CONDTAB.put("EQ", 3);
        CONDTAB.put("GT", 4);
        CONDTAB.put("GE", 5);
        CONDTAB.put("ANY", 6);

        // Initialize ADTAB
        ADTAB.put("START", 1);
        ADTAB.put("END", 2);
        ADTAB.put("ORIGIN", 3);
        ADTAB.put("EQU", 4);
        ADTAB.put("LTORG", 5);
    }

    public void pass1(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;

        while ((line = reader.readLine()) != null) {
            processLine(line.trim());
        }

        reader.close();
        handleLiterals(); // Process any remaining literals at the end
        writeOutput();
    }

    private void processLine(String line) {
        if (line.isEmpty()) return;

        String[] parts = line.split("\\s+");
        int index = 0;
        String label = null;

        // Check for label
        if (!parts[0].equals("START") && !OPTAB.containsKey(parts[0]) && !ADTAB.containsKey(parts[0])) {
            label = parts[0];
            symbolTable.put(label, new SymbolTableEntry(label, locationCounter, true));
            index++;
        }

        // Process operation
        String operation = parts[index];
        if (OPTAB.containsKey(operation)) {
            processInstruction(parts, index);
        } else if (ADTAB.containsKey(operation)) {
            processDirective(parts, index);
        }
    }

    private void processInstruction(String[] parts, int index) {
        String operation = parts[index];
        StringBuilder ic = new StringBuilder();
        ic.append("(IS,").append(String.format("%02d", OPTAB.get(operation))).append(") ");

        // Process operands
        if (index + 1 < parts.length) {
            if (REGTAB.containsKey(parts[index + 1])) {
                ic.append("(").append(REGTAB.get(parts[index + 1])).append(") ");
            }

            if (index + 2 < parts.length) {
                String operand = parts[index + 2];
                if (operand.startsWith("=")) { // Literal
                    processliteral(operand);
                    ic.append("(L,").append(literalTable.size()).append(")");
                } else { // Symbol
                    if (!symbolTable.containsKey(operand)) {
                        symbolTable.put(operand, new SymbolTableEntry(operand, 0, false));
                    }
                    ic.append("(S,").append(symbolTable.size()).append(")");
                }
            }
        }

        intermediateCode.add(ic.toString());
        locationCounter++;
    }

    private void processDirective(String[] parts, int index) {
        String directive = parts[index];
        StringBuilder ic = new StringBuilder();
        ic.append("(AD,").append(String.format("%02d", ADTAB.get(directive))).append(") ");

        if (directive.equals("START")) {
            if (index + 1 < parts.length) {
                locationCounter = Integer.parseInt(parts[index + 1]);
                ic.append("(C,").append(locationCounter).append(")");
            }
        } else if (directive.equals("LTORG")) {
            handleLiterals();
        }

        intermediateCode.add(ic.toString());
    }

    private void processliteral(String literal) {
        if (!literalTable.containsKey(literal)) {
            literalTable.put(literal, new LiteralTableEntry(literal, 0, false));
        }
    }

    private void handleLiterals() {
        for (Map.Entry<String, LiteralTableEntry> entry : literalTable.entrySet()) {
            if (!entry.getValue().isAssigned) {
                entry.getValue().address = locationCounter;
                entry.getValue().isAssigned = true;
                locationCounter++;
            }
        }
    }

    private void writeOutput() throws IOException {
        // Write Symbol Table
        try (PrintWriter writer = new PrintWriter("symbol_table.txt")) {
            writer.println("Symbol    \tAddress \tDefined");
            for (SymbolTableEntry entry : symbolTable.values()) {
                writer.println(entry);
            }
        }

        // Write Literal Table
        try (PrintWriter writer = new PrintWriter("literal_table.txt")) {
            writer.println("Literal        \tAddress \tAssigned");
            for (LiteralTableEntry entry : literalTable.values()) {
                writer.println(entry);
            }
        }

        // Write Intermediate Code
        try (PrintWriter writer = new PrintWriter("intermediate_code.txt")) {
            for (String code : intermediateCode) {
                writer.println(code);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Main assembler = new Main();
            assembler.pass1("input.asm");
            System.out.println("Pass 1 completed successfully!");
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }
}