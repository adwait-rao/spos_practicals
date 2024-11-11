import java.io.*;
import java.util.*;

class SymbolTableEntry {
    String symbol;
    int address;
    boolean isDefined;

    public SymbolTableEntry(String symbol, int address, boolean isDefined) {
        this.symbol = symbol;
        this.address = address;
        this.isDefined = isDefined;
    }
}

class LiteralTableEntry {
    String literal;
    int address;
    boolean isAssigned;

    public LiteralTableEntry(String literal, int address, boolean isAssigned) {
        this.literal = literal;
        this.address = address;
        this.isAssigned = isAssigned;
    }
}

public class Main {
    private Map<Integer, SymbolTableEntry> symbolTable = new HashMap<>();
    private Map<Integer, LiteralTableEntry> literalTable = new HashMap<>();
    private List<String> targetCode = new ArrayList<>();
    private int locationCounter = 0;

    public void loadSymbolTable(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        // Skip header
        reader.readLine();
        int index = 1;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            symbolTable.put(index++, new SymbolTableEntry(
                    parts[0],
                    Integer.parseInt(parts[1]),
                    parts[2].equals("Y")
            ));
        }
        reader.close();
    }

    public void loadLiteralTable(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        // Skip header
        reader.readLine();
        int index = 1;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            literalTable.put(index++, new LiteralTableEntry(
                    parts[0],
                    Integer.parseInt(parts[1]),
                    parts[2].equals("Y")
            ));
        }
        reader.close();
    }

    public void generateTargetCode(String intermediateFile, String outputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(intermediateFile));
        String line;

        while ((line = reader.readLine()) != null) {
            processIntermediateLine(line.trim());
        }
        reader.close();

        // Write target code to output file
        writeTargetCode(outputFile);
    }

    private void processIntermediateLine(String line) {
        if (line.isEmpty()) return;

        // Parse the intermediate code
        String[] parts = line.split("\\s+");

        // Check if it's an instruction
        if (parts[0].startsWith("(IS,")) {
            processInstruction(parts);
        }
        // Handle assembly directive
        else if (parts[0].startsWith("(AD,")) {
            processDirective(parts);
        }
    }

    private void processInstruction(String[] parts) {
        StringBuilder machineCode = new StringBuilder("+ ");

        // Get operation code
        String opcode = parts[0].substring(4, parts[0].length() - 1);
        machineCode.append(String.format("%02d", Integer.parseInt(opcode))).append(" ");

        // Process register if present
        if (parts.length > 1 && parts[1].startsWith("(") && parts[1].endsWith(")")) {
            String register = parts[1].substring(1, parts[1].length() - 1);
            machineCode.append(register).append(" ");
        }

        // Process operand (symbol or literal)
        if (parts.length > 2) {
            String operand = parts[2];
            if (operand.startsWith("(S,")) {
                // Symbol reference
                int symbolIndex = Integer.parseInt(operand.substring(3, operand.length() - 1));
                SymbolTableEntry symbol = symbolTable.get(symbolIndex);
                if (symbol != null) {
                    machineCode.append(String.format("%03d", symbol.address));
                }
            } else if (operand.startsWith("(L,")) {
                // Literal reference
                int literalIndex = Integer.parseInt(operand.substring(3, operand.length() - 1));
                LiteralTableEntry literal = literalTable.get(literalIndex);
                if (literal != null) {
                    machineCode.append(String.format("%03d", literal.address));
                }
            }
        }

        if (machineCode.toString().split(" ").length > 1) {
            targetCode.add(machineCode.toString());
        }
    }

    private void processDirective(String[] parts) {
        String directiveCode = parts[0].substring(4, parts[0].length() - 1);

        // Handle START directive
        if (directiveCode.equals("01") && parts.length > 1) {
            String startAddress = parts[1].substring(3, parts[1].length() - 1);
            locationCounter = Integer.parseInt(startAddress);
        }
    }

    private void writeTargetCode(String outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            for (String code : targetCode) {
                writer.println(code);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Main pass2 = new Main();

            // Load tables from Pass 1 output
            pass2.loadSymbolTable("symbol_table.txt");
            pass2.loadLiteralTable("literal_table.txt");

            // Generate target code
            pass2.generateTargetCode("intermediate_code.txt", "target_code.txt");

            System.out.println("Pass 2 completed successfully! Target code generated in target_code.txt");
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}