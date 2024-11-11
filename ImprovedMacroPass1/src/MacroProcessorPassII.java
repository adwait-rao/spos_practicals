import java.io.*;
import java.util.*;

public class MacroProcessorPassII {
    private Map<String, MNTEntry> mnt;
    private List<MDTEntry> mdt;
    private List<String> intermediateCode;
    private List<String> finalCode;

    public MacroProcessorPassII(String mntFile, String mdtFile, String intermediateCodeFile) {
        mnt = new HashMap<>();
        mdt = new ArrayList<>();
        intermediateCode = new ArrayList<>();
        finalCode = new ArrayList<>();

        loadMNT(mntFile);
        loadMDT(mdtFile);
        loadIntermediateCode(intermediateCodeFile);
    }

    private void loadMNT(String mntFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mntFile));
            String line;
            reader.readLine(); // Skip header line
            reader.readLine(); // Skip separator line

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t", -1); // Use -1 to keep empty parts
                if (parts.length == 3) {
                    String macroName = parts[0];
                    int mdtIndex = Integer.parseInt(parts[1]);
                    int paramCount = Integer.parseInt(parts[2]);
                    mnt.put(macroName, new MNTEntry(macroName, mdtIndex, paramCount));
                } else {
                    // Ignore lines with an unexpected number of fields
                    System.out.println("Ignoring line with unexpected format: " + line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadMDT(String mdtFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mdtFile));
            String line;
            reader.readLine(); // Skip header line
            reader.readLine(); // Skip separator line

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                int index = Integer.parseInt(parts[0]);
                String instruction = parts[1];
                mdt.add(new MDTEntry(instruction, index));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadIntermediateCode(String intermediateCodeFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(intermediateCodeFile));
            String line;
            while ((line = reader.readLine()) != null) {
                intermediateCode.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void passTwo() {
        for (String line : intermediateCode) {
            String[] tokens = line.split("\\s+");
            if (mnt.containsKey(tokens[0])) {
                // This is a macro call
                expandMacro(tokens[0], tokens);
            } else {
                // This is a regular instruction
                finalCode.add(line);
            }
        }
    }

    private void expandMacro(String macroName, String[] tokens) {
        MNTEntry entry = mnt.get(macroName);
        int mdtIndex = entry.mdtIndex;
        int paramCount = entry.paramCount;

        // Extract macro parameters from the call
        List<String> params = new ArrayList<>();
        for (int i = 1; i <= paramCount; i++) {
            params.add(tokens[i]);
        }

        // Replace parameters in macro definition and add to final code
        for (int i = mdtIndex; i < mdt.size(); i++) {
            String instruction = mdt.get(i).instruction;
            for (int j = 0; j < paramCount; j++) {
                instruction = instruction.replace("&" + (j + 1), params.get(j));
            }
            finalCode.add(instruction);
            if (mdt.get(i).instruction.equals("MEND")) {
                break;
            }
        }
    }

    public void saveFinalCodeToFile(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (String line : finalCode) {
                writer.write(line + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MacroProcessorPassII processor = new MacroProcessorPassII(
                "mnt.txt", "mdt.txt", "intermediate_code.txt"
        );
        processor.passTwo();
        processor.saveFinalCodeToFile("final_code.txt");
    }
}