/**
 * Sketch Outline provides a tree view of a sketch and its 
 * members functions, variables and inner classes. Clicking on any 
 * node moves the cursor to its definition.
 *
 * Copyright (c) 2012 Manindra Moharana
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *  
 * 
 * @author	Manindra Moharana	##author##
 * @modified 19-03-2012			##date##
 * @version 0.1.5 (beta)		##version##
 */

package sketchoutline.tool;

import japa.parser.Token;
import japa.parser.ast.*;
import japa.parser.ast.body.*;

import java.awt.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.tree.*;

import processing.app.*;
import processing.core.*;

/**
 * This class is responsible for fetching and pre-processing code from the
 * editor and calling the japa parser internally to generate a tree data
 * structure which is accessed by the GUI.
 * 
 * @author Manindra Moharana
 */

public class TreeMaker {

	/**
	 * Controls whether stack traces and other debugging messages are displayed
	 * when some exception/error occurs.
	 */
	static boolean debugMode = false;

	/**
	 * PATH stores the path to a file that is to be read. Used for testing when
	 * running Sketch Outline outside the PDE
	 */
	static String PATH = "D:\\TestStuff\\UMLTest1.java";

	/**
	 * Contains the entire source code of the editor
	 */
	protected String sourceString;

	/**
	 * The main DefaultMutableTreeNode storing the entire code tree
	 */
	private DefaultMutableTreeNode codeTree = null;

	private DefaultMutableTreeNode currentParent = null,
			importStatments = null;
	public String mainClassName = null;
	public boolean treeCreated = false;

	public boolean hideFields = false;

	// Various offsets used mostly in xyToOffset() to precisely position cursor
	public int mainOffSet = -1, mainClassLineOffset = -1, importLineOffset = 0;
	public int firstMemberOffset = -1;
	private boolean mainClassParsed = false;
	private int classCount = 0;

	/**
	 * Whether the tree is to be sorted alphabetically
	 */
	public boolean enableSortingCodeTree = false;

	// The parent editor

	protected Editor editor;

	/**
	 * Stores if the PDE is running in basic mode(no function declarations)
	 */
	protected boolean basicMode = false;

