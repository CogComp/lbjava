/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computations Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava;

import edu.illinois.cs.cogcomp.lbjava.IR.*;
import edu.illinois.cs.cogcomp.lbjava.frontend.Yylex;
import edu.illinois.cs.cogcomp.lbjava.frontend.parser;
import edu.illinois.cs.cogcomp.lbjava.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the main functions of LBJava:
 * <ul>
 *     <li>{@link SemanticAnalysis}</li>
 *     <li>{@link ClassifierCSE}</li>
 *     <li>{@link RevisionAnalysis}</li>
 *     <li>{@link TranslateToJava}</li>
 * </ul>
 *
 * @author Christos Christodoulopoulos
 */
public class MainTest {

    private String generateLBJavaScript(String learnerName, String extractor, String featImport) {
        return "import java.util.Vector;\n" +
                "import edu.illinois.cs.cogcomp.lbjava.VectorParser;\n" +
                featImport + "\n" +
                "import edu.illinois.cs.cogcomp.lbjava.PredefinedLabel;\n" +
                "\n" +
                "discrete "+learnerName+"(Vector v) <-\n" +
                "learn PredefinedLabel\n" +
                "\tusing "+extractor+"\n" +
                "\tfrom new VectorParser(\"target/test-classes/test1.train\")\n" +
                "\twith new NaiveBayes()\n" +
                "\ttestFrom new VectorParser(\"target/test-classes/test2.train\")\n" +
                "end";
    }

    @Before
    public void setUp() throws Exception {
        Main.fileNames = new HashSet<>();
        Main.generatedSourceDirectory = FileUtils.getPlatformIndependentFilePath("target/test-classes/lbj");
        Main.classDirectory = FileUtils.getPlatformIndependentFilePath("target/test-classes");
        Main.classPackageDirectory = FileUtils.getPlatformIndependentFilePath("target/test-classes/lbj");
        Main.sourceDirectory = FileUtils.getPlatformIndependentFilePath("target/test-classes/lbj");

        // The auto-generated code directory needs to be added to classpath
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, getClass().getResource("/lbj/."));
    }

    @Test
    public void testOneFeature() throws Exception {
        String input = generateLBJavaScript("OneFeatLearner", "testFeature1",
                "import edu.illinois.cs.cogcomp.lbjava.features.PredefinedFeature;");
        Yylex scanner = new Yylex(new ByteArrayInputStream(input.getBytes()));
        AST ast = (AST) new parser(scanner).parse().value;

        Main.runSemanticAnalysis(ast);

        assertEquals(3, SemanticAnalysis.dependorGraph.size());
        assertEquals(1, ast.declarations.size());

        ASTNode[] astNodes = ast.declarations.iterator().next().iterator().children;
        assertEquals(3, astNodes.length);
        assertTrue(astNodes[0] instanceof ClassifierReturnType);
        assertEquals("discrete", astNodes[0].toString());
        assertTrue(astNodes[2] instanceof LearningClassifierExpression);
        LearningClassifierExpression lce = (LearningClassifierExpression) astNodes[2];
        assertEquals("testFeature1", lce.extractor.getName());
        assertTrue(((ClassifierName) lce.extractor).isField);
        assertEquals("PredefinedFeature", AST.globalSymbolTable.classForName(lce.extractor.getName()).getSimpleName());

        new RevisionAnalysis(ast).run(ast);
        new ClassifierCSE(ast).run(ast);
        new TranslateToJava(ast).run(ast);
        new Train(ast, 0).run(ast);
    }

    @Test
    public void testTwoFeatures() throws Exception {
        String input = generateLBJavaScript("TwoFeatLearner", "testFeature1, testFeature2",
                "import edu.illinois.cs.cogcomp.lbjava.features.PredefinedFeature;");
        Yylex scanner = new Yylex(new ByteArrayInputStream(input.getBytes()));
        AST ast = (AST) new parser(scanner).parse().value;

        Main.runSemanticAnalysis(ast);

        assertEquals(5, SemanticAnalysis.dependorGraph.size());
        assertEquals(1, ast.declarations.size());

        ASTNode[] astNodes = ast.declarations.iterator().next().iterator().children;
        assertEquals(3, astNodes.length);
        assertTrue(astNodes[0] instanceof ClassifierReturnType);
        assertEquals("discrete", astNodes[0].toString());
        assertTrue(astNodes[2] instanceof LearningClassifierExpression);
        LearningClassifierExpression lce = (LearningClassifierExpression) astNodes[2];
        assertEquals("TwoFeatLearner$$1", lce.extractor.getName());
        ClassifierExpressionList components = ((CompositeGenerator) lce.extractor).components;
        assertEquals(2, components.size());
        ClassifierName component1 = (ClassifierName) components.iterator().next();
        assertTrue(component1.isField);
        assertEquals("PredefinedFeature", AST.globalSymbolTable.classForName(component1.getName()).getSimpleName());

        new RevisionAnalysis(ast).run(ast);
        new ClassifierCSE(ast).run(ast);
        new TranslateToJava(ast).run(ast);
        new Train(ast, 0).run(ast);
    }

    @Test
    public void testPackageFeature() throws Exception {
        String input = generateLBJavaScript("PackageFeatLearner", "testFeature1",
                "import edu.illinois.cs.cogcomp.lbjava.features.*;");
        Yylex scanner = new Yylex(new ByteArrayInputStream(input.getBytes()));
        AST ast = (AST) new parser(scanner).parse().value;

        Main.runSemanticAnalysis(ast);

        assertEquals(3, SemanticAnalysis.dependorGraph.size());
        assertEquals(1, ast.declarations.size());

        ASTNode[] astNodes = ast.declarations.iterator().next().iterator().children;
        assertEquals(3, astNodes.length);
        assertTrue(astNodes[0] instanceof ClassifierReturnType);
        assertEquals("discrete", astNodes[0].toString());
        assertTrue(astNodes[2] instanceof LearningClassifierExpression);
        LearningClassifierExpression lce = (LearningClassifierExpression) astNodes[2];
        assertEquals("testFeature1", lce.extractor.getName());
        assertTrue(((ClassifierName) lce.extractor).isField);
        assertEquals("PredefinedFeature", AST.globalSymbolTable.classForName(lce.extractor.getName()).getSimpleName());

        new RevisionAnalysis(ast).run(ast);
        new ClassifierCSE(ast).run(ast);
        new TranslateToJava(ast).run(ast);
        new Train(ast, 0).run(ast);
    }

    @After
    public void cleanup() {
        //Make sure we don't leave our auto-generated files behind
        File lbjDir = new File(Main.generatedSourceDirectory);
        File[] dirFiles = lbjDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return !name.endsWith(".lbj") && !name.startsWith(".nfs");
            }
        });
        for (File file: dirFiles) assert file.delete() : "Could not delete file " + file;
    }
}