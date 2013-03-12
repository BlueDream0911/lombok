/*
 * Copyright (C) 2009-2012 The Project Lombok Authors.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.eclipse.handlers;

import static lombok.eclipse.handlers.EclipseHandlerUtil.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.SneakyThrows;
import lombok.core.AnnotationValues;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.mangosdk.spi.ProviderFor;

/**
 * Handles the {@code lombok.HandleSneakyThrows} annotation for eclipse.
 */
@ProviderFor(EclipseAnnotationHandler.class)
@DeferUntilPostDiet
public class HandleSneakyThrows extends EclipseAnnotationHandler<SneakyThrows> {
	
	private static class DeclaredException {
		final String exceptionName;
		final ASTNode node;
		
		DeclaredException(String exceptionName, ASTNode node) {
			this.exceptionName = exceptionName;
			this.node = node;
		}
	}
	
	@Override public void handle(AnnotationValues<SneakyThrows> annotation, Annotation source, EclipseNode annotationNode) {
		List<String> exceptionNames = annotation.getRawExpressions("value");
		List<DeclaredException> exceptions = new ArrayList<DeclaredException>();
		
		MemberValuePair[] memberValuePairs = source.memberValuePairs();
		if (memberValuePairs == null || memberValuePairs.length == 0) {
			exceptions.add(new DeclaredException("java.lang.Throwable", source));
		} else {
			Expression arrayOrSingle = memberValuePairs[0].value;
			final Expression[] exceptionNameNodes;
			if (arrayOrSingle instanceof ArrayInitializer) {
				exceptionNameNodes = ((ArrayInitializer)arrayOrSingle).expressions;
			} else exceptionNameNodes = new Expression[] { arrayOrSingle };
			
			if (exceptionNames.size() != exceptionNameNodes.length) {
				annotationNode.addError(
						"LOMBOK BUG: The number of exception classes in the annotation isn't the same pre- and post- guessing.");
			}
			
			int idx = 0;
			for (String exceptionName : exceptionNames) {
				if (exceptionName.endsWith(".class")) exceptionName = exceptionName.substring(0, exceptionName.length() - 6);
				exceptions.add(new DeclaredException(exceptionName, exceptionNameNodes[idx++]));
			}
		}
		
		
		EclipseNode owner = annotationNode.up();
		switch (owner.getKind()) {
//		case FIELD:
//			return handleField(annotationNode, (FieldDeclaration)owner.get(), exceptions);
		case METHOD:
			handleMethod(annotationNode, (AbstractMethodDeclaration)owner.get(), exceptions);
			break;
		default:
			annotationNode.addError("@SneakyThrows is legal only on methods and constructors.");
		}
	}
	
//	private boolean handleField(Node annotation, FieldDeclaration field, List<DeclaredException> exceptions) {
//		if (field.initialization == null) {
//			annotation.addError("@SneakyThrows can only be used on fields with an initialization statement.");
//			return true;
//		}
//		
//		Expression expression = field.initialization;
//		Statement[] content = new Statement[] {new Assignment(
//				new SingleNameReference(field.name, 0), expression, 0)};
//		field.initialization = null;
//		
//		for (DeclaredException exception : exceptions) {
//			content = new Statement[] { buildTryCatchBlock(content, exception) };
//		}
//		
//		Block block = new Block(0);
//		block.statements = content;
//		
//		Node typeNode = annotation.up().up();
//		
//		Initializer initializer = new Initializer(block, field.modifiers & Modifier.STATIC);
//		initializer.sourceStart = expression.sourceStart;
//		initializer.sourceEnd = expression.sourceEnd;
//		initializer.declarationSourceStart = expression.sourceStart;
//		initializer.declarationSourceEnd = expression.sourceEnd;
//		injectField(typeNode, initializer);
//		
//		typeNode.rebuild();
//		
//		return true;
//	}
	
	private void handleMethod(EclipseNode annotation, AbstractMethodDeclaration method, List<DeclaredException> exceptions) {
		if (method.isAbstract()) {
			annotation.addError("@SneakyThrows can only be used on concrete methods.");
			return;
		}
		
		if (method.statements == null || method.statements.length == 0) {
			boolean hasConstructorCall = false;
			if (method instanceof ConstructorDeclaration) {
				ExplicitConstructorCall constructorCall = ((ConstructorDeclaration) method).constructorCall;
				hasConstructorCall = constructorCall != null && !constructorCall.isImplicitSuper() && !constructorCall.isImplicitThis();
			}
			
			if (hasConstructorCall) {
				annotation.addWarning("Calls to sibling / super constructors are always excluded from @SneakyThrows; @SneakyThrows has been ignored because there is no other code in this constructor.");
			} else {
				annotation.addWarning("This method or constructor is empty; @SneakyThrows has been ignored.");
			}
			
			return;
		}
		
		Statement[] contents = method.statements;
		
		for (DeclaredException exception : exceptions) {
			contents = new Statement[] { buildTryCatchBlock(contents, exception, exception.node, method) };
		}
		
		method.statements = contents;
		annotation.up().rebuild();
	}
	
