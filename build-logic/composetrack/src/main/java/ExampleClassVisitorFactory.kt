import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

/**
 * This is an example of a class visitor factory. In this recipe it is used in the transformClassesWith API to be
 * performed on the classes specified in the instrumentation scope.
 */
abstract class ExampleClassVisitorFactory :
    AsmClassVisitorFactory<ExampleClassVisitorFactory.ExampleParams> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        // This will transform the method names of the classes
        return ExampleClassMethodVisitor(
            instrumentationContext.apiVersion.get(),
            nextClassVisitor,
            parameters.get().newMethodName.get()
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className.contains("SomeSource")
    }

    interface ExampleParams : InstrumentationParameters {
        @get:Input
        val newMethodName: Property<String>
    }
}
