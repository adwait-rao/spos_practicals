import java.util.*;
import java.io.*;
// Data structures for Macro Name Table (MNT)
class MNTEntry {
    String macroName;
    int mdtIndex;
    int paramCount;

    public MNTEntry(String name, int index, int params) {
        this.macroName = name;
        this.mdtIndex = index;
        this.paramCount = params;
    }
}

// Data structures for Macro Definition Table (MDT)
class MDTEntry {
    String instruction;
    int index;

    public MDTEntry(String inst, int idx) {
        this.instruction = inst;
        this.index = idx;
    }
}


public class Main {
    private ArrayList<MNTEntry> mnt;
    private ArrayList<MDTEntry> mdt;
    private ArrayList<String> intermediateCode;
    private int mdtPointer;
    private boolean insideMacro;

    public Main() {
        mnt = new ArrayList<>();
        mdt = new ArrayList<>();
        intermediateCode = new ArrayList<>();
        mdtPointer = 0;
        insideMacro = false;
    }

    // Pass I of Macro Processor
    public void passOne(String inputFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            ArrayList<String> macroDefinition = new ArrayList<>();
            String currentMacroName = "";
            int paramCount = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] tokens = line.split("\\s+");

                if (tokens[0].equals("MACRO")) {
                    insideMacro = true;
                    continue;
                }

                if (insideMacro) {
                    if (tokens[0].equals("MEND")) {
                        // Process the macro definition
                        processMacroDefinition(macroDefinition);
                        macroDefinition.clear();
                        insideMacro = false;
                    } else {
                        macroDefinition.add(line);
                        if (currentMacroName.isEmpty()) {
                            // First line after MACRO is the macro prototype
                            currentMacroName = tokens[0];
                            paramCount = countParameters(line);
                            // Add to MNT
                            mnt.add(new MNTEntry(currentMacroName, mdtPointer, paramCount));
                        }
                    }
                } else {
                    // Not inside macro definition, add to intermediate code
                    if (!tokens[0].equals("END")) {
                        intermediateCode.add(line);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMacroDefinition(ArrayList<String> definition) {
        for (String line : definition) {
            mdt.add(new MDTEntry(line, mdtPointer++));
        }
    }

    private int countParameters(String prototype) {
        int count = 0;
        for (char c : prototype.toCharArray()) {
            if (c == '&') {
                count++;
            }
        }
        return count;
    }

    // Save MNT to file
    public void saveMNTToFile(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("Macro Name\tMDT Index\tParameters\n");
            writer.write("----------------------------------------\n");
            for (MNTEntry entry : mnt) {
                writer.write(String.format("%-15s%-15d%d\n",
                        entry.macroName,
                        entry.mdtIndex,
                        entry.paramCount));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save MDT to file
    public void saveMDTToFile(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("Index\tInstruction\n");
            writer.write("----------------------------------------\n");
            for (MDTEntry entry : mdt) {
                writer.write(String.format("%d\t%s\n",
                        entry.index,
                        entry.instruction));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save Intermediate Code to file
    public void saveIntermediateCodeToFile(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (String line : intermediateCode) {
                writer.write(line + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main processor = new Main();
        processor.passOne("input.txt");
        processor.saveMNTToFile("mnt.txt");
        processor.saveMDTToFile("mdt.txt");
        processor.saveIntermediateCodeToFile("intermediate_code.txt");
    }
}