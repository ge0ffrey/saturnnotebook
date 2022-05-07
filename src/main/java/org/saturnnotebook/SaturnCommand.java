package org.saturnnotebook;

import java.io.File;
import java.util.List;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "saturn", mixinStandardHelpOptions = true)
public class SaturnCommand implements Runnable {

    @Parameters(defaultValue = "data/helloWorld.adoc", description = "Notebook file.")
    File notebookFile;

    @Override
    public void run() {
        System.out.printf("Saturn started: notebookFile (%s)\n", notebookFile);
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        OptionsBuilder optionsBuilder = Options.builder();
        Document document = asciidoctor.loadFile(notebookFile, optionsBuilder.build());
        System.out.printf("Parsing ended: title (%s).\n", document.getTitle());

        JShell jshell = JShell.builder()
                .out(System.out)
                .err(System.err)
                .build();
        for (StructuralNode block : document.getBlocks()) {
            if (block.getNodeName().equals("listing")) {
                System.out.println("Listing found.");
                if (block.getAttributes().get("language").equals("java")) {
                    String content = block.getContent().toString();
                    List<SnippetEvent> snippetEvents = jshell.eval(content);
                    System.out.println("Executed.");
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }

}
