import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class ExampleClassMethodVisitor(
    apiVersion: Int,
    cv: ClassVisitor,
    private val newMethodName: String
) : ClassVisitor(apiVersion, cv) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name == "someMethod") {
            return super.visitMethod(access, newMethodName, descriptor, signature, exceptions)
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}
