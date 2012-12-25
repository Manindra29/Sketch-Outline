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
 * @modified 25-12-2012			##date##
 * @version 0.1.7 (beta)		##version##
 */
package sketchoutline.tool;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * Implements a VoidVisitor which visits every node and emits them. Modified and used from
 * japa.parser.ast.test.TestAdapters
 * 
 * @author Julio Vilmar Gesser
 */
public class Visitor extends VoidVisitorAdapter<Object> {

	protected final String source;
	private TreeMaker parent;

	public Visitor(String source, TreeMaker parent) {
		this.source = source;
		this.parent = parent;
	}


	@Override
	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		parent.processNode(arg, n);
		// n.accept(new Visitor(source), null);
		super.visit(n, arg);
	}

	@Override
	public void visit(CompilationUnit n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}
	
	@Override
	public void visit(ConstructorDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EnumDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}
	
	@Override
	public void visit(ImportDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}
	
	@Override
	public void visit(VariableDeclarator n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(VariableDeclaratorId n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}
	
	@Override
	public void visit(MethodDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}
}
