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
 * @modified 23-02-2012			##date##
 * @version 0.1.2.1 (beta)		##version##
 */
package sketchoutline.tool;

import japa.parser.ast.BlockComment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
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
	public void visit(AnnotationDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(AnnotationMemberDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ArrayAccessExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ArrayCreationExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ArrayInitializerExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(AssertStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(AssignExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(BinaryExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(BlockComment n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(BlockStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(BooleanLiteralExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(BreakStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(CastExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(CatchClause n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(CharLiteralExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ClassExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		parent.processNode(arg, n);
		// n.accept(new Visitor(source), null);
		super.visit(n, arg);
	}

	@Override
	public void visit(ClassOrInterfaceType n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(CompilationUnit n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ConditionalExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ConstructorDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ContinueStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(DoStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(DoubleLiteralExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EmptyMemberDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EmptyStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EmptyTypeDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EnclosedExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EnumConstantDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EnumDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ExpressionStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldAccessExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ForeachStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ForStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(IfStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ImportDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(InitializerDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(InstanceOfExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(IntegerLiteralExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(IntegerLiteralMinValueExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(JavadocComment n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(LabeledStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(LineComment n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(LongLiteralExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(LongLiteralMinValueExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(MarkerAnnotationExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(MemberValuePair n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(MethodCallExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(MethodDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(NameExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(NormalAnnotationExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(NullLiteralExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ObjectCreationExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(PackageDeclaration n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(Parameter n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(PrimitiveType n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(QualifiedNameExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ReferenceType n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ReturnStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(SingleMemberAnnotationExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(StringLiteralExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(SuperExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(SwitchEntryStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(SwitchStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(SynchronizedStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ThisExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(ThrowStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(TryStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(TypeDeclarationStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(TypeParameter n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(UnaryExpr n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(VariableDeclarationExpr n, Object arg) {
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
	public void visit(VoidType n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(WhileStmt n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

	@Override
	public void visit(WildcardType n, Object arg) {
		parent.processNode(arg, n);
		super.visit(n, arg);
	}

}