	/**
	 * Converts a Node object into a TmNode and adds it to the code tree.
	 * 
	 * @param arg
	 *            - Used internally by Visitor
	 * @param node
	 *            - The Node to be converted
	 */
	protected void processNode(Object arg, Node node) {

		String type = getType(node);
		String parsed = type;
		// parsed += " --> " + node.toString() + " ";
		String nodeName = "";
		DefaultMutableTreeNode treeNode = null;
		TmNode tmNode = null;
		if (type.equals("ClassOrInterfaceDeclaration")
				|| type.equals("EnumDeclaration")
				|| type.equals("MethodDeclaration")
				|| type.equals("ConstructorDeclaration")) {

			if (type.equals("ClassOrInterfaceDeclaration")) {
				classCount++;
				nodeName = ((ClassOrInterfaceDeclaration) node).getName();
				// parsed += " <->" + nodeName;
				// t = new DefaultMutableTreeNode(name, true);

				if (classCount == 1 && mainOffSet == -1) {

					// This is the parent class
					mainClassLineOffset = node.getBeginLine();
					mainClassName = nodeName;
					// System.out.println( ((ClassOrInterfaceDeclaration)
					// node));
					// System.out.println(((ClassOrInterfaceDeclaration) node)
					// .getName()
					// + " - "
					// + ((ClassOrInterfaceDeclaration) node)
					// .getBeginLine());
				}

				if (classCount > 1 && firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			} else if (type.equals("EnumDeclaration")) {
				EnumDeclaration s = (EnumDeclaration) node;
				nodeName = s.getName();
				if (firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			} else if (type.equals("MethodDeclaration")) {
				MethodDeclaration s = (MethodDeclaration) node;
				nodeName = s.getName() + "(";
				LinkedList<Parameter> paramList = (LinkedList<Parameter>) s
						.getParameters();
				if (paramList != null) {
					for (Parameter parameter : paramList) {
						nodeName += parameter.getType().toString() + ", ";
					}
					nodeName = nodeName.substring(0, nodeName.length() - 2);
				}

				nodeName += ") : " + s.getType();
				// parsed += " <->" + ((MethodDeclaration) node).getName();

				// t = new DefaultMutableTreeNode(name, true);
				if (firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			} else if (type.equals("ConstructorDeclaration")) {
				parsed += " <->" + ((ConstructorDeclaration) node).getName();
				ConstructorDeclaration s = (ConstructorDeclaration) node;
				nodeName = s.getName() + "(";
				LinkedList<Parameter> paramList = (LinkedList<Parameter>) s
						.getParameters();
				if (paramList != null) {
					for (Parameter parameter : paramList) {
						nodeName += parameter.getType().toString() + ", ";
					}
					nodeName = nodeName.substring(0, nodeName.length() - 2);
				}

				nodeName += ")";
				// t = new DefaultMutableTreeNode(name, true);
				if (firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			}

			// System.out.println(getType(node));
			if (codeTree != null && currentParent != null) {
				tmNode = new TmNode(node, nodeName);
				treeNode = new DefaultMutableTreeNode(tmNode, true);

				if (liesWithinParent(node)) {
					// System.out.println("Within parent, adding..");
				} else {
					// System.out
					// .println("Outside current parent,getting last parent and adding..");
					do {
						currentParent = (DefaultMutableTreeNode) currentParent
								.getParent();
					} while (!liesWithinParent(node));

				}
				currentParent.add(treeNode);
				currentParent = treeNode;

				// If this is main class, add import statements list to it if
				// there are any
				if (classCount == 1 && importStatments != null) {
					if (importStatments.isRoot())
						currentParent.add(importStatments);
				}

			}
			return;
		}

		if (type.equals("FieldDeclaration")) {
			// parsed += " <->" + ((FieldDeclaration) node).toString();

			FieldDeclaration fd = (FieldDeclaration) node;

			// System.out.println(fd.getType());
			if (codeTree != null && currentParent != null) {

				if (liesWithinParent(node)) {
					// System.out.println("Within parent, adding..");

				} else {
					// System.out
					// .println("Outside current parent,getting last parent and adding..");
					do {
						currentParent = (DefaultMutableTreeNode) currentParent
								.getParent();
					} while (!liesWithinParent(node));
				}
				for (Iterator<VariableDeclarator> iterator = fd.getVariables()
						.iterator(); iterator.hasNext();) {
					VariableDeclarator vd = (VariableDeclarator) iterator
							.next();

					// tmVariable tv = new tmVariable((FieldDeclaration) node,
					// vd);
					nodeName = vd.getId().getName() + " : "
							+ fd.getType().toString();
					// nodeName = fd.getType().toString();
					tmNode = new TmNode(node, nodeName);
					treeNode = new DefaultMutableTreeNode(tmNode, true);

					currentParent.add(treeNode);
					// System.out.println(type2.getId().getName());

				}

			}

			if (firstMemberOffset == -1) {
				firstMemberOffset = node.getBeginLine();
			}
			return;
		}

		if (type.equals("CompilationUnit")) {
			tmNode = new TmNode(node, PATH);
			codeTree = new DefaultMutableTreeNode(tmNode, true);
			currentParent = codeTree;
			return;
		}

		if (type.equals("ImportDeclaration")) {

			ImportDeclaration id = (ImportDeclaration) node;
			// System.out
			// .println(id.toString().trim() + " - " + id.getBeginLine());

			if ((id.toString().trim()).equals("import java.applet.*;")) {
				enableParsingImport = false;

				// Processed file adds the custom imports leaving a new line
				// above and below
				// So subtract 3 from line offset
				importLineOffset = node.getBeginLine() - importLineOffset - 3;
				if (importLineOffset <= 0) {

				}
				System.out.println(node.getBeginLine() + ",import "
						+ (importLineOffset));
			}

			if (enableParsingImport && codeTree != null
					&& currentParent != null) {
				if (importStatments == null) {
					tmNode = new TmNode(node, "Import Statements");
					importStatments = new DefaultMutableTreeNode(tmNode, true);
					// currentParent.add(importStatments);
				}
				tmNode = new TmNode(node, ((ImportDeclaration) node).toString());
				treeNode = new DefaultMutableTreeNode(tmNode, true);
				importStatments.add(treeNode);
				// System.out.println(node.toString());
			}

			if ((id.toString().trim()).equals("import processing.xml.*;")) {
				importLineOffset = node.getBeginLine();
				System.out.print(importLineOffset + ",");
				enableParsingImport = true;
			}

			return;
		}

	}

	/**
	 * For testing purposes.
	 */
	protected void processNode2(Object arg, Node node) {

		String type = getType(node);
		String parsed = type;
		// parsed += " --> " + node.toString() + " ";
		String nodeName = "";
		DefaultMutableTreeNode treeNode = null;
		TmNode tmNode = null;
		if (type.equals("ClassOrInterfaceDeclaration")
				|| type.equals("EnumDeclaration")
				|| type.equals("MethodDeclaration")
				|| type.equals("ConstructorDeclaration")) {

			if (type.equals("ClassOrInterfaceDeclaration")) {
				classCount++;
				nodeName = ((ClassOrInterfaceDeclaration) node).getName();
				// parsed += " <->" + nodeName;
				// t = new DefaultMutableTreeNode(name, true);

				if (classCount == 1 && mainOffSet == -1) {

					// This is the parent class
					mainClassLineOffset = node.getBeginLine();
					mainClassName = nodeName;
					// System.out.println( ((ClassOrInterfaceDeclaration)
					// node));
					// System.out.println(((ClassOrInterfaceDeclaration) node)
					// .getName()
					// + " - "
					// + ((ClassOrInterfaceDeclaration) node)
					// .getBeginLine());
				}

				if (classCount > 1 && firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			} else if (type.equals("EnumDeclaration")) {
				EnumDeclaration s = (EnumDeclaration) node;
				nodeName = s.getName();
				if (firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			} else if (type.equals("MethodDeclaration")) {
				MethodDeclaration s = (MethodDeclaration) node;
				nodeName = s.getName() + "(";
				LinkedList<Parameter> paramList = (LinkedList<Parameter>) s
						.getParameters();
				if (paramList != null) {
					for (Parameter parameter : paramList) {
						nodeName += parameter.getType().toString() + ", ";
					}
					nodeName = nodeName.substring(0, nodeName.length() - 2);
				}

				nodeName += ") : " + s.getType();
				// parsed += " <->" + ((MethodDeclaration) node).getName();

				// t = new DefaultMutableTreeNode(name, true);
				if (firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			} else if (type.equals("ConstructorDeclaration")) {
				parsed += " <->" + ((ConstructorDeclaration) node).getName();
				ConstructorDeclaration s = (ConstructorDeclaration) node;
				nodeName = s.getName() + "(";
				LinkedList<Parameter> paramList = (LinkedList<Parameter>) s
						.getParameters();
				if (paramList != null) {
					for (Parameter parameter : paramList) {
						nodeName += parameter.getType().toString() + ", ";
					}
					nodeName = nodeName.substring(0, nodeName.length() - 2);
				}

				nodeName += ")";
				// t = new DefaultMutableTreeNode(name, true);
				if (firstMemberOffset == -1) {
					firstMemberOffset = node.getBeginLine();
				}
			}

			// System.out.println(getType(node));
			if (codeTree != null && currentParent != null) {
				tmNode = new TmNode(node, nodeName);
				treeNode = new DefaultMutableTreeNode(tmNode, true);

				if (liesWithinParent(node)) {
					// System.out.println("Within parent, adding..");
				} else {
					// System.out
					// .println("Outside current parent,getting last parent and adding..");
					do {
						currentParent = (DefaultMutableTreeNode) currentParent
								.getParent();
					} while (!liesWithinParent(node));

				}
				currentParent.add(treeNode);
				currentParent = treeNode;

				// If this is main class, add import statements list to it if
				// there are any
				if (classCount == 1 && importStatments != null) {
					if (importStatments.isRoot())
						currentParent.add(importStatments);
				}

			}
			return;
		}

		if (type.equals("FieldDeclaration")) {
			// parsed += " <->" + ((FieldDeclaration) node).toString();

			FieldDeclaration fd = (FieldDeclaration) node;

			// System.out.println(fd.getType());
			if (codeTree != null && currentParent != null) {

				if (liesWithinParent(node)) {
					// System.out.println("Within parent, adding..");

				} else {
					// System.out
					// .println("Outside current parent,getting last parent and adding..");
					do {
						currentParent = (DefaultMutableTreeNode) currentParent
								.getParent();
					} while (!liesWithinParent(node));
				}
				for (Iterator<VariableDeclarator> iterator = fd.getVariables()
						.iterator(); iterator.hasNext();) {
					VariableDeclarator vd = (VariableDeclarator) iterator
							.next();

					// tmVariable tv = new tmVariable((FieldDeclaration) node,
					// vd);
					nodeName = vd.getId().getName() + " : "
							+ fd.getType().toString();
					// nodeName = fd.getType().toString();
					tmNode = new TmNode(node, nodeName);
					treeNode = new DefaultMutableTreeNode(tmNode, true);

					currentParent.add(treeNode);
					// System.out.println(type2.getId().getName());

				}

			}

			if (firstMemberOffset == -1) {
				firstMemberOffset = node.getBeginLine();
			}
			return;
		}

		if (type.equals("CompilationUnit")) {
			tmNode = new TmNode(node, PATH);
			codeTree = new DefaultMutableTreeNode(tmNode, true);
			currentParent = codeTree;
			return;
		}

		if (type.equals("ImportDeclaration")) {

			ImportDeclaration id = (ImportDeclaration) node;
			// System.out
			// .println(id.toString().trim() + " - " + id.getBeginLine());

			if ((id.toString().trim()).equals("import java.applet.*;")) {
				enableParsingImport = false;

				// Processed file adds the custom imports leaving a new line
				// above and below
				// So subtract 3 from line offset
				importLineOffset = node.getBeginLine() - importLineOffset - 3;
				if (importLineOffset <= 0) {

				}
				System.out.println(node.getBeginLine() + ",import "
						+ (importLineOffset));
			}

			if (enableParsingImport && codeTree != null
					&& currentParent != null) {
				if (importStatments == null) {
					tmNode = new TmNode(node, "Import Statements");
					importStatments = new DefaultMutableTreeNode(tmNode, true);
					// currentParent.add(importStatments);
				}
				tmNode = new TmNode(node, ((ImportDeclaration) node).toString());
				treeNode = new DefaultMutableTreeNode(tmNode, true);
				importStatments.add(treeNode);
				// System.out.println(node.toString());
			}

			if ((id.toString().trim()).equals("import processing.xml.*;")) {
				importLineOffset = node.getBeginLine();
				System.out.print(importLineOffset + ",");
				enableParsingImport = true;
			}

			return;
		}

	}

	private boolean enableParsingImport = false;

	/**
	 * Checks whether a node lies inside the current parent node. i.e., if
	 * methods of a class are being parsed, the methods are defined within the
	 * current parent(class)
	 * 
	 * @param node
	 *            - The Node to be checked
	 */
	private boolean liesWithinParent(Node node) {

		if (node == null)
			return true;

		Point p1 = new Point(node.getBeginLine(), node.getBeginColumn());
		Point p2 = new Point(node.getEndLine(), node.getEndColumn());

		Node n = ((TmNode) currentParent.getUserObject()).node;

		Point lastNodeBegin = new Point(n.getBeginLine(), n.getBeginColumn());
		Point lastNodeEnd = new Point(n.getEndLine(), n.getEndColumn());

		// System.out.println(" parent: " + lastNodeBegin.toString() + " , "
		// + lastNodeEnd.toString() + " #" + getType(n));
		// System.out.println("  child: " + p1.toString() + " , " +
		// p2.toString());
		if (lastNodeBegin.x <= p1.x && p2.x <= lastNodeEnd.x)
			return true;
		if (lastNodeBegin.x == p1.x && p2.x == lastNodeEnd.x) {
			if (lastNodeBegin.y < p1.y && p2.y < lastNodeEnd.y)
				return true;
		}
		return false;

	}

	/**
	 * Converts a row no, column no. representation of cursor location to offset
	 * representation. Editor uses JTextArea internally which deals only with
	 * caret offset, not row no. and column no.
	 * 
	 * @param x
	 *            - row no.
	 * @param y
	 *            - column no.
	 * 
	 * @return int - Offset
	 */
	public int xyToOffset(int x, int y) {

		String[] lines = {};// = PApplet.split(sourceString, '\n');
		int offset = 0;

		int codeIndex = 0;
		int bigCount = 0;
		for (SketchCode sc : editor.getSketch().getCode()) {
			if (sc.isExtension("pde")) {
				sc.setPreprocOffset(bigCount);

				try {
					int len = 0;
					if (editor.getSketch().getCurrentCode().equals(sc)) {
						lines = PApplet.split(
								sc.getDocument().getText(0,
										sc.getDocument().getLength()), '\n');
						// System.out.println("Getting from document "
						// + sc.getLineCount() + "," + lines.length);
						len = Base.countLines(sc.getDocument().getText(0,
								sc.getDocument().getLength())) + 1;
					} else {
						lines = PApplet.split(sc.getProgram(), '\n');
						len = Base.countLines(sc.getProgram()) + 1;
					}

					// Adding + 1 to len because \n gets appended for each
					// sketchcode extracted during processPDECode()
					if (x >= len) {
						x -= len;
					} else {
						// System.out.println("x: " + temp + " relative x = " +
						// x +
						// "in tab: " +
						// editor.getSketch().getCode(codeIndex).getPrettyName());
						editor.getSketch().setCurrentCode(codeIndex);
						break;
					}
					codeIndex++;

				} catch (Exception e) {
					System.out.println("Document Exception in xyToOffset");
					if (TreeMaker.debugMode) {

						e.printStackTrace();
					}
				}
				bigCount += sc.getLineCount();
			}

		}

		//

		// Count chars till the end of previous line(x-1), keeping in mind x
		// starts from 1
		// System.out.println(" offset x: " + x);
		for (int i = 0; i < x - 1; i++) {
			offset += lines[i].length() + 1;
		}
		// Line Columns start from 1
		offset += y == 0 ? 0 : y - 1;
		return offset;
	}

	/**
	 * Converts a row no to offset. Calls xyToOffset internally -
	 * xyToOffset(x,0)
	 * 
	 * @param x
	 *            - row no.
	 */
	public int xToOffset(int x) {
		return xyToOffset(x, 0);
	}

	public TreeMaker() {
		buildTree();
	}

	/**
	 * Constructor for TreeMaker
	 * 
	 * @param editor
	 *            - The parent Editor
	 */
	public TreeMaker(Editor editor) {
		this.editor = editor;
		debugMode = true;
		buildTree();
		debugMode = false;
	}

	public TreeMaker(String path) {
		PATH = path;
		buildTree();
	}

	public TreeMaker(String path, Editor editor) {
		PATH = path;
		this.editor = editor;
		buildTree();
	}

	/**
	 * The main function that generates the code tree.
	 * 
	 * @return Returns true if tree was built successfully
	 */
	protected boolean buildTree() {

		codeTree = currentParent = importStatments = null;
		mainClassName = null;
		treeCreated = false;
		mainOffSet = mainClassLineOffset = -1;
		importLineOffset = 0;
		firstMemberOffset = -1;
		mainClassParsed = false;
		classCount = 0;
		basicMode = false;
		try {
			sourceString = processPDECode();// Helper.readFile(PATH);
			CompilationUnit cu = Helper.parserString(sourceString);
			mainClassParsed = false;
			cu.accept(new Visitor(sourceString, this), null);
			// if (TreeMaker.debugMode)
			// System.out.println("Outline Tree Built.");

			sortTree(codeTree, enableSortingCodeTree);

			if (hideFields)
				removeFields(codeTree);

			treeCreated = true;
			firstMemberOffset = firstMemberOffset - mainClassLineOffset;
			return true;
		} catch (japa.parser.ParseException e) {
			// Tree not built 'cause of parsing errors, notify user
			if (debugMode) {
				Token t = e.currentToken;
				int errorLine = t.endLine;
				System.out.println("Parse error encountered after -> \""
						+ t.image + "\"");

				if (editor.getSketch().getCodeCount() == 1) {
					System.out.println("Near line no: " + (errorLine - 1)
							+ " in tab "
							+ editor.getSketch().getCode(0).getPrettyName());
				} else {
					for (int i = 0; i < editor.getSketch().getCodeCount(); i++) {
						if (errorLine
								- editor.getSketch().getCode(i).getLineCount() <= 0) {
							System.out.println("Near line no: "
									+ (errorLine - i - 1)
									+ " in tab "
									+ editor.getSketch().getCode(i)
											.getPrettyName());
							return false;
						} else
							errorLine -= editor.getSketch().getCode(i)
									.getLineCount();
					}
				}
			}
			return false;
		} catch (Exception e) {
			if (debugMode) {
				System.err
						.println("Sketch Outline Tool - Oops. Something went wrong with buildTree() :P "
								+ e.toString());
				e.printStackTrace();
			}
			treeCreated = false;
			return false;
			//
		} catch (japa.parser.TokenMgrError e) {

			if (debugMode) {
				System.out.println("Lexical Error in buildTree() -> "
						+ e.getMessage());
			}
			return false;
		} catch (Error e) {
			if (debugMode) {
				System.err.println("Error caught in buildTree() "
						+ e.toString());
				e.printStackTrace();
				System.out.println("Can't build tree.");
			}
			treeCreated = false;
			return false;
		}

	}

	/**
	 * This one's is for testing and trying out stuff.
	 */
	@SuppressWarnings("unused")
	private boolean buildTree2() {
		codeTree = currentParent = importStatments = null;
		mainClassName = null;
		treeCreated = false;
		mainOffSet = mainClassLineOffset = -1;
		importLineOffset = 0;
		firstMemberOffset = -1;
		mainClassParsed = false;
		classCount = 0;

		try {
			sourceString = processPDECode2();// Helper.readFile(PATH);
			System.out.println("-->\n" + sourceString + "\n<--");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		int count = 0;
		while (count <= 10) {
			try {
				count++;
				System.out.println(count);
				// sourceString = processPDECode2(sourceString);
				CompilationUnit cu = Helper.parserString(sourceString);
				mainClassParsed = false;
				cu.accept(new Visitor(sourceString, this), null);
				System.out.println("Tree Built.");
				treeCreated = true;
				firstMemberOffset = firstMemberOffset - mainClassLineOffset;
				// System.out.println(firstMemberOffset);
				return true;
			} catch (japa.parser.ParseException e) {
				System.out.println(e.getLocalizedMessage());
				// Token t = e.currentToken;
				// System.out.println("Last token:" + t.toString() + "->Next:"
				// + t.next.toString());
				// int errorLine = t.endLine;
				// int errorColumn = t.endColumn;
				// System.out.println("Error at: " + t.beginColumn + ","
				// + errorLine + "," + t.next.endColumn);
				// String lines[] = PApplet.split(sourceString, '\n');
				// StringBuffer sb = new StringBuffer();
				// for (int i = 0; i < lines.length; i++) {
				// String line = lines[i] + "\n";
				// if (i != errorLine - 1)
				// sb.append(line);
				// else {
				// System.out.println(line);
				// System.out.println("Length" + line.length());
				// System.out.println("Error at: " + t.beginColumn + ","
				// + errorLine + "," + t.next.endColumn);
				// String newLine = line.substring(0,
				// t.next.beginColumn - 1)
				// + getWhiteSpaces(t.next.toString())
				// + line.substring(t.next.endColumn,
				// line.length() - 1);
				// // if(newLine.length() >= t.next.endColumn + 1)
				// // newLine+= line.substring(t.next.endColumn ,
				// // line.length());
				// // else
				// // newLine += line.substring(t.next.endColumn,
				// // line.length());
				// sb.append(newLine);
				// }
				// }
				// sourceString = sb.toString();
				// System.out.println("-->\n" + sourceString + "\n<--");
				// continue;
				// String exp = e.toString();
				// System.out.println(exp);
				//

			} catch (Exception e) {
				System.err
						.println("Sketch Outline Tool - Oops. Something went wrong with buildTree() :P "
								+ e.toString());
				e.printStackTrace();
				treeCreated = false;
				return false;
				//
			}
		}
		return true;
	}

	/**
	 * Returns a String consisting of only whitespaces of the same length as the
	 * input String.
	 * 
	 * @param what
	 *            - String whose whitespace String is to be returned
	 * 
	 * @return String - whitespace String
	 * 
	 */
	public String getWhiteSpaces(String what) {
		String space = "";
		for (int i = 0; i < what.length(); i++) {
			space += " ";
		}
		return space;
	}

	/**
	 * Return the main codeTree
	 * 
	 * @return DefaultMutableTreeNode
	 */
	public DefaultMutableTreeNode getCodeTree() {
		return (DefaultMutableTreeNode) (codeTree.getChildAt(0));
	}

	/**
	 * Sort and return the main codeTree
	 * 
	 * @return DefaultMutableTreeNode
	 */
	public DefaultMutableTreeNode getSortedCodeTree() {
		sortTree(codeTree, true);
		return (DefaultMutableTreeNode) (codeTree.getChildAt(0));
	}

	/**
	 * Returns the type of Node as String
	 * 
	 * @param Node
	 *            - The input Node
	 * 
	 * @return String - The type of Node
	 */
	public static String getType(Node node) {
		String type = node.getClass().getName();
		for (int i = type.length() - 1; i >= 0; i--) {
			if (type.charAt(i) == '.') {
				type = type.substring(i);
				type = type.substring(1);
				break;
			}
		}
		return type;
	}

	/**
	 * Returns the type of TmNode as String
	 * 
	 * @param Node
	 *            - The input TmNode
	 * 
	 * @return String - The type of Node
	 */
	public static String getType(TmNode node) {
		return getType(node.node);
	}

	/**
	 * Used only for testing.
	 */
	public String processPDECode2() {
		String sourceAlt = "";
		// Handle code input from editor/java file
		final String importRegexp = "(?:^|;)\\s*(import\\s+)((?:static\\s+)?\\S+)(\\s*;)";
		ArrayList<DefaultMutableTreeNode> tabList = new ArrayList<DefaultMutableTreeNode>();
		ArrayList<Integer> tabLineOffset = new ArrayList<Integer>();
		try {
			if (editor == null) {

				sourceAlt = Helper.readFile(PATH);
				System.out.println("Reading .java file");
				System.exit(0);
			} else {
				StringBuffer bigCode = new StringBuffer();
				int bigCount = 0;
				for (SketchCode sc : editor.getSketch().getCode()) {
					if (sc.isExtension("pde")) {
						sc.setPreprocOffset(bigCount);
						bigCount += sc.getLineCount();
						TmNode tn = new TmNode(null, sc.getPrettyName());
						tabList.add(new DefaultMutableTreeNode(tn, true));
						tabLineOffset.add(new Integer(bigCount + 1));
						try {
							if (editor.getSketch().getCurrentCode().equals(sc))
								bigCode.append(sc.getDocument().getText(0,
										sc.getDocument().getLength()));
							else {
								bigCode.append(sc.getProgram());
							}
							bigCode.append('\n');
						} catch (Exception e) {
							System.out.println("Document Exception");
							e.printStackTrace();
						}
						bigCode.append('\n');
					}
				}

				sourceAlt = bigCode.toString();
				// System.out.println("Obtaining source from editor.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Replace comments with whitespaces
		// sourceAlt = scrubComments(sourceAlt);

		// Find all int(*), replace with (int)(*)

		// \bint\s*\(\s*\b , i.e all exclusive "int("
		String dataTypeFunc[] = { "int", "char", "float", "boolean", "byte" };
		for (String dataType : dataTypeFunc) {
			String dataTypeRegexp = "\\b" + dataType + "\\s*\\(\\s*\\b";
			Pattern pattern = Pattern.compile(dataTypeRegexp);
			Matcher matcher = pattern.matcher(sourceAlt);

			// while (matcher.find()) {
			// System.out.print("Start index: " + matcher.start());
			// System.out.println(" End index: " + matcher.end() + " ");
			// System.out.println("-->" + matcher.group() + "<--");
			// }
			sourceAlt = matcher.replaceAll("(" + dataType + ")(");

		}

		// Find all import statements and remove them, add them to import list
		ArrayList<String> programImports = new ArrayList<String>();
		// int i = 0;
		do {
			// System.out.println("-->\n" + sourceAlt + "\n<--");
			String[] pieces = PApplet.match(sourceAlt, importRegexp);

			// Stop the loop if we've removed all the import lines
			if (pieces == null)
				break;

			String piece = pieces[1] + pieces[2] + pieces[3];
			int len = piece.length(); // how much to trim out

			programImports.add(piece); // the package name
			// System.out.println("Import -> " + piece);

			// find index of this import in the program
			int idx = sourceAlt.indexOf(piece);

			// Remove the import from the main program
			String whiteSpace = "";
			for (int j = 0; j < piece.length(); j++) {
				whiteSpace += " ";
			}
			sourceAlt = sourceAlt.substring(0, idx) + whiteSpace
					+ sourceAlt.substring(idx + len);

		} while (true);

		for (int j = 0; j < sourceAlt.length(); j++) {
			if (sourceAlt.charAt(j) == '#') {
				sourceAlt = sourceAlt.substring(0, j) + "0xff"
						+ sourceAlt.substring(j + 1);
			}
		}

		String className = (editor == null) ? "DefaultClass" : editor
				.getSketch().getName();

		sourceAlt = "public class " + className + " extends PApplet {\n"
				+ sourceAlt + "\n}\n";

		// System.out.println("-->\n" + sourceAlt + "\n<--");
		// System.out.println("PDE code processed.");
		return sourceAlt;
	}

	/**
	 * Fetches code from the editor tabs and pre-processes it into parsable pure
	 * java source. <br>
	 * Handles: <li>Removal of import statements <li>Conversion of int(),
	 * char(), etc to (int)(), (char)(), etc. <li>Replacing '#' with 0xff for
	 * color representation <li>Appends class declaration statement
	 * 
	 * @return String - Pure java representation of PDE code
	 */
	public String processPDECode() {

		// Super wicked regular expressions! (Used from Processing source)
		final String importRegexp = "(?:^|;)\\s*(import\\s+)((?:static\\s+)?\\S+)(\\s*;)";
		final Pattern FUNCTION_DECL = Pattern.compile(
				"(^|;)\\s*((public|private|protected|final|static)\\s+)*"
						+ "(void|int|float|double|String|char|byte)"
						+ "(\\s*\\[\\s*\\])?\\s+[a-zA-Z0-9]+\\s*\\(",
				Pattern.MULTILINE);
		String sourceAlt = "";

		// Handle code input from editor/java file
		try {
			if (editor == null) {

				sourceAlt = Helper.readFile(PATH);
				// System.out.println("Reading .java file");
			} else {
				StringBuffer bigCode = new StringBuffer();
				int bigCount = 0;
				for (SketchCode sc : editor.getSketch().getCode()) {
					if (sc.isExtension("pde")) {
						sc.setPreprocOffset(bigCount);

						try {

							if (editor.getSketch().getCurrentCode().equals(sc))
								bigCode.append(sc.getDocument().getText(0,
										sc.getDocument().getLength()));
							else {
								bigCode.append(sc.getProgram());

							}
							bigCode.append('\n');
						} catch (Exception e) {
							System.err
									.println("Exception in processPDECode() - bigCode "
											+ e.toString());
							if (debugMode) {
								e.printStackTrace();
							}
						}
						bigCode.append('\n');
						bigCount += sc.getLineCount();
					}
				}

				sourceAlt = bigCode.toString();
				// System.out.println("Obtaining source from editor.");
			}
		} catch (Exception e) {

			System.out.println("Exception in processPDECode()");
			if (debugMode)
				e.printStackTrace();
		}

		// Replace comments with whitespaces
		// sourceAlt = scrubComments(sourceAlt);

		// Find all int(*), replace with (int)(*)

		// \bint\s*\(\s*\b , i.e all exclusive "int("

		String dataTypeFunc[] = { "int", "char", "float", "boolean", "byte" };
		for (String dataType : dataTypeFunc) {
			String dataTypeRegexp = "\\b" + dataType + "\\s*\\(";
			Pattern pattern = Pattern.compile(dataTypeRegexp);
			Matcher matcher = pattern.matcher(sourceAlt);

			// while (matcher.find()) {
			// System.out.print("Start index: " + matcher.start());
			// System.out.println(" End index: " + matcher.end() + " ");
			// System.out.println("-->" + matcher.group() + "<--");
			// }
			sourceAlt = matcher.replaceAll("PApplet.parse"
					+ Character.toUpperCase(dataType.charAt(0))
					+ dataType.substring(1) + "(");
		}

		// Find all #[web color] and replace with 0xff[webcolor]
		// Should be 6 digits only.
		String webColorRegexp = "#{1}[A-F|a-f|0-9]{6}\\W";
		Pattern webPattern = Pattern.compile(webColorRegexp);
		Matcher webMatcher = webPattern.matcher(sourceAlt);
		while (webMatcher.find()) {
			// System.out.println("Found at: " + webMatcher.start());
			String found = sourceAlt.substring(webMatcher.start(),
					webMatcher.end());
			// System.out.println("-> " + found);
			sourceAlt = webMatcher.replaceFirst("0xff" + found.substring(1));
			webMatcher = webPattern.matcher(sourceAlt);
		}

		// Find all import statements and remove them, add them to import list
		ArrayList<String> programImports = new ArrayList<String>();
		//
		do {
			// System.out.println("-->\n" + sourceAlt + "\n<--");
			String[] pieces = PApplet.match(sourceAlt, importRegexp);

			// Stop the loop if we've removed all the import lines
			if (pieces == null)
				break;

			String piece = pieces[1] + pieces[2] + pieces[3];
			int len = piece.length(); // how much to trim out

			programImports.add(piece); // the package name
			// System.out.println("Import -> " + piece);

			// find index of this import in the program
			int idx = sourceAlt.indexOf(piece);

			// Remove the import from the main program
			String whiteSpace = "";
			for (int j = 0; j < piece.length(); j++) {
				whiteSpace += " ";
			}
			sourceAlt = sourceAlt.substring(0, idx) + whiteSpace
					+ sourceAlt.substring(idx + len);

		} while (true);

		String className = (editor == null) ? "DefaultClass" : editor
				.getSketch().getName();

		// Check whether the code is being written in BASIC mode(no function
		// declarations) - append class declaration and void setup() declaration
		Matcher matcher = FUNCTION_DECL.matcher(sourceAlt);
		if (!matcher.find()) {
			sourceAlt = "public class " + className + " extends PApplet {\n"
					+ "public void setup() {\n" + sourceAlt
					+ "\nnoLoop();\n}\n" + "\n}\n";
			basicMode = true;
		} else
			sourceAlt = "public class " + className + " extends PApplet {\n"
					+ sourceAlt + "\n}\n";

		// Convert non ascii characters
		sourceAlt = substituteUnicode(sourceAlt);

		// System.out.println("-->\n" + sourceAlt + "\n<--");
		// System.out.println("PDE code processed.");
		return sourceAlt;
	}

	/**
	 * Returns the line no and column no. from cursor offset.
	 * 
	 * @return Point - x and y are line no and column no.
	 */

	public Point calculateCursorLocation() {

		if (editor == null) {
			return null;
		}

		// Find the nearest node to the cursor - basically reverse of xyToOffet
		editor.getLineCount();
		SketchCode currentTab = editor.getSketch().getCurrentCode();
		int currentLine = 0;
		// Find currentTotalOffset and totalLineNo
		for (int i = 0; i < editor.getSketch().getCodeCount(); i++) {
			if (editor.getSketch().getCode(i).equals(currentTab)) {
				// currentOffest += editor.getCaretOffset();
				break;
			} else {
				// currentOffest +=
				// editor.getSketch().getCode(i).getProgram().length();
				currentLine += editor.getSketch().getCode(i).getLineCount();
			}
		}

		String lines[] = PApplet.split(currentTab.getProgram(), '\n');
		int currentTabLine = 0, currentTabOffset = 0, currentColumn = 0;
		for (int i = 0; i < lines.length; i++) {
			int len = lines[i].length() + 1; // + 1 as split() removes \n
			currentTabOffset += len;
			currentTabLine++;
			if (editor.getCaretOffset() <= currentTabOffset) {
				currentColumn = currentTabOffset - editor.getCaretOffset();
				currentColumn = len - currentColumn;
				break;
			}
		}

		currentLine += currentTabLine;

		System.out.println("Current Line: " + currentTabLine + "col: "
				+ currentColumn + " off: " + currentTabOffset);
		Point location = new Point(currentLine, currentColumn);
		return location;
	}

	/**
	 * Removes comments from input String. Used from Processing source.
	 * 
	 * @param what
	 *            - Input String
	 * @return String - The processed string with comments removed.
	 */
	static public String scrubComments(String what) {
		char p[] = what.toCharArray();

		int index = 0;
		while (index < p.length) {
			// for any double slash comments, ignore until the end of the line
			if ((p[index] == '/') && (index < p.length - 1)
					&& (p[index + 1] == '/')) {
				p[index++] = ' ';
				p[index++] = ' ';
				while ((index < p.length) && (p[index] != '\n')) {
					p[index++] = ' ';
				}

				// check to see if this is the start of a new multiline comment.
				// if it is, then make sure it's actually terminated somewhere.
			} else if ((p[index] == '/') && (index < p.length - 1)
					&& (p[index + 1] == '*')) {
				p[index++] = ' ';
				p[index++] = ' ';
				boolean endOfRainbow = false;
				while (index < p.length - 1) {
					if ((p[index] == '*') && (p[index + 1] == '/')) {
						p[index++] = ' ';
						p[index++] = ' ';
						endOfRainbow = true;
						break;

					} else {
						// continue blanking this area
						p[index++] = ' ';
					}
				}
				if (!endOfRainbow) {
					throw new RuntimeException(
							"Missing the */ from the end of a "
									+ "/* comment */");
				}
			} else { // any old character, move along
				index++;
			}
		}
		return new String(p);
	}

	/**
	 * Replaces non-ascii characters with their unicode escape sequences and
	 * stuff. Used as it is from
	 * /processing/src/processing/mode/java/preproc/PdePreprocessor
	 * 
	 * @param program
	 *            - Input String containing non ascii characters
	 * @return String - Converted String
	 */
	public static String substituteUnicode(String program) {
		// check for non-ascii chars (these will be/must be in unicode format)
		char p[] = program.toCharArray();
		int unicodeCount = 0;
		for (int i = 0; i < p.length; i++) {
			if (p[i] > 127)
				unicodeCount++;
		}
		if (unicodeCount == 0)
			return program;
		// if non-ascii chars are in there, convert to unicode escapes
		// add unicodeCount * 5.. replacing each unicode char
		// with six digit uXXXX sequence (xxxx is in hex)
		// (except for nbsp chars which will be a replaced with a space)
		int index = 0;
		char p2[] = new char[p.length + unicodeCount * 5];
		for (int i = 0; i < p.length; i++) {
			if (p[i] < 128) {
				p2[index++] = p[i];
			} else if (p[i] == 160) { // unicode for non-breaking space
				p2[index++] = ' ';
			} else {
				int c = p[i];
				p2[index++] = '\\';
				p2[index++] = 'u';
				char str[] = Integer.toHexString(c).toCharArray();
				// add leading zeros, so that the length is 4
				// for (int i = 0; i < 4 - str.length; i++) p2[index++] = '0';
				for (int m = 0; m < 4 - str.length; m++)
					p2[index++] = '0';
				System.arraycopy(str, 0, p2, index, str.length);
				index += str.length;
			}
		}
		return new String(p2, 0, index);
	}

	/**
	 * Prints codeTree to the console
	 */
	public static void printTree(DefaultMutableTreeNode node) {
		if (node.isLeaf()) {
			System.out.println(node.getUserObject().toString());
			return;
		} else {
			System.out.println(node.getUserObject().toString() + " --> parent");
			for (int i = 0; i < node.getChildCount(); i++) {
				printTree((DefaultMutableTreeNode) node.getChildAt(i));
			}
		}
	}

	/**
	 * Sorts all nodes of a tree and child nodes alphabetically using recursion
	 * 
	 * @param unsortedTree
	 *            - The Tree to be sorted
	 * 
	 */

	@SuppressWarnings("unchecked")
	public static void sortTree(DefaultMutableTreeNode unsortedTree,
			final boolean sortAlphabetical) {

		for (int i = 0; i < unsortedTree.getChildCount(); i++) {
			if (unsortedTree.getChildAt(i).isLeaf() == false)
				sortTree(((DefaultMutableTreeNode) unsortedTree.getChildAt(i)),
						sortAlphabetical);
		}

		@SuppressWarnings({ "rawtypes" })
		ArrayList children = Collections.list(unsortedTree.children());
		Collections.sort(children, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				DefaultMutableTreeNode n1 = (DefaultMutableTreeNode) o1;
				DefaultMutableTreeNode n2 = (DefaultMutableTreeNode) o2;

				TmNode a = (TmNode) (n1).getUserObject();
				TmNode b = (TmNode) (n2).getUserObject();
				String typeA = getType(a.node), typeB = getType(b.node);
				
				// When comparing unequal types, the ordering is:
				// class, field, methods.
				if (!typeA.equals(typeB)) {
					if (typeA.equals("ClassOrInterfaceDeclaration")) {
						return -1;
					}

					if (typeB.equals("ClassOrInterfaceDeclaration")) {
						return 1;
					}

					if (typeA.equals("FieldDeclaration")) {
						return -1;
					}

					if (typeB.equals("FieldDeclaration")) {
						return 1;
					}
				}

				if (sortAlphabetical)
					return a.compareTo(b);
				else
					return -1;

			}
		});
		unsortedTree.removeAllChildren();
		Iterator childrenIterator = children.iterator();
		while (childrenIterator.hasNext()) {
			unsortedTree.add((DefaultMutableTreeNode) childrenIterator.next());
		}

	}

	/**
	 * Removes all FieldDeclaration nodes using recursion
	 * 
	 * @param defaultCodeTree
	 *            - The Tree to be modified
	 * 
	 */
	public static void removeFields(DefaultMutableTreeNode defaultCodeTree) {

		for (int i = 0; i < defaultCodeTree.getChildCount(); i++) {
			if (defaultCodeTree.getChildAt(i).isLeaf() == false)
				removeFields((((DefaultMutableTreeNode) defaultCodeTree
						.getChildAt(i))));
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList children = Collections.list(defaultCodeTree.children());
		defaultCodeTree.removeAllChildren();
		@SuppressWarnings("rawtypes")
		Iterator childrenIterator = children.iterator();
		while (childrenIterator.hasNext()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) childrenIterator
					.next();
			if (!(getType((TmNode) node.getUserObject()))
					.equals("FieldDeclaration"))
				defaultCodeTree.add(node);
		}

	}

	public static void main(String[] args) {
		(new TreeMaker()).buildTree();
	}

	/**
	 * Wrapper class to the Node class provided by japa parser. Adds some more
	 * members required within {@link TreeMaker}. The original node object is
	 * retained. Used as userObjects in tree nodes of {@link TreeMaker.codeTree}
	 */
	public class TmNode implements Comparable {
		public Node node;
		public String label;
		public int offset;

		public TmNode(Node node, String label) {
			this.node = node;
			this.label = label;
		}

		public String toString() {
			return label;
		}

		public int compareTo(Object o) {
			TmNode tmNode = null;
			try {
				tmNode = (TmNode) o;

				return label.compareToIgnoreCase(tmNode.label);
			} catch (Exception e) {

			}
			return -1;
		}
	}

}