	private Statement buildTryCatchBlock(Statement[] contents, DeclaredException exception, ASTNode source, AbstractMethodDeclaration method) {
		int methodStart = method.bodyStart;
		int methodEnd = method.bodyEnd;
		long methodPosEnd = ((long) methodEnd) << 32 | (methodEnd & 0xFFFFFFFFL);
		
		TryStatement tryStatement = new TryStatement();
		setGeneratedBy(tryStatement, source);
		tryStatement.tryBlock = new Block(0);
		
		// Positions for in-method generated nodes are special
		tryStatement.tryBlock.sourceStart = methodStart; tryStatement.tryBlock.sourceEnd = methodEnd;
		
		setGeneratedBy(tryStatement.tryBlock, source);
		tryStatement.tryBlock.statements = contents;
		TypeReference typeReference;
		if (exception.exceptionName.indexOf('.') == -1) {
			typeReference = new SingleTypeReference(exception.exceptionName.toCharArray(), methodPosEnd);
			typeReference.statementEnd = methodEnd;
		} else {
			String[] x = exception.exceptionName.split("\\.");
			char[][] elems = new char[x.length][];
			long[] poss = new long[x.length];
			Arrays.fill(poss, methodPosEnd);
			for (int i = 0; i < x.length; i++) {
				elems[i] = x[i].trim().toCharArray();
			}
			typeReference = new QualifiedTypeReference(elems, poss);
		}
		setGeneratedBy(typeReference, source);
		
		Argument catchArg = new Argument("$ex".toCharArray(), methodPosEnd, typeReference, Modifier.FINAL);
		setGeneratedBy(catchArg, source);
		catchArg.declarationSourceEnd = catchArg.declarationEnd = catchArg.sourceEnd = methodEnd;
		catchArg.declarationSourceStart = catchArg.modifiersSourceStart = catchArg.sourceStart = methodEnd;
		
		tryStatement.catchArguments = new Argument[] { catchArg };
		
		MessageSend sneakyThrowStatement = new MessageSend();
		setGeneratedBy(sneakyThrowStatement, source);
		sneakyThrowStatement.receiver = new QualifiedNameReference(new char[][] { "lombok".toCharArray(), "Lombok".toCharArray() }, new long[2], methodEnd, methodEnd);
		setGeneratedBy(sneakyThrowStatement.receiver, source);
		sneakyThrowStatement.receiver.statementEnd = methodEnd;
		sneakyThrowStatement.selector = "sneakyThrow".toCharArray();
		SingleNameReference exRef = new SingleNameReference("$ex".toCharArray(), methodPosEnd);
		setGeneratedBy(exRef, source);
		exRef.statementEnd = methodEnd;
		sneakyThrowStatement.arguments = new Expression[] { exRef };
		
		// This is the magic fix for rendering issues
		// In org.eclipse.jdt.core.dom.ASTConverter#convert(org.eclipse.jdt.internal.compiler.ast.MessageSend)
		// a new SimpleName is created and the setSourceRange should receive -1, 0. That's why we provide -2L :-)
		sneakyThrowStatement.nameSourcePosition = -2L;
		
		sneakyThrowStatement.sourceStart = methodEnd;
		sneakyThrowStatement.sourceEnd = sneakyThrowStatement.statementEnd = methodEnd;
		
		Statement rethrowStatement = new ThrowStatement(sneakyThrowStatement, methodEnd, methodEnd);
		setGeneratedBy(rethrowStatement, source);
		
		Block block = new Block(0);
		block.sourceStart = methodEnd;
		block.sourceEnd = methodEnd;
		setGeneratedBy(block, source);
		block.statements = new Statement[] { rethrowStatement };
		
		tryStatement.catchBlocks = new Block[] { block };
		
		// Positions for in-method generated nodes are special
		tryStatement.sourceStart = method.bodyStart;
		tryStatement.sourceEnd = method.bodyEnd;
		
		return tryStatement;
	}
}
