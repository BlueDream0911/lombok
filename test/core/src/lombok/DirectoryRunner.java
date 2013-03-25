/*
 * Copyright (C) 2009-2013 The Project Lombok Authors.
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
package lombok;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import com.sun.tools.javac.main.JavaCompiler;

public class DirectoryRunner extends Runner {
	public enum Compiler {
		DELOMBOK {
			@Override public int getVersion() {
				Matcher m = VERSION_PARSER.matcher(JavaCompiler.version());
				if (m.matches()) {
					int major = Integer.parseInt(m.group(1));
					int minor = Integer.parseInt(m.group(2));
					if (major == 1) return minor;
				}
				
				return 6;
			}
		}, 
		JAVAC {
			@Override public int getVersion() {
				return DELOMBOK.getVersion();
			}
		},
		ECJ {
			@Override public int getVersion() {
				return 6;
			}
		};
		
		private static final Pattern VERSION_PARSER = Pattern.compile("^(\\d+)\\.(\\d+).*$");
		public abstract int getVersion();
	}
	
	public static abstract class TestParams {
		public abstract Compiler getCompiler();
		public abstract boolean printErrors();
		public abstract File getBeforeDirectory();
		public abstract File getAfterDirectory();
		public abstract File getMessagesDirectory();
		/** Version of the JDK dialect that the compiler can understand; for example, if you return '7', you should know what try-with-resources is. */
		public int getVersion() {
			return getCompiler().getVersion();
		}
		
		public boolean accept(File file) {
			return true;
		}
		
		private static final Pattern P1 = Pattern.compile("^(\\d+)$");
		private static final Pattern P2 = Pattern.compile("^\\:(\\d+)$");
		private static final Pattern P3 = Pattern.compile("^(\\d+):$");
		private static final Pattern P4 = Pattern.compile("^(\\d+):(\\d+)$");
		
		public boolean shouldIgnoreBasedOnVersion(String firstLine) {
			int thisVersion = getVersion();
			if (!firstLine.startsWith("//version ")) return false;
			
			String spec = firstLine.substring("//version ".length());
			
			/* Single version: '5' */ {
				Matcher m = P1.matcher(spec);
				if (m.matches()) return Integer.parseInt(m.group(1)) != thisVersion;
			}
			
			/* Upper bound: ':5' (inclusive) */ {
				Matcher m = P2.matcher(spec);
				if (m.matches()) return Integer.parseInt(m.group(1)) < thisVersion;
			}
			
			/* Lower bound '5:' (inclusive) */ {
				Matcher m = P3.matcher(spec);
				if (m.matches()) return Integer.parseInt(m.group(1)) > thisVersion;
			}
			
			/* Range '7:8' (inclusive) */ {
				Matcher m = P4.matcher(spec);
				if (m.matches()) {
					if (Integer.parseInt(m.group(1)) < thisVersion) return true;
					if (Integer.parseInt(m.group(2)) > thisVersion) return true;
					return false;
				}
			}
			
			throw new IllegalArgumentException("Version validity spec not valid: " + spec);
		}
	}
	
	private static final FileFilter JAVA_FILE_FILTER = new FileFilter() {
		@Override public boolean accept(File file) {
			return file.isFile() && file.getName().endsWith(".java");
		}
	};
	
	private final Description description;
	private final Map<String, Description> tests = new TreeMap<String, Description>();
	private final Throwable failure;
	private final TestParams params;
	
	public DirectoryRunner(Class<?> testClass) throws Exception {
		description = Description.createSuiteDescription(testClass);
		
		this.params = (TestParams) testClass.newInstance();
		
		Throwable error = null;
		try {
			addTests(testClass);
		}
		catch (Throwable t) {
			error = t;
		}
		this.failure = error;
	}
	
	private void addTests(Class<?> testClass) throws Exception {
		for (File file : params.getBeforeDirectory().listFiles(JAVA_FILE_FILTER)) {
			if (!params.accept(file)) continue;
			Description testDescription = Description.createTestDescription(testClass, file.getName());
			description.addChild(testDescription);
			tests.put(file.getName(), testDescription);
		}
	}
	
	@Override
	public Description getDescription() {
		return description;
	}
	
	@Override
	public void run(RunNotifier notifier) {
		if (failure != null) {
			notifier.fireTestStarted(description);
			notifier.fireTestFailure(new Failure(description, failure));
			notifier.fireTestFinished(description);
			return;
		}
		
		for (Map.Entry<String, Description> entry : tests.entrySet()) {
			Description testDescription = entry.getValue();
			notifier.fireTestStarted(testDescription);
			try {
				if (!runTest(entry.getKey())) {
					notifier.fireTestIgnored(testDescription);
				}
			} catch (Throwable t) {
				notifier.fireTestFailure(new Failure(testDescription, t));
			}
			notifier.fireTestFinished(testDescription);
		}
	}
	
	private boolean runTest(String fileName) throws Throwable {
		File file = new File(params.getBeforeDirectory(), fileName);
		if (mustIgnore(file)) {
			return false;
		}
		switch (params.getCompiler()) {
		case DELOMBOK:
			return new RunTestsViaDelombok().compareFile(params, file);
		case ECJ:
			return new RunTestsViaEcj().compareFile(params, file);
		default:
		case JAVAC:
			throw new UnsupportedOperationException();
		}
	}
	
	private boolean mustIgnore(File file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		reader.close();
		return line != null && (line.startsWith("//ignore") || params.shouldIgnoreBasedOnVersion(line));
	}
}
