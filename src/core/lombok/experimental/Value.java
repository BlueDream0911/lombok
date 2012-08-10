/*
 * Copyright (C) 2012 The Project Lombok Authors.
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
package lombok.experimental;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates a lot of code which fits with a class that is a representation of an immutable entity.
 * Specifically, it generates:<ul>
 * <li>Getters for all fields
 * <li>toString method
 * <li>hashCode and equals implementations that check all non-transient fields.
 * <li>Generates withers for all fields (except final fields that are initialized in the field declaration itself)
 * <li>Generates a constructor for each argument
 * <li>Adds {@code private} and {@code final} to each field.
 * <li>Makes the class itself final.
 * </ul>
 * 
 * In other words, {@code @Value} is a shorthand for:<br />
 * {@code final @Getter @Wither @FieldDefaults(makeFinal=true,level=AccessLevel.PRIVATE) @EqualsAndHashCode @ToString @AllArgsConstructor}.
 * <p>
 * If any method to be generated already exists (in name and parameter c ount - the return type or parameter types are not relevant), then
 * that method will not be generated by the Value annotation.
 * <p>
 * The generated constructor will have 1 parameter for each field. The generated toString will print all fields,
 * while the generated hashCode and equals take into account all non-transient fields.<br>
 * Static fields are skipped (no getter or setter, and they are not included in toString, equals, hashCode, or the constructor).
 * <p>
 * {@code toString}, {@code equals}, and {@code hashCode} use the deepX variants in the
 * {@code java.util.Arrays} utility class. Therefore, if your class has arrays that contain themselves,
 * these methods will just loop endlessly until the inevitable {@code StackOverflowError}. This behaviour
 * is no different from {@code java.util.ArrayList}, though.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Value {
	/**
	 * If you specify a static constructor name, then the generated constructor will be private, and
	 * instead a static factory method is created that other classes can use to create instances.
	 * We suggest the name: "of", like so:
	 * 
	 * <pre>
	 *     public @Data(staticConstructor = "of") class Point { final int x, y; }
	 * </pre>
	 * 
	 * Default: No static constructor, instead the normal constructor is public.
	 */
	String staticConstructor() default "";
}
