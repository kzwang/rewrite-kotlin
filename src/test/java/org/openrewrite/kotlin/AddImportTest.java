/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.kotlin;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openrewrite.Issue;
import org.openrewrite.java.AddImport;
import org.openrewrite.java.ChangeType;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.test.RewriteTest.toRecipe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openrewrite.kotlin.Assertions.kotlin;

public class AddImportTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(toRecipe(() -> new AddImport<>("a.b.Target", null, false)));
    }

    @Test
    void addImport() {
        rewriteRun(
          kotlin(
            """
            package a.b
            class Original
            """),
          kotlin(
            """
            package a.b
            class Target
            """),
          kotlin(
            """
            import a.b.Original
            
            class A {
                val type : Original = Original()
            }
            """,
            """
            import a.b.Original
            import a.b.Target
            
            class A {
                val type : Original = Original()
            }
            """
          )
        );
    }

    @Test
    void addStaticImport() {
        rewriteRun(
          spec -> spec.recipe(toRecipe(() -> new AddImport<>("a.b.Target", "method", false))),
          kotlin(
            """
            package a.b
            class Original
            """
          ),
          kotlin(
            """
            package a.b
            class Target {
                inline fun method() {}
            }
            """
          ),
          kotlin(
            """
            import a.b.Original
            
            class A {
                val type : Original = Original()
            }
            """,
            """
            import a.b.Original
            
            import a.b.method
            
            class A {
                val type : Original = Original()
            }
            """
          )
        );
    }
}
